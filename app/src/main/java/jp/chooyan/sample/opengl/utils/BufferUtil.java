package jp.chooyan.sample.opengl.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by tsuyoshi-chujo on 2016/01/28.
 */
public class BufferUtil {
    public static FloatBuffer convert(float[] data) {
        ByteBuffer bb = ByteBuffer.allocateDirect(data.length * 4);
        bb.order(ByteOrder.nativeOrder());

        FloatBuffer floatBuffer = bb.asFloatBuffer();
        floatBuffer.put(data);
        floatBuffer.position(0);

        return floatBuffer;
    }

    public static ShortBuffer convert(short[] data) {
        ByteBuffer bb = ByteBuffer.allocateDirect(data.length * 2);
        bb.order(ByteOrder.nativeOrder());

        ShortBuffer shortBuffer = bb.asShortBuffer();
        shortBuffer.put(data);
        shortBuffer.position(0);

        return shortBuffer;
    }
}