package com.example.sns_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sns_project.Posts.WritePostActivity;
import com.example.sns_project.R;
import com.example.sns_project.SignLogins.LoginActivity;
import com.example.sns_project.SignLogins.MemberInitActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends BasicActivity {
    private static final String TAG = "MainActivity";
    TextView CurrentEmail, CurrentName, CurrentPhone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 액션바 제거
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

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
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            CurrentEmail = (TextView)findViewById(R.id.EmailText);
                            CurrentEmail.setText("접속한 이메일 : "+FirebaseAuth.getInstance().getCurrentUser().getEmail());
                            CurrentName = (TextView) findViewById(R.id.NameText);
                            CurrentName.setText("접속한 이름 : " + document.getData().get("name"));
                            CurrentPhone = (TextView) findViewById(R.id.PNumText);
                            CurrentPhone.setText("접속한 이름 : " + document.getData().get("phoneNumber"));
                        } else {
                            Log.d(TAG, "No such document");
                            finish();
                            myStartActivity(MemberInitActivity.class);
                        }
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });





        // 버튼 리스너
        findViewById(R.id.logoutButton).setOnClickListener(onClickListener);
        findViewById(R.id.floatingActionButton).setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.logoutButton:
                    FirebaseAuth.getInstance().signOut();
                    myStartActivity(LoginActivity.class);
                    break;
                case R.id.floatingActionButton:
                    myStartActivity(WritePostActivity.class);
                    break;

            }
        }
    };

    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }
}
