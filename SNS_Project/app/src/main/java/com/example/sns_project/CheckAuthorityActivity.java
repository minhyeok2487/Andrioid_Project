package com.example.sns_project;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sns_project.Posts.PostInfo;
import com.example.sns_project.Posts.WritePostActivity;
import com.example.sns_project.SignLogins.LoginActivity;
import com.example.sns_project.SignLogins.MemberInitActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

public class CheckAuthorityActivity extends BasicActivity {
    private static final String TAG = "CheckAuthorityActivity";
    private static DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkauthority);
        final String[] CurrentName = new String[1];
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // 회원정보가 존재하는지 확인
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(document != null){
                        if (document.exists()) {
                            mDatabase.child("users").child(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (!task.isSuccessful()) {
                                    }
                                    else {
                                        CurrentName[0] = (String) document.getData().get("name");
                                        //데이터 권한요청이 들어오면 화면이 보임
                                        mDatabase.child("Authority").child(CurrentName[0]).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                String str = dataSnapshot.getValue().toString();
                                                String[] array = str.split("/");
                                                if(dataSnapshot.getValue().toString().equals("권한 없음") ){

                                                } else if(array[0].equals("권한요청보냄")){
                                                    DialogClick(array[1]);
                                                }
                                                else{
                                                }
                                            }
                                            @Override
                                            public void onCancelled(DatabaseError error) {
                                                // Failed to read value
                                                Log.w(TAG, "Failed to read value.", error.toException());
                                            }
                                        });
                                    }
                                }
                            });

                        } else {
                            Log.d(TAG, "No such document");
                        }
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        // 버튼 리스너
        findViewById(R.id.AuthoritySendButton).setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.AuthoritySendButton:
                    EditText ParentId = (EditText)findViewById(R.id.AuthorityParentId);
                    SendData(ParentId.getText().toString());
                    break;
            }
        }
    };

    private void SendData(String ParentId){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final String[] ChildName = new String[1];

        DocumentReference docRefChild = db.collection("users").document(user.getUid());
        docRefChild.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(document != null){
                        if (document.exists()) {
                            ChildName[0] = (String) document.getData().get("name");
                            //ChildEmail = user.getEmail();
                        } else {
                            Log.d("자식데이터", "No such document");
                        }
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        db.collection("users")
                .whereEqualTo("email", ParentId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("부모 이름",document.getData().get("name").toString());
                                Log.d("부모 번호",document.getData().get("phoneNumber").toString());
                                mDatabase.child("Authority").child(document.getData().get("name").toString()).setValue("권한요청보냄/"+ChildName[0]);
                            }
                        } else {
                            Log.d("부모데이터", "No such document");
                        }
                    }
                });
    }

    private void Okay(String email){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(document != null){
                        if (document.exists()) {
                            mDatabase.child("Authority").child(document.getData().get("name").toString()).setValue("자식 데이터 / "+email);
                        } else {
                            Log.d("데이터", "No such document");
                        }
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public void DialogClick(String name){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("수락요청").setMessage(name+"으로 부터의 등록 요청을 수락하시겠습니까?");
        builder.setPositiveButton("수락", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Okay("자식 이메일 받아야함");
                Toast.makeText(getApplicationContext(), "Yeah!!",Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("거절", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getApplicationContext(), "Try again",Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    public void writeNewUser(String userId, String name, String email, String message) {
        MainActivity.User user = new MainActivity.User(name, email, message, new Date());

        mDatabase.child("users").child(userId).child(name).child(user.getDate().toString()).setValue(message);
    }

}
