package jp.chooyan.sample.opengl.game;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import jp.chooyan.sample.opengl.game.data.Map;
import jp.chooyan.sample.opengl.game.data.ScreenManager;

/**
 * Created by o_fcf_hpbt_vvcc_p on 16/02/10.
 */
public class GameRenderer implements GLSurfaceView.Renderer {

    private float[] mViewProjectionMatrix = new float[16];
    private float[] mProjectionMatrix = new float[16];
    private float[] mViewMatrix = new float[16];

    private boolean mIsMoving = false;

    private EnemyManager mEnemyManager;
    private YusyaManager mYusyaManager;
    private TileManager mTileManager;
    private Context mContext;

    private ScreenManager mScreenManager;

    public GameRenderer(Context context) {
        this.mContext = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glClearColor(0f, 1f, 0f, 1f);

        mScreenManager = new ScreenManager();
        mYusyaManager = new YusyaManager(mContext, mScreenManager);
        mEnemyManager = new EnemyManager(mContext, mScreenManager);
        mTileManager = new TileManager(mContext, mScreenManager);

        mYusyaManager.setOnMovedListener(new YusyaManager.YushaOnMovedListener() {
            @Override
            public void onMovedX(float pixel) {
                mScreenManager.setDeltaX(mScreenManager.getDeltaX() - pixel);
                Matrix.translateM(mViewMatrix, 0, pixel, 0, 0);
                Matrix.multiplyMM(mViewProjectionMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0); // 射影変換行列とビュー変換座標行列を掛け合わせる。
            }

            @Override
            public void onMovedY(float pixel) {
                mScreenManager.setDeltaY(mScreenManager.getDeltaY() + pixel);
                Matrix.translateM(mViewMatrix, 0, 0, pixel, 0);
                Matrix.multiplyMM(mViewProjectionMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0); // 射影変換行列とビュー変換座標行列を掛け合わせる。
            }
        });
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        GLES20.glViewport(0, 0, width, height); // 普通に原点からviewの幅、高さ通り

        int originalYushaPositionX = 4;
        int originalYushaPositionY = 4;

        mScreenManager.setWidth(width);
        mScreenManager.setHeight(height);
        mScreenManager.setTileLength(Map.getTileLength(height));
        mScreenManager.setDeltaX(Math.max(0, (originalYushaPositionX + 1) * mScreenManager.getTileLength() - width / 2));
        mScreenManager.setDeltaY(Math.max(0, (originalYushaPositionY + 1) * mScreenManager.getTileLength() - width / 2));
        mScreenManager.setHorizontalTileNum((int) (width / mScreenManager.getTileLength()) + 1);
        mScreenManager.setVerticalTileNum((int) (height / mScreenManager.getTileLength()) + 1);

        mYusyaManager.setPosition(originalYushaPositionX, originalYushaPositionY);

        // 計算結果の保存場所
        mProjectionMatrix = new float[16]; // 射影変換座標行列
        mViewMatrix = new float[16]; // ビュー変換座標行列

        // fixme: heightの部分がなぜか逆になる。（プリミティブが上下逆さまに表示される）
        Matrix.orthoM(mProjectionMatrix, 0,
                0, width, // 原点を挟んで幅分
                height, 0, // 原点を挟んで高さ分
                1f, -1f); // lookatで指定したカメラの手前位置と同じ長さだけ奥にも伸ばす。

        Matrix.setLookAtM(mViewMatrix, 0,
                0f, 0f, 1f, // 1手前から
                0f, 0f, 0f, // 原点に向けて
                0f, 1f, 0f); // 上むき（通常の向き）

        Matrix.translateM(mViewMatrix, 0,
                Math.min(0, -1 * ((originalYushaPositionX + 0.5f) * mScreenManager.getTileLength() - width / 2)),
                Math.min(0, -1 * ((originalYushaPositionY + 0.5f) * mScreenManager.getTileLength() - height / 2)), 0);

        Matrix.multiplyMM(mViewProjectionMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0); // 射影変換行列とビュー変換座標行列を掛け合わせる。

    }

    @Override
    public void onDrawFrame(GL10 gl10) {

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        if (mIsMoving) {
            move();
        }

        float[] worldMatrix = new float[16];
        Matrix.setIdentityM(worldMatrix, 0); // 単位座標への変換

        mTileManager.draw(mViewProjectionMatrix, worldMatrix);
        mYusyaManager.draw(mViewProjectionMatrix, worldMatrix);
        mEnemyManager.draw(mViewProjectionMatrix, worldMatrix);
    }

    private int mLastDirection;

    public void onMove(int direction) {
        mLastDirection = direction;
        mIsMoving = true;
    }

    private void move() {
        switch (mLastDirection) {
            case 0: // 左
                mYusyaManager.moveX(-1);
                break;
            case 1: // 右
                mYusyaManager.moveX(1);
                break;
            case 2: // 下
                mYusyaManager.moveY(1);
                break;
            case 3: // 上
                mYusyaManager.moveY(-1);
                break;
        }
    }

    public void stopMoving() {
        mIsMoving = false;
    }
}
