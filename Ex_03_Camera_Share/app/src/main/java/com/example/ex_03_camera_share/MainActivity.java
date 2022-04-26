package com.example.ex_03_camera_share;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;

import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Session;

public class MainActivity extends AppCompatActivity {

    Session mSession;

    GLSurfaceView mySurView;

    MainRenderer mRenderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mySurView = (GLSurfaceView)findViewById(R.id.glsurfaceview);
        MainRenderer.Rendercallback mr = new MainRenderer.Rendercallback() {

            @Override
            public void preRender() {
                //sesson 객체와 연결해서 화면 그리게 하기

                mSession.setCameraTextureName(mRenderer.getTextureId());
            }
        };
        mRenderer = new MainRenderer(mr);


        //pause시 관련데이터가 사라지는것을 막는다.
        mySurView.setPreserveEGLContextOnPause(true);
        //버전을 2.0사용
        mySurView.setEGLContextClientVersion(2);
        //화면을 그리는 Renderer를 지정한다.
        //새로 정의한 MainRenderer을 사용한다.
        mySurView.setRenderer(mRenderer);

    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraPerm();

        //ARCore가 정상적으로 설치 되어 있는가?
        try {
            if(mSession == null){
                switch (ArCoreApk.getInstance().requestInstall(this, true)){
                    case INSTALLED: //정상적으로 설치되어있으면 Session을 생성한다
                        mSession = new Session(this);
                        Log.d("Session?", "Session!");
                        break;
                    case INSTALL_REQUESTED: // ARCore 설치 필요
                        Log.d("Session?", "Session!");
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //카메라 퍼미션 요청
    void cameraPerm(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.CAMERA}, 0);
        }
    }
}