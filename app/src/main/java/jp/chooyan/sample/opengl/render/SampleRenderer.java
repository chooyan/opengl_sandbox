package jp.chooyan.sample.opengl.render;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import jp.chooyan.sample.opengl.R;

/**
 * Created by tsuyoshi-chujo on 2016/01/28.
 */
public class SampleRenderer implements GLSurfaceView.Renderer {

    // シェーダーで使う変数たち
    private static final String VAR_VIEW_PROJECTIVE_MATRIX = "viewProjectiveMatrix";
    private static final String VAR_WORLD_MATRIX = "worldMatrix";
    private static final String VAR_TEXTURE_COORD = "textureCoord";
    private static final String VAR_TEXTURE_COORD_VARYING = "textureCoordVarying";
    private static final String VAR_TEXTURE = "texture";
    private static final String VAR_POSITION = "position";

    private static final String VERTEX_SHADER_SOURCE = new StringBuilder()
            .append(String.format("uniform mat4 %s;", VAR_VIEW_PROJECTIVE_MATRIX)) //ビュー座標変換行列と射影変換行列を掛けあわせた行列
            .append(String.format("uniform mat4 %s;", VAR_WORLD_MATRIX)) // ワールド座標変換行列
            .append(String.format("attribute vec2 %s;", VAR_TEXTURE_COORD)) // テクスチャの座標（Javaから受け取る用）
            .append(String.format("varying vec2  %s;", VAR_TEXTURE_COORD_VARYING)) // テクスチャの座標（フラグメントシェーダに送る用）
            .append(String.format("attribute vec3 %s;", VAR_POSITION)) // 頂点座標
                          .append("void main() {")
                          .append("  gl_Position = viewProjectiveMatrix * worldMatrix * vec4(position , 1.0); ") // 1.0は同次座標系（おまじない）
                          .append("  textureCoordVarying = textureCoord;")
                          .append("}")
            .toString();

    private static final String FRAGMENT_SHADER_SOURCE = new StringBuilder()
                          .append("precision mediump float;")
            .append(String.format("varying vec2  %s;", VAR_TEXTURE_COORD_VARYING)) // テクスチャの座標
            .append(String.format("uniform sampler2D %s;", VAR_TEXTURE)) // テクスチャ
                          .append("void main() {")
                          .append("  gl_FragColor = texture2D(texture, textureCoordVarying);") // テクスチャを描画する
                          .append("}")
            .toString();

    private Context mContext;
    private float[] mViewProjectionMatrix = new float[16];
    private int mFrameCount;
    private int mProgramId;

    private int mTexture;
    private int mTextureId;
    private int mTextureCoord;

    public SampleRenderer(Context context) {
        mContext = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0f, 0f, 0f, 1f);

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

        // ドロイドくんテクスチャの作成
        final Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher);
        mTextureId = loadTexture(bitmap, GLES20.GL_NEAREST, GLES20.GL_LINEAR);
        mTexture = GLES20.glGetUniformLocation(mProgramId, VAR_TEXTURE);
        mTextureCoord = GLES20.glGetAttribLocation(mProgramId, VAR_TEXTURE_COORD);
        GLES20.glEnableVertexAttribArray(mTextureCoord);
        bitmap.recycle();


    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height); // 普通に原点からviewの幅、高さ通り

        // 計算結果の保存場所
        float[] projectionMatrix = new float[16]; // 射影変換座標行列
        float[] viewMatrix = new float[16]; // ビュー変換座標行列

        Matrix.orthoM(projectionMatrix, 0,
                width / 2f * -1, width / 2f, // 原点を挟んで幅分
                height / 2f * -1, height / 2f, // 原点を挟んで高さ分
                0f, 2f); // lookatで指定したカメラの手前位置と同じ長さだけ奥にも伸ばす。

        Matrix.setLookAtM(viewMatrix, 0,
                0f, 0f, 1f, // 1手前から
                0f, 0f, 0f, // 原点に向けて
                0f, 1f, 0f); // 上むき（通常の向き）

        Matrix.multiplyMM(mViewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0); // 射影変換行列とビュー変換座標行列を掛け合わせる。
    }

    @Override
    public void onDrawFrame(GL10 gl) {

         // テクスチャ (UV マッピング) データ
        float textureCoord[] = {
                0.0f, 0.0f,	// 左上
                0.0f, 1.0f,	// 左下
                1.0f, 1.0f,	// 右下

                1.0f, 1.0f,	// 右下
                1.0f, 0.0f,	// 右上
                0.0f, 0.0f	// 左上
        };

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // テクスチャのブレンド設定
        GLES20.glEnable(GLES20.GL_TEXTURE_2D);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);	// 単純なアルファブレンド

        // テクスチャを所定のオブジェクトに登録していく
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0); // 0番目のテクスチャを有効化（以降の処理はTEXTURE0に対しての処理になる）
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId);
        GLES20.glUniform1i(mTexture, 0);
        GLES20.glVertexAttribPointer(mTextureCoord, 2, GLES20.GL_FLOAT, false, 0, BufferUtil.convert(textureCoord));
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glDisable(GLES20.GL_BLEND);
        GLES20.glDisable(GLES20.GL_TEXTURE_2D);

        // 一辺100の正方形
        float length = 1000f;
        float left = length / 2f * -1;
        float right = length / 2f ;
        float top = length / 2f * -1;
        float bottom = length / 2f ;

        // 反時計回りに2つ三角形をつくる（合わせると四角形になる）
        float[] vertices = new float[] {
                left, top, 0f,
                left, bottom, 0f,
                right, bottom, 0f,

                right, bottom, 0f,
                right, top, 0f,
                left, top, 0f
        };

        // 頂点の順番はverticesに書いた通り
        short[] indices = new short[] {
                0, 1, 2,
                3, 4, 5
        };

        FloatBuffer verticesBuffer = BufferUtil.convert(vertices);
        ShortBuffer indicesBuffer = BufferUtil.convert(indices);

        float[] worldMatrix = new float[16];
        Matrix.setIdentityM(worldMatrix, 0); // 単位座標への変換
        Matrix.rotateM(worldMatrix, 0, (float) mFrameCount / 2f, 0, 0, 1); // 毎フレーム少しずつ回転する

        // シェーダーで使う変数へのポインタを取得
        int attPositionLoc = GLES20.glGetAttribLocation(mProgramId, VAR_POSITION);
        int uniViewProjectionMatrixLoc = GLES20.glGetUniformLocation(mProgramId, VAR_VIEW_PROJECTIVE_MATRIX);
        int uniWorldMatrixLoc = GLES20.glGetUniformLocation(mProgramId, VAR_WORLD_MATRIX);

        GLES20.glEnableVertexAttribArray(attPositionLoc); // attributeは有効化が必要

        // シェーダーへ値を転送
        GLES20.glVertexAttribPointer(attPositionLoc, 3, GLES20.GL_FLOAT, false, 0, verticesBuffer);
        GLES20.glUniformMatrix4fv(uniViewProjectionMatrixLoc, 1, false, mViewProjectionMatrix, 0);
        GLES20.glUniformMatrix4fv(uniWorldMatrixLoc, 1, false, worldMatrix, 0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indicesBuffer.capacity(), GLES20.GL_UNSIGNED_SHORT, indicesBuffer);

        GLES20.glDisableVertexAttribArray(attPositionLoc); // attributeを無効化しておく

        GLES20.glDisable(GLES20.GL_BLEND);
        GLES20.glDisable(GLES20.GL_TEXTURE_2D);

        mFrameCount++;
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
}
