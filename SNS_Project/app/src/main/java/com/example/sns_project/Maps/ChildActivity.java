package com.example.sns_project.Maps;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.sns_project.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ChildActivity extends AppCompatActivity {
    SupportMapFragment mapFragment;
    GoogleMap map;
    MarkerOptions houseMarker;
    String abc = "tel:010-8671-7273";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child);


        final DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        findViewById(R.id.ChildImageMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                Log.d("Map", "지도 준비됨.");

                map = googleMap;
            }
        });
        try {
            MapsInitializer.initialize(this);
        } catch (Exception e) {
            e.printStackTrace();
        }


        Button callbutton = findViewById(R.id.callbutton);
        callbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse(abc));

                try {
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Button houseButton = findViewById(R.id.houseButton);
        houseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                houseLocation();
            }
        });
        //  AutoPermissions.Companion.loadAllPermissions(this, 101);
    }

    private void houseLocation() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        long minTime = 10000;
        float minDistance = 0;

        try {
            manager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    minTime,
                    minDistance,
                    new LocationListener() {
                        @Override
                        public void onLocationChanged(@NonNull Location location) {
                            showCurrentLocation(location);
                        }

                        @Override
                        public void onProviderEnabled(@NonNull String provider) {

                        }

                        @Override
                        public void onProviderDisabled(@NonNull String provider) {

                        }
                    }
            );
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void showCurrentLocation(Location location) {
        LatLng curPoint = new LatLng(37.613259, 127.030102);

        try {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 15));

            showMyLocationMarker(location);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showMyLocationMarker(Location location) {
        if (houseMarker == null) {
            houseMarker = new MarkerOptions();
            houseMarker.position(new LatLng(37.613259, 127.030102));
            houseMarker.title(" 내 위치\n");
            houseMarker.snippet(" GSP로 확인한 위치");
            houseMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.mylocation));
            map.addMarker(houseMarker);
        } else {
            houseMarker.position(new LatLng(location.getLatitude(), location.getLongitude()));
        }
    }


}
