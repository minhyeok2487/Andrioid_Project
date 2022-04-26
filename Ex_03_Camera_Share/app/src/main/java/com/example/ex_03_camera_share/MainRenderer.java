package com.example.ex_03_camera_share;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainRenderer implements GLSurfaceView.Renderer {
    final static String TAG = "MainRenderer :";

    Rendercallback myCallBack;

    interface Rendercallback{
        void preRender(); //MainActivty에서 재정의하여 호출하게 함
    }

    //생성시 RenderCallBack을 매개변수로 대입받아 자신의 멤버

    MainRenderer(Rendercallback myCallBack){
        this.myCallBack = myCallBack;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        Log.d(TAG,  "onSurfaceCreated");
        GLES20.glClearColor(1.0f, 1.0f,0.0f, 1.0f); //노란색
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {

        Log.d(TAG,  "onSurfaceChanged");
        GLES20.glViewport(0,0,width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {

        Log.d(TAG,  "onDrawFrame");
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT| GLES20.GL_DEPTH_BUFFER_BIT);
    }

    int getTextureId(){
        return 0;
    }

}
