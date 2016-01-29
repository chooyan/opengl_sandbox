package jp.chooyan.sample.opengl.render;

import android.content.Context;
import android.opengl.GLSurfaceView;

/**
 * Created by tsuyoshi-chujo on 2016/01/28.
 */
public class SampleSurfaceView extends GLSurfaceView {

    private static final int OPENGL_ES_VERSION = 2;

    public SampleSurfaceView(Context context) {
        super(context);

        setEGLContextClientVersion(OPENGL_ES_VERSION);
        setRenderer(new SampleRenderer());
        setRenderMode(RENDERMODE_CONTINUOUSLY);
    }
}
