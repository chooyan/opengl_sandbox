package jp.chooyan.sample.opengl.game;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import jp.chooyan.sample.opengl.render.CircleRenderer;

/**
 * Created by o_fcf_hpbt_vvcc_p on 16/02/10.
 */
public class GameSurfaceView extends GLSurfaceView {
    private static final int OPENGL_ES_VERSION = 2;

    private float mOriginalX;
    private float mOriginalY;

    private GameRenderer mRenderer;
    public GameSurfaceView(Context context) {
        super(context);

        setEGLContextClientVersion(OPENGL_ES_VERSION);
        setRenderer(mRenderer = new GameRenderer(context));
        setRenderMode(RENDERMODE_CONTINUOUSLY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mOriginalX = x;
                mOriginalY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = x - mOriginalX;
                float dy = y - mOriginalY;

                if (Math.abs(dx) < 100f && Math.abs(dy) < 100f) break; // あまり動いていなかったら反応しない

                if (Math.abs(dx) > Math.abs(dy)) {
                    mRenderer.onMove(dx > 0 ? 1 : 0);
                } else {
                    mRenderer.onMove(dy > 0 ? 2 : 3);
                }
                break;

            case MotionEvent.ACTION_UP:
                mRenderer.stopMoving();
                break;
        }

        return true;
    }

}
