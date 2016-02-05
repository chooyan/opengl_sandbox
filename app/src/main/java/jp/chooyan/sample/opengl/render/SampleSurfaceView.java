package jp.chooyan.sample.opengl.render;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

/**
 * Created by tsuyoshi-chujo on 2016/01/28.
 */
public class SampleSurfaceView extends GLSurfaceView {

    private static final int OPENGL_ES_VERSION = 2;

    private CircleRenderer mRenderer;
    public SampleSurfaceView(Context context) {
        super(context);

        setEGLContextClientVersion(OPENGL_ES_VERSION);
        setRenderer(mRenderer = new CircleRenderer(getContext()));
        setRenderMode(RENDERMODE_CONTINUOUSLY);
    }

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float mOriginalX;
    private float mOriginalY;

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_UP:
                mRenderer.touch(x, y);
                break;
        }

        return true;
    }
}
