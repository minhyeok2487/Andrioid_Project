package com.example.sns_project;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.Random;

public class LocationService extends Service {
    private static final String TAG = "LocationService";
    private IBinder mBinder = new LocalBinder();

    private double latitude;
    private double longitude;

    public class LocalBinder extends Binder {
        public LocationService getService() {
            return LocationService.this;
        }
    }

    private static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    private LocationCallback mLocationCallback = new LocationCallback() {
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult != null && locationResult.getLastLocation() != null) {
                //getLastLocation=유저가 요청할때의 최신 위치를 기준으로 정보 표현
                latitude = locationResult.getLastLocation().getLatitude();
                longitude = locationResult.getLastLocation().getLongitude();
                //Toast.makeText(getApplicationContext(), latitude + "," + longitude, Toast.LENGTH_SHORT).show();

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference docRefParrent = db.collection("Users").document(user.getUid());

                docRefParrent.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {
                                if (document.exists()) {
                                    //내 좌표 업데이트
                                    mDatabase.child("Users").child(user.getUid()).child("내 좌표").child("위도").setValue(latitude);
                                    mDatabase.child("Users").child(user.getUid()).child("내 좌표").child("경도").setValue(longitude);

                                    mDatabase.child("Users").child(user.getUid()).child("연결된 코드").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                                            if (!task.isSuccessful()) {
                                                Log.e("firebase", "Error getting data", task.getException());
                                            } else {
                                                db.collection("Users")
                                                        .whereEqualTo("uidCode", String.valueOf(task.getResult().getValue()))
                                                        .get()
                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                if (task.isSuccessful()) {
                                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                                        mDatabase.child("Users")
                                                                                .child(document.getData().get("uidCode").toString())
                                                                                .child("연결된 코드의 좌표")
                                                                                .child("위도").setValue(latitude);
                                                                        mDatabase.child("Users")
                                                                                .child(document.getData().get("uidCode").toString())
                                                                                .child("연결된 코드의 좌표")
                                                                                .child("경도").setValue(longitude);
                                                                    }
                                                                } else {
                                                                    Log.d("부모데이터", "No such document");
                                                                }
                                                            }
                                                        });

                                            }
                                        }
                                    });
                                    //연결된 사람한테 업데이트


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
        }
    };


    public LocationService() {
    }

    public void onCreate() {
//        Log.d(TAG,"onCreate() 호출 ");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand() 호출됨 ");
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                if (action.equals(Action.START_LOCATION_SERVICE)) {
                    startLocationService();
                } else if (action.equals(Action.STOP_LOCATION_SERVICE)) {
                    stopLocationService();
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startLocationService() {
        Log.d(TAG, "마바사아");

        String channelId = "location_notification_channel";
        //LocationManager 객체 참조하기
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, resultIntent, PendingIntent.FLAG_IMMUTABLE);
        //알림창
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId);
//        builder.setSmallIcon(R.mipmap.ic_launcher);
//        builder.setContentTitle("Location Service");
//        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
//        builder.setContentText("Running");
//        builder.setContentIntent(pendingIntent);
//        builder.setAutoCancel(false);
//        builder.setPriority(NotificationCompat.PRIORITY_MAX);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager != null && notificationManager.getNotificationChannel(channelId) == null) {
                NotificationChannel notificationChannel = new NotificationChannel(channelId, "Location Service", NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setDescription("This channel is used by location service");
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
        //위치 설정 변경
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //위치 업데이트
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, mLocationCallback, Looper.getMainLooper());
//        startForeground(Action.LOCATION_SERVICE_ID, builder.build());
    }

    private void stopLocationService() {
        Log.d(TAG, "가나다라");
        LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(mLocationCallback);
        stopSelf();
    }

    private void SendData(String ParrentId) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRefParrent = db.collection("users").document(ParrentId);
        DocumentReference docRefChild = db.collection("users").document(user.getUid());


        docRefParrent.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        if (document.exists()) {
                            Log.d("부모 이름", document.getData().get("name").toString());
                            Log.d("부모 번호", document.getData().get("phoneNumber").toString());
                            writeNewUser(ParrentId, "시험이름", "시험이메일", "테스트메세지입니다1");

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

    public void writeNewUser(String userId, String name, String email, String message) {
        MainActivity.User user = new MainActivity.User(name, email, message, new Date());

        mDatabase.child("users").child(userId).child(name).child(user.getDate().toString()).setValue(message);
    }

    public void write(String userId, String lat) {
        mDatabase.child("maps").setValue(lat);
    }

}
