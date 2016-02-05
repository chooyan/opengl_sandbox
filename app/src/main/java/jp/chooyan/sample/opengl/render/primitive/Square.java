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

    public Square(int type) {
        super();

        float length = 1000f;
        float left = length / 2f * -1;
        float right = length / 2f ;
        float top = length / 2f * -1;
        float bottom = length / 2f ;

        switch(type) {
            case 1:
                mVerticesBuffer = BufferUtil.convert(
                        new float[]{
                                left, top, 1f,
                                left, bottom, 1f,
                                right, bottom, 1f,
                                right, top, 1f,
                        }
                );
                mColorCodes = new float[]{1f, 0f, 1f, 0.7f};
                break;
            case 2:
                mVerticesBuffer = BufferUtil.convert(
                        new float[]{
                                left + 300, top - 400, -0.5f,
                                left + 300, bottom - 300, -0.5f,
                                right + 300, bottom - 300, -0.5f,
                                right + 300, top - 400, -0.5f,
                        }
                );
                mColorCodes = new float[]{0.2f, 0.5f, 1f, 0.4f};
                break;
            case 3:
                mVerticesBuffer = BufferUtil.convert(
                        new float[]{
                                -1000f,    -200f, 0f,
                                -1000f,    -1500f,    0f,
                                1000f, -1500f,    0f,
                                1000f, -200f, 0f,
                        }
                );
                mColorCodes = new float[]{0.9f, 0.4f, 0.4f, 0.5f};
                break;
        }
        mIndicesBuffer = BufferUtil.convert(new short[]{0, 1, 2, 0, 2, 3});
    }

    public void draw(float[] viewProjectionMatrix, float[] worldMatrix) {
        super.draw(viewProjectionMatrix, worldMatrix, mVerticesBuffer, mIndicesBuffer, mColorCodes);
    }
}
