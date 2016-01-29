package jp.chooyan.sample.opengl;

import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import jp.chooyan.sample.opengl.render.SampleSurfaceView;

public class MainActivity extends AppCompatActivity {

    private SampleSurfaceView mSurfaceView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSurfaceView = new SampleSurfaceView(this);
        setContentView(mSurfaceView);
    }
}
