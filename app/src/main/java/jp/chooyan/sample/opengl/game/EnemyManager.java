package jp.chooyan.sample.opengl.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jp.chooyan.sample.opengl.R;
import jp.chooyan.sample.opengl.game.data.Map;
import jp.chooyan.sample.opengl.game.data.ScreenManager;
import jp.chooyan.sample.opengl.render.primitive.MonoColorFigure;
import jp.chooyan.sample.opengl.utils.BufferUtil;

/**
 * Created by tsuyoshi-chujo on 2016/02/01.
 */
public class EnemyManager extends MonoColorFigure{

    private int mPositionX;
    private int mPositionY;

    private float walkDistanceXLeft; // x方向への残りの移動距離
    private float walkDistanceYLeft; // y方向への残りの移動距離

    private int mDirection = 0; // 0, 1, 2, 3 -> 下、上、右、左

    // シェーダーで使う変数たち
    private static final String VAR_VIEW_PROJECTIVE_MATRIX = "viewProjectiveMatrix";
    private static final String VAR_WORLD_MATRIX = "worldMatrix";
    private static final String VAR_TEXTURE = "texture";
    private static final String VAR_TEXTURE_COORD = "textureCoord";
    private static final String VAR_TEXTURE_COORD_VARYING = "textureCoordVarying";
    private static final String VAR_POSITION = "position";

    private static final String VERTEX_SHADER_SOURCE = new StringBuilder()
            .append(String.format("uniform mat4 %s;", VAR_VIEW_PROJECTIVE_MATRIX)) //ビュー座標変換行列と射影変換行列を掛けあわせた行列
            .append(String.format("uniform mat4 %s;", VAR_WORLD_MATRIX)) // ワールド座標変換行列
            .append(String.format("attribute vec2 %s;", VAR_TEXTURE_COORD)) // テクスチャの座標（Javaから受け取る用）
            .append(String.format("varying vec2 %s;", VAR_TEXTURE_COORD_VARYING)) // テクスチャの座標（フラグメントシェーダに送る用）
            .append(String.format("attribute vec3 %s;", VAR_POSITION)) // 頂点座標
                          .append("void main() {")
            .append(String.format("  gl_Position = %s * %s * vec4(%s , 1.0); ", VAR_VIEW_PROJECTIVE_MATRIX, VAR_WORLD_MATRIX, VAR_POSITION)) // 1.0は同次座標系（おまじない）
            .append(String.format("  %s = %s;", VAR_TEXTURE_COORD_VARYING, VAR_TEXTURE_COORD))
                          .append("}")
            .toString();

    private static final String FRAGMENT_SHADER_SOURCE = new StringBuilder()
                          .append("precision mediump float;")
            .append(String.format("varying vec2 %s;", VAR_TEXTURE_COORD_VARYING)) // テクスチャの座標
            .append(String.format("uniform sampler2D %s;", VAR_TEXTURE)) // テクスチャ
                          .append("void main() {")
            .append(String.format("  gl_FragColor = texture2D(%s, %s);", VAR_TEXTURE, VAR_TEXTURE_COORD_VARYING)) // テクスチャを描画する
                          .append("}")
            .toString();

    private int mProgramId;
    private int mTextureLoc;
    private int mTextureId;
    private int mTextureCoordLoc;

    private ScreenManager mScreenManager;
    private Random mRandom;

    public EnemyManager(Context context, ScreenManager screenManager) {
        setPosition(4, 5);

        mScreenManager = screenManager;
        mRandom = new Random(System.currentTimeMillis());

        // region:シェーダープログラムのコンパイル
        int vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        GLES20.glShaderSource(vertexShader, VERTEX_SHADER_SOURCE);
        GLES20.glCompileShader(vertexShader);

        int fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        GLES20.glShaderSource(fragmentShader, FRAGMENT_SHADER_SOURCE);
        GLES20.glCompileShader(fragmentShader);
        // endregion

        // region:シェーダープログラムの適用
        mProgramId = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgramId, vertexShader);
        GLES20.glAttachShader(mProgramId, fragmentShader);

        GLES20.glLinkProgram(mProgramId);
        GLES20.glUseProgram(mProgramId);
        // endregion:

