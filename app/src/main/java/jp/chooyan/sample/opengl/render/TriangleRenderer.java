package jp.chooyan.sample.opengl.render;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import jp.chooyan.sample.opengl.R;
import jp.chooyan.sample.opengl.render.primitive.Square;
import jp.chooyan.sample.opengl.render.primitive.Triangle;

/**
 * Created by tsuyoshi-chujo on 2016/01/28.
 */
public class TriangleRenderer implements GLSurfaceView.Renderer {

    private Context mContext;
    private float[] mViewProjectionMatrix = new float[16];
    private float[] mViewProjectionMatrix2 = new float[16];
    private float[] mViewProjectionMatrix3 = new float[16];
    private Triangle mTriangle;
    private Square mSquare;
    private Square mSquare2;
    private Square mSquare3;
    private float mAngle;
    private int mFrameCount = 0;
    private boolean mIsMoving;
    private float mRatio;

    public TriangleRenderer(Context context) {
        mContext = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(1f, 1f, 1f, 0f);
        GLES20.glEnable(GLES20.GL_ALPHA);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height); // 普通に原点からviewの幅、高さ通り

        // 計算結果の保存場所
        float[] projectionMatrix = new float[16]; // 射影変換座標行列
        float[] viewMatrix = new float[16]; // ビュー変換座標行列

        Matrix.orthoM(projectionMatrix, 0,
                width / 2f * -1, width / 2f, // 原点を挟んで幅分
                height / 2f * -1, height / 2f, // 原点を挟んで高さ分
                -4f, 4f); // lookatで指定したカメラの手前位置と同じ長さだけ奥にも伸ばす。

        Matrix.setLookAtM(viewMatrix, 0,
                0f, 0f, 2f, // 1手前から
                0f, 0f, 0f, // 原点に向けて
                0f, 1f, 0f); // 上むき（通常の向き）

        Matrix.multiplyMM(mViewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0); // 射影変換行列とビュー変換座標行列を掛け合わせる。
        Matrix.multiplyMM(mViewProjectionMatrix2, 0, projectionMatrix, 0, viewMatrix, 0); // 射影変換行列とビュー変換座標行列を掛け合わせる。
        Matrix.multiplyMM(mViewProjectionMatrix3, 0, projectionMatrix, 0, viewMatrix, 0); // 射影変換行列とビュー変換座標行列を掛け合わせる。

        mTriangle = new Triangle(); //とりあえずデフォルトの三角形
        mSquare = new Square(1);
        mSquare2 = new Square(2);
        mSquare3 = new Square(3);
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        float[] worldMatrix = new float[16];
        Matrix.setIdentityM(worldMatrix, 0); // 単位座標への変換
        if (mIsMoving) {
            Matrix.translateM(mViewProjectionMatrix2, 0, mRatio, 0, 0);
            Matrix.translateM(mViewProjectionMatrix3, 0, mRatio / 1.5f, 0, 0);
        }
        mSquare.draw(mViewProjectionMatrix3, worldMatrix);
        mSquare3.draw(mViewProjectionMatrix, worldMatrix);
        mTriangle.draw(mViewProjectionMatrix, worldMatrix);
        mSquare2.draw(mViewProjectionMatrix2, worldMatrix);

        mFrameCount++;
    }

    public void setMoving(boolean isMoving, float ratio) {
        mIsMoving = isMoving;
        mRatio = -ratio;
        if (ratio != 0) {
            mTriangle.changeDirection(ratio > 0 ? 1 : -1);
        }
    }
}
