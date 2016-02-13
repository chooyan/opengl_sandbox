package jp.chooyan.sample.opengl.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import jp.chooyan.sample.opengl.R;
import jp.chooyan.sample.opengl.game.data.Map;
import jp.chooyan.sample.opengl.render.primitive.MonoColorFigure;
import jp.chooyan.sample.opengl.utils.BufferUtil;

/**
 * Created by tsuyoshi-chujo on 2016/02/01.
 */
public class YusyaManager extends MonoColorFigure{

    private int mPositionX;
    private int mPositionY;

    private float walkDistanceXLeft; // x方向への残りの移動距離
    private float walkDistanceYLeft; // y方向への残りの移動距離

    private YushaOnMovedListener mListener;

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

    public YusyaManager(Context context) {
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
        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.yusha);
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
        float textureCoord[] = {
                0f, 0f,	// 左上
                0f, 1f,	// 左下
                1f, 1f,	// 右下
                1f, 0f	// 右上
        };

        ShortBuffer textureIndicesBuffer = BufferUtil.convert(new short[]{0, 1, 2, 0, 2, 3});

        GLES20.glVertexAttribPointer(mTextureCoordLoc, 2, GLES20.GL_FLOAT, false, 0, BufferUtil.convert(textureCoord));
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, textureIndicesBuffer.capacity(), GLES20.GL_UNSIGNED_SHORT, textureIndicesBuffer);
//            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        float plusY = System.currentTimeMillis() % 500 > 250 && isMoving() ? -20f : 0;
        float left = horizontalNum * Map.TILE_LENGTH - walkDistanceXLeft;
        float right = Map.TILE_LENGTH + horizontalNum * Map.TILE_LENGTH - walkDistanceXLeft;
        float top = verticalNum * Map.TILE_LENGTH - walkDistanceYLeft + plusY;
        float bottom = Map.TILE_LENGTH + verticalNum * Map.TILE_LENGTH - walkDistanceYLeft + plusY;

        if (walkDistanceXLeft != 0) {
            float movePix = walkDistanceXLeft > 0 ? -20f : 20f;
            walkDistanceXLeft += movePix;
            if (mListener != null) {
                mListener.onMovedX(movePix);
            }
        }

        if (walkDistanceYLeft != 0) {
            float movePix = walkDistanceYLeft > 0 ? -20f : 20f;
            walkDistanceYLeft += movePix;
            if (mListener != null) {
                mListener.onMovedY(movePix);
            }
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
            if (mPositionX == 0) return;

            if (TileType.get(Map.MAP[mPositionY][mPositionX - 1]).isWalkable()) {
                mPositionX--;
                walkDistanceXLeft = -Map.TILE_LENGTH;
            }
        } else {
            if (mPositionX == Map.MAP[0].length - 1) return;

            if (TileType.get(Map.MAP[mPositionY][mPositionX + 1]).isWalkable()) {
                mPositionX++;
                walkDistanceXLeft = Map.TILE_LENGTH;
            }
        }
    }

    public void moveY(int duration) {
        if (isMoving()) return;

        if (duration < 0) {
            if (mPositionY == 0) return;

            if (TileType.get(Map.MAP[mPositionY - 1][mPositionX]).isWalkable()) {
                mPositionY--;
                walkDistanceYLeft = -Map.TILE_LENGTH;
            }
        } else {
            if (mPositionY == Map.MAP.length - 1) return;

            if (TileType.get(Map.MAP[mPositionY + 1][mPositionX]).isWalkable()) {
                mPositionY++;
                walkDistanceYLeft = Map.TILE_LENGTH;
            }
        }
    }

    private boolean isMoving() {
        return walkDistanceXLeft != 0f || walkDistanceYLeft != 0f;
    }

    public void setOnMovedListener(YushaOnMovedListener listener) {
        mListener = listener;
    }

    public interface YushaOnMovedListener {
        void onMovedX(float pixel);
        void onMovedY(float pixel);
    }
}
