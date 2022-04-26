package com.example.ex_02_oepengl;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGlrenderer implements GLSurfaceView.Renderer {

    Square myBox;
    Square2 myBox2;
    Square3 myBox3;
    Square4 myBox4;
    Square5 myBox5;
    Circle circle2, circle3;
    HalfCircle circle, circle1;

    float [] mMVPMatrix = new float[16];
    float [] mProjectionMatrix = new float[16];
    float [] mViewMatrix = new float[16];

    //화면갱신 되면서 화면에서 배치
    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glClearColor(0.0f, 1.0f, 1.0f, 1.0f);
        myBox = new Square();
        myBox2 = new Square2();
        myBox3 = new Square3();
        myBox4 = new Square4();
        myBox5 = new Square5();
        circle = new HalfCircle(0.0f, 1.0f, "y", "Up");
        circle1 = new HalfCircle(0.0f, 1.0f, "y", "Down");
        circle2 = new Circle(-0.5f, 0.5f, "y");
        circle3 = new Circle(0.5f, 0.5f, "y");
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        GLES20.glViewport(0, 0, width,height);

        //핸드폰의 종횡비를 기준으로 위치를 잡아줌
        //해상도, (portrait or landscape) 때문
        float ratio = (float) width / height;

        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT| GLES20.GL_DEPTH_BUFFER_BIT);
        Matrix.setLookAtM(mViewMatrix, 0,
                // X, Y, Z
                0, 0, 4, // 카메라 위치
                0, 0, 0,// 카메라 시선
                0, 1, 0 // 카메라 윗방향
                );

        Matrix.multiplyMM(mMVPMatrix,0,mProjectionMatrix, 0, mViewMatrix, 0);

        //정사각형 그리기
        //myBox.draw(mMVPMatrix);
        //myBox2.draw(mMVPMatrix);
        //myBox3.draw(mMVPMatrix);
        //myBox4.draw(mMVPMatrix);
        //myBox5.draw(mMVPMatrix);
        circle.draw(mMVPMatrix, 1.0f, 0.0f, 0.0f);
        circle1.draw(mMVPMatrix, 0.0f, 0.0f, 1.0f);
        circle2.draw(mMVPMatrix, 1.0f, 0.0f, 0.0f);
        circle3.draw(mMVPMatrix, 0.0f, 0.0f, 1.0f);
    }

    //GPU를 이용하여 그리기를 연산한다.
    static int loadShader(int type, String shaderCode){
        int res = GLES20.glCreateShader(type);
        GLES20.glShaderSource(res, shaderCode);
        GLES20.glCompileShader(res);
        return res;
    }
}
