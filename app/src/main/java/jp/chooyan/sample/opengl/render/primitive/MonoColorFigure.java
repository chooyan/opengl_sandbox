package jp.chooyan.sample.opengl.render.primitive;

import android.opengl.GLES20;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by tsuyoshi-chujo on 2016/02/03.
 */
public abstract class MonoColorFigure {

    // シェーダーで使う変数たち
    private static final String VAR_VIEW_PROJECTIVE_MATRIX = "viewProjectiveMatrix";
    private static final String VAR_WORLD_MATRIX = "worldMatrix";
    private static final String VAR_COLOR = "v_Color";
    private static final String VAR_POSITION = "position";

    private static final String VERTEX_SHADER_SOURCE = new StringBuilder()
            .append(String.format("uniform mat4 %s;", VAR_VIEW_PROJECTIVE_MATRIX)) //ビュー座標変換行列と射影変換行列を掛けあわせた行列
            .append(String.format("uniform mat4 %s;", VAR_WORLD_MATRIX)) // ワールド座標変換行列
            .append(String.format("attribute vec3 %s;", VAR_POSITION)) // 頂点座標
            .append("void main() {")
            .append("  gl_Position = viewProjectiveMatrix * worldMatrix * vec4(position , 1.0); ") // 1.0は同次座標系（おまじない）
            .append("}")
            .toString();

    private static final String FRAGMENT_SHADER_SOURCE = new StringBuilder()
            .append("precision mediump float;")
            .append(String.format("uniform vec4 %s;", VAR_COLOR)) // 頂点座標
            .append("void main() {")
            .append(String.format("  gl_FragColor = %s;", VAR_COLOR)) // テクスチャを描画する
            .append("}")
            .toString();

    protected int mProgramId;

    public MonoColorFigure() {
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
        // endregion:
    }

    protected void draw(float[] viewProjectionMatrix, float[] worldMatrix, FloatBuffer verticesBuffer, ShortBuffer indicesBuffer, float[] colorCodes) {
        GLES20.glUseProgram(mProgramId);

        // シェーダーで使う変数へのポインタを取得
        // get handle to fragment shader's vColor member
        int uniColorLoc = GLES20.glGetUniformLocation(mProgramId, VAR_COLOR);
        int attPositionLoc = GLES20.glGetAttribLocation(mProgramId, VAR_POSITION);
        int uniViewProjectionMatrixLoc = GLES20.glGetUniformLocation(mProgramId, VAR_VIEW_PROJECTIVE_MATRIX);
        int uniWorldMatrixLoc = GLES20.glGetUniformLocation(mProgramId, VAR_WORLD_MATRIX);
        GLES20.glEnableVertexAttribArray(attPositionLoc); // attributeは有効化が必要

        // シェーダーへ値を転送
        GLES20.glUniform4fv(uniColorLoc, 1, colorCodes, 0);
        GLES20.glVertexAttribPointer(attPositionLoc, 3, GLES20.GL_FLOAT, false, 0, verticesBuffer);
        GLES20.glUniformMatrix4fv(uniViewProjectionMatrixLoc, 1, false, viewProjectionMatrix, 0);
        GLES20.glUniformMatrix4fv(uniWorldMatrixLoc, 1, false, worldMatrix, 0);

        if (indicesBuffer != null) {
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, indicesBuffer.capacity(), GLES20.GL_UNSIGNED_SHORT, indicesBuffer);
        } else {
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, verticesBuffer.capacity() / 3);
        }

        GLES20.glDisableVertexAttribArray(attPositionLoc); // attributeを無効化しておく

    }

}
