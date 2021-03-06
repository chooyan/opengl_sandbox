package jp.chooyan.sample.opengl.game.data;

/**
 * Created by o_fcf_hpbt_vvcc_p on 16/02/11.
 */
public class Map {
    public static int[][] MAP = {
            {4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4},
            {4, 4, 4, 4, 4, 4, 8, 8, 8, 8, 8, 8, 4, 4, 4},
            {4, 4, 4, 2, 2, 8, 8, 8, 8, 8, 8, 8, 8, 8, 4},
            {4, 4, 2, 2, 2, 8, 8, 5, 5, 5, 8, 8, 8, 8, 4},
            {4, 4, 2, 2, 2, 2, 4, 5, 5, 5, 5, 5, 8, 7, 7},
            {4, 4, 4, 2, 2, 2, 5, 5, 5, 5, 5, 8, 8, 7, 7},
            {4, 4, 4, 2, 2, 4, 4, 4, 4, 4, 4, 4, 4, 7, 7},
            {4, 4, 4, 4, 3, 4, 4, 4, 4, 4, 4, 4, 4, 7, 7},
            {4, 4, 4, 4, 3, 4, 4, 4, 4, 4, 4, 4, 7, 7, 4},
            {4, 4, 4, 4, 3, 4, 4, 4, 4, 4, 4, 7, 7, 4, 4},
            {4, 4, 4, 4, 3, 4, 4, 4, 4, 4, 4, 7, 7, 4, 4},
            {3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 4, 7, 7, 4},
            {4, 4, 4, 4, 6, 3, 6, 4, 4, 4, 3, 4, 4, 7, 7},
            {4, 4, 4, 4, 6, 3, 6, 4, 4, 4, 3, 4, 4, 4, 7},
            {4, 4, 4, 4, 6, 3, 6, 4, 4, 4, 3, 4, 7, 7, 4},
            {4, 0, 0, 0, 0, 3, 0, 0, 0, 4, 3, 7, 7, 4, 4},
            {4, 0, 3, 3, 3, 3, 3, 3, 0, 4, 3, 7, 7, 4, 4},
            {3, 0, 3, 0, 0, 0, 0, 0, 0, 3, 3, 4, 7, 7, 4},
            {4, 0, 3, 0, 2, 2, 2, 2, 0, 4, 3, 4, 4, 7, 7},
            {4, 0, 3, 0, 2, 2, 2, 2, 0, 4, 3, 4, 4, 4, 7},
            {4, 0, 3, 0, 2, 2, 2, 2, 0, 4, 3, 4, 7, 7, 4},
            {4, 0, 3, 0, 2, 2, 2, 2, 0, 4, 3, 7, 7, 4, 4},
            {4, 0, 3, 3, 3, 3, 3, 3, 0, 4, 3, 7, 7, 4, 4},
            {4, 0, 3, 3, 3, 3, 3, 3, 0, 3, 3, 4, 7, 7, 4},
            {4, 0, 0, 0, 0, 0, 0, 0, 0, 4, 3, 4, 4, 7, 7},
            {4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 3, 4, 4, 4, 7},
    };

    public static final float getTileLength(float screenHeight) {
        if (screenHeight > 2000f) {
            return 200f;
        } else if (screenHeight > 1800f) {
            return 180f;
        } else if (screenHeight > 1600f) {
            return 160f;
        } else {
            return 140f;
        }
    };

}
