package com.example.sns_project.SignLogins;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.sns_project.BasicActivity;
import com.example.sns_project.CameraGallerys.CameraActivity;
import com.example.sns_project.CameraGallerys.GalleryActivity;
import com.example.sns_project.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;
import java.util.regex.Pattern;


public class MemberInitActivity extends BasicActivity {
    private static final String TAG = "MemberInitActivity";
    private ImageView profileImageVIew;
    private String profilePath;
    private FirebaseUser user;
    private RelativeLayout loaderLayout;
    private CardView cardView;
    private boolean flag = true;
    private String dbName, dbPhoneNumber, dbBirthDay, dbAddress, dbImage, dbEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_init);

        profileImageVIew = findViewById(R.id.profileImageView);
        profileImageVIew.setOnClickListener(onClickListener);
        cardView = findViewById(R.id.buttonsCardView);

        loaderLayout = findViewById(R.id.loaderLayout);
        findViewById(R.id.checkButton).setOnClickListener(onClickListener);
        findViewById(R.id.picture).setOnClickListener(onClickListener);
        findViewById(R.id.gallery).setOnClickListener(onClickListener);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Users").document(user.getUid());

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        if (document.exists()) {
                            flag = false;
                            // 개인정보가 있다면 정보 출력
                            Map<String, Object> hm = document.getData();
                            dbName = hm.get("name").toString();
                            dbPhoneNumber = hm.get("phoneNumber").toString();
                            dbBirthDay = hm.get("birthDay").toString();
                            dbAddress = hm.get("address").toString();
                            if(hm.get("photoUrl").toString() != null){
                                dbImage = hm.get("photoUrl").toString();
                                Glide.with(MemberInitActivity.this).load(dbImage).centerCrop().override(500).into(profileImageVIew);
                            }
                            EditText etName = (EditText) findViewById(R.id.nameEditText);
                            EditText etPhoneNumber = (EditText) findViewById(R.id.phoneNumberEditText);
                            EditText etBirthDay = (EditText) findViewById(R.id.birthDayEditText);
                            EditText etAddress = (EditText) findViewById(R.id.addressEditText);

                            etName.setText(dbName);
                            etPhoneNumber.setText(dbPhoneNumber);
                            etBirthDay.setText(dbBirthDay);
                            etAddress.setText(dbAddress);



                            if (hm.get("email") != null) {
                                dbEmail = hm.get("email").toString();
                            }

                        }
                    }
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0: {
                if (resultCode == Activity.RESULT_OK) {
                    profilePath = data.getStringExtra("profilePath");
                    Glide.with(this).load(profilePath).centerCrop().override(500).into(profileImageVIew);
                }
                break;
            }
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.checkButton:
                    profileUpdate();
                    break;
                case R.id.profileImageView:
                    if (cardView.getVisibility() == View.VISIBLE) {
                        cardView.setVisibility(View.GONE);
                    } else {
                        cardView.setVisibility(View.VISIBLE);
                    }
                    break;
                case R.id.picture:
                    myStartActivity(CameraActivity.class);
                    cardView.setVisibility(View.GONE);
                    break;
                case R.id.gallery:
                    if (ContextCompat.checkSelfPermission(MemberInitActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MemberInitActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                1);
                        if (ActivityCompat.shouldShowRequestPermissionRationale(MemberInitActivity.this,
                                Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        } else {
                            startToast("권한을 허용해 주세요");
                        }
                    } else {
                        myStartGalleryActivity(GalleryActivity.class, "image", 0);
                        cardView.setVisibility(View.GONE);
                    }
                    break;

            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    myStartActivity(GalleryActivity.class);
                } else {
                    startToast("권한을 허용해 주세요");
                }
            }
        }
    }


    private void profileUpdate() {
        final String name = ((EditText) findViewById(R.id.nameEditText)).getText().toString();
        final String phoneNumber = ((EditText) findViewById(R.id.phoneNumberEditText)).getText().toString();
        final String birthDay = ((EditText) findViewById(R.id.birthDayEditText)).getText().toString();
        final String address = ((EditText) findViewById(R.id.addressEditText)).getText().toString();

        if (name.length() > 0) {
            if (Pattern.matches("^[0-9]*$", phoneNumber)) {
                if (phoneNumber.length() == 11) {
                    if (Pattern.matches("^[0-9]*$", birthDay)) {
                        if (birthDay.length() == 8) {
                            if (address.length() > 0) {
                                loaderLayout.setVisibility(View.VISIBLE);
                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                StorageReference storageRef = storage.getReference();
                                user = FirebaseAuth.getInstance().getCurrentUser();
                                final StorageReference mountainImagesRef = storageRef.child("Users/" + user.getUid() + "/profileImage.jpg");

                                if (profilePath == null) {
                                    MemberInfo memberInfo = new MemberInfo(user.getUid(), user.getEmail(), name, phoneNumber, birthDay, address);
                                    uploader(memberInfo);
                                } else {
                                    try {
                                        InputStream stream = new FileInputStream(new File(profilePath));
                                        UploadTask uploadTask = mountainImagesRef.putStream(stream);
                                        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                            @Override
                                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                                if (!task.isSuccessful()) {
                                                    throw task.getException();
                                                }
                                                return mountainImagesRef.getDownloadUrl();
                                            }
                                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Uri> task) {
                                                if (task.isSuccessful()) {
                                                    Uri downloadUri = task.getResult();
                                                    MemberInfo memberInfo = new MemberInfo(user.getUid(), user.getEmail(), name, phoneNumber, birthDay, address, downloadUri.toString());
                                                    uploader(memberInfo);
                                                } else {
                                                    startToast("회원정보를 보내는데 실패하였습니다.");
                                                }
                                            }
                                        });
                                    } catch (FileNotFoundException e) {
                                        Log.e(TAG, "파일이 존재하지 않습니다.");
                                    }
                                }
                            } else {
                                startToast("주소를 입력해주세요");
                            }
                        } else {
                            startToast("생년월일을 입력해주세요 (ex. 19950101)");
                        }
                    } else {
                        startToast("생년월일은 숫자만 입력 가능합니다 (ex. 19950101)");
                    }
                } else {
                    startToast("휴대폰 번호를 입력해주세요 (-제외)");
                }
            } else {
                startToast("휴대폰 번호는 숫자만 입력해주세요 (-제외)");
            }
        } else {
            startToast("이름을 입력해주세요");
        }
    }

    private void uploader(MemberInfo memberInfo) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(user.getUid()).set(memberInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        SendData();
                        startToast("회원정보 등록을 성공하였습니다.");
                        loaderLayout.setVisibility(View.GONE);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        startToast("회원정보 등록에 실패하였습니다.");
                        loaderLayout.setVisibility(View.GONE);
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    // 실시간 데이터베이스에 필요값 부여
    private void SendData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        DocumentReference docRef = db.collection("Users").document(user.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        if (document.exists()) {
                            mDatabase.child("Users").child(user.getUid()).child("권한").setValue("권한 없음");
                            mDatabase.child("Users").child(user.getUid()).child("사용자 이름").setValue(document.getData().get("name").toString());
                            mDatabase.child("Users").child(user.getUid()).child("사용자 이메일").setValue(user.getEmail());
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


    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivityForResult(intent, 0);
    }

    private void myStartGalleryActivity(Class c, String media, int requestCode) {
        Intent intent = new Intent(this, c);
        intent.putExtra("media", media);
        startActivityForResult(intent, requestCode);
    }
}