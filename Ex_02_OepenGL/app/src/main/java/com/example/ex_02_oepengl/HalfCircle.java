package com.example.ex_02_oepengl;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class HalfCircle {

    // GPU를 이용하여 고속 계산하여 화면처리 하기위한 코드
    String vertexShaderCode = "uniform mat4 uMVPMatrix;" + // 4*4 형태의 상수로 지정
            "attribute vec4 vPosition;" + // vec4 -> 3차원 좌표
            "void main () {" +
            "gl_Position = uMVPMatrix * vPosition;" + // gl_Position : OpenGl에 있는 변수
            // 계산식 uMVPMatrix * vPosition
            "}";
    // 색깔
    String fragmentShaderCode =
            "precision mediump float;" // 정밀도
                    + "uniform vec4 vColor;" // 4개의 원소(RGB alpha)
                    + "void main() {"
                    + "gl_FragColor = vColor; "
                    + "}";

    // 직사각형 점의 좌표
//    static float squzreCoords[] = {
//            // x, y, z
//            0.0f, 0.0f, 0.0f, //원점
//            1.0f, 0.0f, 0.0f, //기준점
//            (float) (1.0f*Math.cos(Math.toRadians(1))), (float) (1.0f*Math.sin(Math.toRadians(1))), 0.0f,
//            (float) (1.0f*Math.cos(Math.toRadians(2))), (float) (1.0f*Math.sin(Math.toRadians(2))), 0.0f,
//    };

    // 그리는 순서
//    static short[] drawOrder = {0, 1, 2,
//            0,2,3
//            };

    public float[] getCoords(float x, float length, String label, String UpDown){
        float squzreCoords2[] = new float[2000];
        squzreCoords2[0] = x;
        squzreCoords2[1] = 0.0f;
        squzreCoords2[2] = 0.0f;
        squzreCoords2[3] = x + length;
        squzreCoords2[4] = 0.0f;
        squzreCoords2[5] = 0.0f;
        if(label.equals("y")){
            double count = 1;
            if(UpDown.equals("Up")){
                for(int i=6; i<=1900; i+=3){
                    squzreCoords2[i] = x + (float) (length*Math.cos(Math.toRadians(count)));
                    squzreCoords2[i+1] = (float) (length*Math.sin(Math.toRadians(count)));
                    if(squzreCoords2[i+1] <=0){
                        squzreCoords2[i+1] = 0;
                    }
                    squzreCoords2[i+2] = 0.0f;
                    count++;
                }
            } else if(UpDown.equals("Down")){
                for(int i=6; i<=1900; i+=3){
                    squzreCoords2[i] = x + (float) (length*Math.cos(Math.toRadians(count)));
                    squzreCoords2[i+1] = (float) (length*Math.sin(Math.toRadians(count)));
                    if(squzreCoords2[i+1] >=0){
                        squzreCoords2[i+1] = 0;
                    }
                    squzreCoords2[i+2] = 0.0f;
                    count++;
                }
            }
        } else if(label.equals("z")){
            double count = 1;
            for(int i=6; i<=1900; i+=3){
                squzreCoords2[i] = (float) (length*Math.cos(Math.toRadians(count)));
                squzreCoords2[i+1] = 0.0f;
                squzreCoords2[i+2] = (float) (length*Math.sin(Math.toRadians(count)));
                count++;
            }
        }

        return squzreCoords2;
    }

    public static short[] getdrawOrder(){
        short drawOrder2[] = new short[2000];
        int count1 = 1;
        int count2 = 2;
        for(int i=0; i<=1900; i+=3){
            drawOrder2[i] = 0;
            drawOrder2[i+1] = (short) (count1++);
            drawOrder2[i+2] = (short) (count2++);
        }
        return drawOrder2;
    }

    static short drawOrder[] = getdrawOrder();


    FloatBuffer vertexBuffer;
    ShortBuffer drawBuffer;
    int mProgram;

    public HalfCircle(float x, float length, String label, String UpDown) {
        float squzreCoords[] = getCoords(x, length, label, UpDown);

        ByteBuffer bb = ByteBuffer.allocateDirect(squzreCoords.length * 4); //float가 4Byte
        bb.order(ByteOrder.nativeOrder()); //정렬
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squzreCoords); //넣음
        vertexBuffer.position(0); //맨 앞에서부터 읽음음

        bb = ByteBuffer.allocateDirect(drawOrder.length * 2); //short가 2Byte
        bb.order(ByteOrder.nativeOrder()); //정렬
        drawBuffer = bb.asShortBuffer();
        drawBuffer.put(drawOrder); //넣음
        drawBuffer.position(0); //맨 앞에서부터 읽음음

        //점위치 계산식
        //vertexShaderCode -> vertexShader
        int vertexShader = MyGlrenderer.loadShader(
                GLES20.GL_VERTEX_SHADER,
                vertexShaderCode
        );

        //점 색상 게산식
        //fragmentShaderCode -> fragmentShader
        int fragmentShader = MyGlrenderer.loadShader(
                GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode
        );


        //mProgram = vertexShader + fragmentShader
        mProgram = GLES20.glCreateProgram();
        //점위치 계산식 합치기
        GLES20.glAttachShader(mProgram,vertexShader);
        //색상 계산식식 합치기
       GLES20.glAttachShader(mProgram,fragmentShader);
        GLES20.glLinkProgram(mProgram); //도형 렌더링 계삭식 정보 넣기
    }

    int mPositionHandle, mColorHandle, mMVPMatrixHandle;

    //도형 그리기 --> MyGLRenderer.onFrawFrame()에서 호출하여 그리기
    public void draw(float[] mMVPMatrix, float Red, float Green, float Blue) {
        float[] color = {Red, Green, Blue, 1.0f};
        //렌더링 계산식 정보 사용한다.
        GLES20.glUseProgram(mProgram);

        //      vPosition
        //mProgram ==> vertexShader
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(
                mPositionHandle, // 정점 속성의 인덱스 지정
                3, // 점속성 - 좌표계
                GLES20.GL_FLOAT, //점의 자료형 float
                false, //정규화 true, 직접 변환 false
                3 * 4, //점 속성의 stride(간격)
                vertexBuffer //점 정보
        );

        //      vColor
        //mProgram ==> fragmentShader
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram,"uMVPMatrix");

        //그려지는 곳에 위치, 보이는 정보를 적용한다.
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        //도형을 그린다.
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES,
                drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT,
                drawBuffer
                );

        //닫는다.
        GLES20.glDisable(mPositionHandle);
    }


}

