package com.example.ex_02_oepengl;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class MyGLSurfaceView extends GLSurfaceView {

    public MyGLSurfaceView(Context context) {
        super(context);

        //버전 2를 사용하겠다
        setEGLContextClientVersion(2);

        setRenderer(new MyGlrenderer());

        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
