package jp.chooyan.sample.opengl.render.primitive;

import android.opengl.GLES20;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import jp.chooyan.sample.opengl.render.BufferUtil;

/**
 * Created by tsuyoshi-chujo on 2016/02/03.
 */
public class Square extends MonoColorFigure {

    private float[] mColorCodes;
    private FloatBuffer mVerticesBuffer;
    private ShortBuffer mIndicesBuffer;

    public Square() {
        super();

        float length = 1000f;
        float left = length / 2f * -1;
        float right = length / 2f ;
        float top = length / 2f * -1;
        float bottom = length / 2f ;

        mVerticesBuffer = BufferUtil.convert(
                new float[]{
                        left, top, 0f,
                        left, bottom, 0f,
                        right, bottom, 0f,
                        right, top, 0f,
                }
        );

        mColorCodes = new float[]{1f, 0f, 1f, 1f};
        mIndicesBuffer = BufferUtil.convert(new short[]{0, 1, 2, 0, 2, 3});
    }

    public void draw(float[] viewProjectionMatrix, float[] worldMatrix) {
        super.draw(viewProjectionMatrix, worldMatrix, mVerticesBuffer, mIndicesBuffer, mColorCodes);
    }
}
