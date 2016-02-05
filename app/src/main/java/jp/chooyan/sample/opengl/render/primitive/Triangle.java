package jp.chooyan.sample.opengl.render.primitive;

import android.opengl.GLES20;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import jp.chooyan.sample.opengl.render.BufferUtil;

/**
 * Created by tsuyoshi-chujo on 2016/02/01.
 */
public class Triangle extends MonoColorFigure{

    private float[] mColorCodes;
    private FloatBuffer mVerticesBuffer;
    private ShortBuffer mIndicesBuffer;

    public Triangle() {
        super();

        changeDirection(1);
        mColorCodes = new float[]{0f, 0f, 1f, 1f};
        mIndicesBuffer = BufferUtil.convert(new short[]{0, 1, 2});
    }

    public void draw(float[] viewProjectionMatrix, float[] worldMatrix) {
        super.draw(viewProjectionMatrix, worldMatrix, mVerticesBuffer, mIndicesBuffer, mColorCodes);
    }

    public void changeDirection(int direction) {
        float length = 500f;
        float left = length / 2f * -1;
        float right = length / 2f ;
        float top = length / 2f * -1;
        float bottom = length / 2f ;

        if (direction > 0) {
            mVerticesBuffer = BufferUtil.convert(
                    new float[]{
                            left, top, 0f,
                            left, bottom, 0f,
                            right, bottom, 0f,
                    }
            );
        } else {
            mVerticesBuffer = BufferUtil.convert(
                    new float[]{
                            right, top, 0f,
                            left, bottom, 0f,
                            right, bottom, 0f,
                    }
            );
        }
    }
}
