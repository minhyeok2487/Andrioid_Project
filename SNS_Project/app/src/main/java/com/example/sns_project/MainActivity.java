package com.example.sns_project;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.sns_project.Maps.ChildActivity;
import com.example.sns_project.Maps.ParentsActivity;
import com.example.sns_project.SignLogins.LoginActivity;
import com.example.sns_project.SignLogins.MemberInitActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class MainActivity extends BasicActivity {
    private static final String TAG = "MainActivity";
    private static TextView CurrentEmail, CurrentName, CurrentPhone, childTextview;
    private static DatabaseReference mDatabase;
    private static String ChildName, ChildEmail;
    private LinearLayout StartStopButtonsLayout, MapsButtonsLayout;
    private static final int REQUEST_CODE = 1;
    Button Startbutton;
    Button Stopbutton, Cbutton, Pbutton;
    Button buttonTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ??????????????? ??????????????? ??????
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        DocumentReference docRef = db.collection("Users").document(user.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(document != null){
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            CurrentEmail = (TextView)findViewById(R.id.EmailText);
                            CurrentEmail.setText("????????? ????????? : "+user.getEmail());
                            CurrentName = (TextView) findViewById(R.id.NameText);
                            CurrentName.setText("????????? ?????? : " + document.getData().get("name"));
                            CurrentPhone = (TextView) findViewById(R.id.PNumText);
                            CurrentPhone.setText("????????? ?????? : " + document.getData().get("phoneNumber"));
                            mDatabase.child("Users").child(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (!task.isSuccessful()) {
                                        Log.e("firebase", "Error getting data", task.getException());
                                    }
                                    else {
                                        Log.d("firebase", String.valueOf(task.getResult().getValue()));
                                        childTextview = (TextView)findViewById(R.id.childTextview);
                                        childTextview.setText(String.valueOf(task.getResult().getValue()));

                                    }
                                }
                            });
                            //????????? ????????? ?????? ?????? ?????????
                            mDatabase.child("Users").child(user.getUid()).child("??????").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String data = String.valueOf(dataSnapshot.getValue());
                                    String subdata = data.substring(data.length()-3,data.length());
                                    StartStopButtonsLayout = findViewById(R.id.StartStopButtonsLayout);
                                    if(subdata.equals("?????????")){
                                        StartStopButtonsLayout.setVisibility(View.VISIBLE);
                                        Toast.makeText(MainActivity.this, "?????? ??????", Toast.LENGTH_SHORT).show();
                                    } else {
                                        StartStopButtonsLayout.setVisibility(View.GONE);
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError error) {
                                    // Failed to read value
                                    Log.w(TAG, "Failed to read value.", error.toException());
                                }
                            });

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

        //????????? ????????? ????????????
