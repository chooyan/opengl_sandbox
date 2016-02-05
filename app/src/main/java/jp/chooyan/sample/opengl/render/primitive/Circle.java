package jp.chooyan.sample.opengl.render.primitive;

import android.opengl.Matrix;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jp.chooyan.sample.opengl.render.BufferUtil;

/**
 * Created by o_fcf_hpbt_vvcc_p on 16/02/04.
 */
public class Circle extends MonoColorFigure {

    private boolean mIsFalling = false;
    private long mCreatedTime;
    private List<FloatBuffer> mVerticesBufferList;
    private FloatBuffer mNewBuffer;
    private float mF = 0f;
    private float mF2 = 0f;
    private boolean mIsAddColor = true;
    private boolean mIsAddColor2 = true;

    public Circle() {
        super();
        mVerticesBufferList = new ArrayList<>();
        createNewCircle(0f, 0f);
    }

    public void draw(float[] viewProjectionMatrix, float[] worldMatrix) {
        if (mIsFalling) {
            float passedTime = ((float)(System.currentTimeMillis() - mCreatedTime)) / 1000f;
            float ratio = (int) (9.8 * passedTime * passedTime) * 5f;
            Matrix.translateM(viewProjectionMatrix, 0, 0, -(ratio), 0);
        }

        if (mIsAddColor) {
            mF += 0.01f;
            mIsAddColor = mF < 0.95f;
        } else {
            mF -= 0.01f;
            mIsAddColor = mF < 0.05f;
        }

        if (mIsAddColor2) {
            mF2 += 0.02f;
            mIsAddColor2 = mF2 < 0.95f;
        } else {
            mF2 -= 0.02f;
            mIsAddColor2 = mF2 < 0.05f;
        }
        for (FloatBuffer verticesBuffer : mVerticesBufferList) {
            super.draw(viewProjectionMatrix, worldMatrix, verticesBuffer, null, new float[]{0.5f, mF2, mF, 0.5f});
        }

        if (mNewBuffer != null) {
            mVerticesBufferList.add(mNewBuffer);
            mNewBuffer = null;
        }
    }

    public void startFalling() {
        mIsFalling = true;
        mCreatedTime = System.currentTimeMillis();
    }

    public void createNewCircle(float centerX, float centerY) {

        List<Float> vertices = new ArrayList<>();

        // 原点
        vertices.add(centerX);
        vertices.add(centerY);
        vertices.add(0f);

        float radius = 100;

        // 円を描画
        float detail = 30; // 細かさ
        for (float i = 0; i < detail; i++) {
            // 座標を計算
            float rate = i / detail;

            vertices.add((radius * (float)Math.cos(2.0 * Math.PI * rate)) + centerX); // x
            vertices.add((radius * (float)Math.sin(2.0 * Math.PI * rate)) + centerY); // y
            vertices.add(0f); // z
        }

        float secondX = vertices.get(3);
        float secondY = vertices.get(4);

        vertices.add(secondX);
        vertices.add(secondY);
        vertices.add(0f);

        float[] verticesArray = new float[vertices.size()];
        for (int i = 0; i < vertices.size(); i++) {
            verticesArray[i] = vertices.get(i);
        }

        mNewBuffer = BufferUtil.convert(verticesArray);

    }
}
