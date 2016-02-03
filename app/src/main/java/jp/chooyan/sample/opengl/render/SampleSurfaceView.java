package jp.chooyan.sample.opengl.render;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

/**
 * Created by tsuyoshi-chujo on 2016/01/28.
 */
public class SampleSurfaceView extends GLSurfaceView {

    private static final int OPENGL_ES_VERSION = 2;

    private TextureRenderer mRenderer;
    public SampleSurfaceView(Context context) {
        super(context);

        setEGLContextClientVersion(OPENGL_ES_VERSION);
        setRenderer(mRenderer = new TextureRenderer(getContext()));
        setRenderMode(RENDERMODE_CONTINUOUSLY);
    }

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float mPreviousX;
    private float mPreviousY;

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPreviousX = x;
                mPreviousY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = x - mPreviousX;
                float dy = y - mPreviousY;

                mRenderer.move(dx, dy);
        }

        return true;
    }
}