        // 地面テクスチャの作成
        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.character);
        mTextureId = loadTexture(bitmap, GLES20.GL_NEAREST, GLES20.GL_LINEAR);
        mTextureLoc = GLES20.glGetUniformLocation(mProgramId, VAR_TEXTURE);
        mTextureCoordLoc = GLES20.glGetAttribLocation(mProgramId, VAR_TEXTURE_COORD);
        GLES20.glEnableVertexAttribArray(mTextureCoordLoc);
        bitmap.recycle();
    }

    public void setPosition(int x, int y) {
        mPositionX = x;
        mPositionY = y;
    }

    public void draw(float[] viewProjectionMatrix, float[] worldMatrix) {
        if (mRandom.nextInt(60) == 1) { // 3秒に１回くらい動く
            switch (mRandom.nextInt(4)) {
                case 0:
                    moveY(-1);
                    break;
                case 1:
                    moveY(1);
                    break;
                case 2:
                    moveX(1);
                    break;
                case 3:
                    moveX(-1);
                    break;
            }
        }


        // テクスチャのブレンド設定
        GLES20.glEnable(GLES20.GL_TEXTURE_2D);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);	// 単純なアルファブレンド

        // テクスチャを所定のオブジェクトに登録していく
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0); // 0番目のテクスチャを有効化（以降の処理はTEXTURE0に対しての処理になる）
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId);
        GLES20.glUniform1i(mTextureLoc, 0);

        render((float) mPositionX, (float) mPositionY, viewProjectionMatrix, worldMatrix);

        GLES20.glDisable(GLES20.GL_BLEND);
        GLES20.glDisable(GLES20.GL_TEXTURE_2D);
    }

    private void render(float horizontalNum, float verticalNum, float[] viewProjectionMatrix, float[] worldMatrix) {
        // テクスチャ (UV マッピング) データ
        float startU = 1f / 4f * (float)mDirection;
        float endU = startU + (1f / 4f);

        float startV = 1f / 2f * 1;
        float endV = startV + (1f / 2f);

        float textureCoord[] = {
                startU, startV,	// 左上
                startU, endV,	// 左下
                endU, endV,	// 右下
                endU, startV	// 右上
        };

        ShortBuffer textureIndicesBuffer = BufferUtil.convert(new short[]{0, 1, 2, 0, 2, 3});

        GLES20.glVertexAttribPointer(mTextureCoordLoc, 2, GLES20.GL_FLOAT, false, 0, BufferUtil.convert(textureCoord));
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, textureIndicesBuffer.capacity(), GLES20.GL_UNSIGNED_SHORT, textureIndicesBuffer);
//            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        float plusY = System.currentTimeMillis() % 500 > 250 && isMoving() ? -20f : 0;
        float left = horizontalNum * mScreenManager.getTileLength() - walkDistanceXLeft;
        float right = mScreenManager.getTileLength() + horizontalNum * mScreenManager.getTileLength() - walkDistanceXLeft;
        float top = verticalNum * mScreenManager.getTileLength() - walkDistanceYLeft + plusY;
        float bottom = mScreenManager.getTileLength() + verticalNum * mScreenManager.getTileLength() - walkDistanceYLeft + plusY;

        if (walkDistanceXLeft != 0) {
            float movePix = walkDistanceXLeft > 0 ? -20f : 20f;
            walkDistanceXLeft += movePix;
        }

        if (walkDistanceYLeft != 0) {
            float movePix = walkDistanceYLeft > 0 ? -20f : 20f;
            walkDistanceYLeft += movePix;
        }

        FloatBuffer verticesBuffer;
        verticesBuffer = BufferUtil.convert(new float[] {
                left, top, 0f,
                left, bottom, 0f,
                right, bottom, 0f,
                right, top, 0f,
        });

        ShortBuffer verticesIndicesBuffer = BufferUtil.convert(new short[] {0, 1, 2, 0, 2, 3});

        // シェーダーで使う変数へのポインタを取得
        int attPositionLoc = GLES20.glGetAttribLocation(mProgramId, VAR_POSITION);
        int uniViewProjectionMatrixLoc = GLES20.glGetUniformLocation(mProgramId, VAR_VIEW_PROJECTIVE_MATRIX);
        int uniWorldMatrixLoc = GLES20.glGetUniformLocation(mProgramId, VAR_WORLD_MATRIX);

        GLES20.glEnableVertexAttribArray(attPositionLoc); // attributeは有効化が必要

        // シェーダーへ値を転送
        GLES20.glVertexAttribPointer(attPositionLoc, 3, GLES20.GL_FLOAT, false, 0, verticesBuffer);
        GLES20.glUniformMatrix4fv(uniViewProjectionMatrixLoc, 1, false, viewProjectionMatrix, 0);
        GLES20.glUniformMatrix4fv(uniWorldMatrixLoc, 1, false, worldMatrix, 0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, verticesIndicesBuffer.capacity(), GLES20.GL_UNSIGNED_SHORT, verticesIndicesBuffer);

        GLES20.glDisableVertexAttribArray(attPositionLoc); // attributeを無効化しておく


    }

    /**
     * 与えられたBitmapでテクスチャを準備し、テクスチャへ割り当てられたIDを返却します。
     * @param bitmap
     * @param minFilter
     * @param magFilter
     * @return
     */
    private int loadTexture(Bitmap bitmap, int minFilter, int magFilter) {
        int[] textures = new int[1]; // glGenTexturesの結果を格納するためのint配列
        GLES20.glGenTextures(textures.length, textures, 0); // テクスチャを1つ分用意する
        int texture = textures[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture); // 2Dテクスチャとしてバインドする（ここから先はtextureが指し示すオブジェクトへの操作となる）
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

        // テクスチャを拡大・縮小する際の補正ロジックを登録
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, minFilter);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, magFilter);

        return texture;
    }

    public void moveX(int duration) {
        if (isMoving()) return;

        if (duration < 0) {
            mDirection = 3;
            if (mPositionX == 0) return;

            if (TileType.get(Map.MAP[mPositionY][mPositionX - 1]).isWalkable()) {
                mPositionX--;
                walkDistanceXLeft = -mScreenManager.getTileLength();
            }
        } else {
            mDirection = 2;
            if (mPositionX == Map.MAP[0].length - 1) return;

            if (TileType.get(Map.MAP[mPositionY][mPositionX + 1]).isWalkable()) {
                mPositionX++;
                walkDistanceXLeft = mScreenManager.getTileLength();
            }
        }
    }

    public void moveY(int duration) {
        if (isMoving()) return;

        if (duration < 0) {
            mDirection = 1;
            if (mPositionY == 0) return;

            if (TileType.get(Map.MAP[mPositionY - 1][mPositionX]).isWalkable()) {
                mPositionY--;
                walkDistanceYLeft = -mScreenManager.getTileLength();
            }
        } else {
            mDirection = 0;
            if (mPositionY == Map.MAP.length - 1) return;

            if (TileType.get(Map.MAP[mPositionY + 1][mPositionX]).isWalkable()) {
                mPositionY++;
                walkDistanceYLeft = mScreenManager.getTileLength();
            }
        }
    }

    private boolean isMoving() {
        return walkDistanceXLeft != 0f || walkDistanceYLeft != 0f;
    }
}