//        db.collection("posts")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            ArrayList<PostInfo> postList = new ArrayList<>();
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.d(TAG, document.getId() + " => " + document.getData());
//                                postList.add(new PostInfo(
//                                        document.getData().get("title").toString(),
//                                        (ArrayList<String>) document.getData().get("contents"),
//                                        document.getData().get("publisher").toString(),
//                                        new Date(document.getDate("createdAt").getTime())));
//                            }
//
//                            RecyclerView recyclerView = findViewById(R.id.recyclerView);
//                            recyclerView.setHasFixedSize(true);
//                            recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
//
//                            RecyclerView.Adapter mAdapter = new MainAdapter(MainActivity.this, postList);
//                            recyclerView.setAdapter(mAdapter);
//                        } else {
//                            Log.d(TAG, "Error getting documents: ", task.getException());
//                        }
//                    }
//                });


        // ?????? ?????????
        findViewById(R.id.logoutButton).setOnClickListener(onClickListener);
        findViewById(R.id.CheckAuthority).setOnClickListener(onClickListener);
        findViewById(R.id.updateButton).setOnClickListener(onClickListener);
        findViewById(R.id.buttonTest).setOnClickListener(onClickListener);


        mDatabase.child("Users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                LinearLayout buttonTestLayout = findViewById(R.id.buttonTestLayout);
                if(dataSnapshot.child("????????? ????????? ??????").getValue() != null){
                    buttonTestLayout.setVisibility(View.VISIBLE);
                    Toast.makeText(MainActivity.this, "???????????? ????????? ???????????? ???????????????", Toast.LENGTH_SHORT).show();
                } else {
                    buttonTestLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });


        Startbutton = findViewById(R.id.Startbutton);
        Startbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
                //?????? ????????? ?????? ?????? ??? ???????????? PackageManager.PERMISSION_DENIED??? ????????????,
                //?????? ??????????????? ??????????????? ????????? ???????????? ?????????. ???, IF?????? ?????? ?????? ???????????? ????????? ???????????? ?????????????????? ????????? ??? ????????????.
                if (permissionCheck != PackageManager.PERMISSION_GRANTED) { //?????? ?????? ??????
                    //?????? ?????? ??????
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
                } else {
                    //myStartActivity(MHtest.class);
                    startLocationService();
                }
            }
        });
        Stopbutton = findViewById(R.id.Stopbutton);
        Stopbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopLocationService();
            }
        });

        Cbutton = findViewById(R.id.Cbutton);
        Cbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myStartActivity2(ChildActivity.class);
            }
        });

        Pbutton = findViewById(R.id.Pbutton);
        Pbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myStartActivity2(ParentsActivity.class);
            }
        });

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.logoutButton:
                    FirebaseAuth.getInstance().signOut();
                    myStartActivity(LoginActivity.class);
                    break;
                case R.id.CheckAuthority:
                    myStartActivity2(CheckAuthorityActivity.class);
                    break;
                case R.id.updateButton:
                    myStartActivity(MemberInitActivity.class);
                    break;
                case R.id.buttonTest:
                    myStartActivity(MHtest.class);
                    break;
            }
        }
    };

    private void startLocationService() {
        Intent startIntent = new Intent(getApplicationContext(), LocationService.class);
        startIntent.setAction(Action.START_LOCATION_SERVICE);
        startService(startIntent);
        Toast.makeText(this, "?????? ???????????? ??????", Toast.LENGTH_SHORT).show();
        //MapsButtonsLayout = findViewById(R.id.MapsButtonsLayout);
        //MapsButtonsLayout.setVisibility(View.VISIBLE);
    }

    private void stopLocationService() {
        Intent stopIntent = new Intent(getApplicationContext(), LocationService.class);
        stopIntent.setAction(Action.STOP_LOCATION_SERVICE);
        startService(stopIntent);
        Toast.makeText(this, "?????? ???????????? ??????", Toast.LENGTH_SHORT).show();
    }

    private void nullAction(){

    }

    //grantResults??? ????????? OK??? ?????? ?????? ????????? ????????????.
    //grantResults[0]?????? PackageManager.PERMISSION_GRANTED ?????? ????????????.
    //?????? ????????? ?????????????????? grantResults?????? ????????? ???????????? ???????????? ???????????????.
    //????????? IF?????? ????????? ?????? OK??? ????????? ??? ????????? ????????? ???????????? ELSE?????? ????????? ?????? CANCEL??? ????????? ?????? ????????? ???????????????.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        startLocationService();
                    }
                } else {
                    Toast.makeText(this, "Permission  denied", Toast.LENGTH_SHORT).show();
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }

    private void myStartActivity2(Class c) {
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    public static class User {
        String name;
        String email;
        String message;
        Date date;

        public User(String name, String email, String message, Date date) {
            this.name = name;
            this.email = email;
            this.message = message;
            this.date = date;
        }

        public String getMessage(){
            return message;
        }
        public Date getDate(){return date;}
    }
    public void writeNewUser(String userId, String name, String email, String message) {
        User user = new User(name, email, message, new Date());

        mDatabase.child("Users").child(userId).child(name).child(user.getDate().toString()).setValue(message);
    }

    private void SendData(String ParrentId){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRefParrent = db.collection("Users").document(ParrentId);
        DocumentReference docRefChild = db.collection("Users").document(user.getUid());

        docRefChild.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(document != null){
                        if (document.exists()) {
                            ChildName = (String) document.getData().get("name");
                            ChildEmail = user.getEmail();
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        docRefParrent.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(document != null){
                        if (document.exists()) {
                            Log.d("?????? ??????",document.getData().get("name").toString());
                            Log.d("?????? ??????",document.getData().get("phoneNumber").toString());
                            writeNewUser(ParrentId,ChildName,ChildEmail,"???????????????????????????1");

                        } else {
                            Log.d(TAG, "No such document");
                        }
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    private void RealtimeData(String userId,String name, String email){
        // ?????????????????? ????????? ????????? ?????????
        //FirebaseDatabase database = FirebaseDatabase.getInstance();
        //DatabaseReference myRef = database.getReference("message");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Users").child(userId).child(name).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Log.d("?????????",dataSnapshot.getValue().toString());
                //notificationText = (TextView) findViewById(R.id.notificationText);
                //notificationText.setText(value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }



}
