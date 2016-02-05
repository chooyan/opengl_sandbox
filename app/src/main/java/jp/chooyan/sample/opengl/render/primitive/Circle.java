package jp.chooyan.sample.opengl.render.primitive;

import android.opengl.Matrix;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import jp.chooyan.sample.opengl.render.BufferUtil;

/**
 * Created by o_fcf_hpbt_vvcc_p on 16/02/04.
 */
public class Circle extends MonoColorFigure {

    private boolean mIsFalling = false;
    private long mCreatedTime;
    private FloatBuffer mVerticesBuffer;
    public Circle() {
        super();

        List<Float> vertices = new ArrayList<>();

        // 原点
        vertices.add(0f);
        vertices.add(0f + 800f);
        vertices.add(0f);

        float radius = 100;

        // 円を描画
        float detail = 120; // 細かさ
        for (float i = 0; i < detail; i++) {
            // 座標を計算
            float rate = i / detail;

            vertices.add((radius * (float)Math.cos(2.0 * Math.PI * rate))); // x
            vertices.add((radius * (float)Math.sin(2.0 * Math.PI * rate)) + 800f); // y
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

        mVerticesBuffer = BufferUtil.convert(verticesArray);
    }

    public void draw(float[] viewProjectionMatrix, float[] worldMatrix) {
        if (mIsFalling) {
            float passedTime = ((float)(System.currentTimeMillis() - mCreatedTime)) / 1000f;
            float ratio = (int) (9.8 * passedTime * passedTime) * 5f;
            Matrix.translateM(viewProjectionMatrix, 0, 0, -(ratio), 0);
        }
        super.draw(viewProjectionMatrix, worldMatrix, mVerticesBuffer, null, new float[]{0.5f, 0.5f, 0.5f, 0.5f});
    }

    public void startFalling() {
        mIsFalling = true;
        mCreatedTime = System.currentTimeMillis();
    }
}
