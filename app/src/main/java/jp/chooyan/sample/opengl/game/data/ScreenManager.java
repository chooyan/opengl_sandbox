package jp.chooyan.sample.opengl.game.data;

/**
 * Created by o_fcf_hpbt_vvcc_p on 16/02/14.
 */
public class ScreenManager {
    // スクリーンの大きさ
    private float mWidth;
    private float mHeight;

    // 画面左上からの移動量
    private float mDeltaX;
    private float mDeltaY;

    private int mHorizontalTileNum;
    private int mVerticalTileNum;

    private float mTileLength;

    public float getWidth() {
        return mWidth;
    }

    public void setWidth(float width) {
        mWidth = width;
    }

    public float getHeight() {
        return mHeight;
    }

    public void setHeight(float height) {
        mHeight = height;
    }

    public float getDeltaX() {
        return mDeltaX;
    }

    public void setDeltaX(float deltaX) {
        mDeltaX = deltaX;
    }

    public float getDeltaY() {
        return mDeltaY;
    }

    public void setDeltaY(float deltaY) {
        mDeltaY = deltaY;
    }

    public int getHorizontalTileNum() {
        return mHorizontalTileNum;
    }

    public void setHorizontalTileNum(int horizontalTileNum) {
        mHorizontalTileNum = horizontalTileNum;
    }

    public int getVerticalTileNum() {
        return mVerticalTileNum;
    }

    public void setVerticalTileNum(int verticalTileNum) {
        mVerticalTileNum = verticalTileNum;
    }

    public float getTileLength() {
        return mTileLength;
    }

    public void setTileLength(float tileLength) {
        mTileLength = tileLength;
    }
}
