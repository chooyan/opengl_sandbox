package jp.chooyan.sample.opengl;

import android.app.Activity;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import jp.chooyan.sample.opengl.game.GameSurfaceView;
import jp.chooyan.sample.opengl.render.SampleSurfaceView;

public class MainActivity extends Activity {

    private GameSurfaceView mSurfaceView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSurfaceView = new GameSurfaceView(this);
        setContentView(mSurfaceView);
    }
}
