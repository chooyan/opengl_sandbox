package jp.chooyan.sample.opengl.game;

/**
 * Created by o_fcf_hpbt_vvcc_p on 16/02/10.
 */
public enum TileType {
    WALL(0, 1f, 1f, false),
    SAND(1, 2f, 1f, true),
    GRASS(2, 3f, 1f, true),
    GRAVEL(3, 1f, 2f, true),
    WOOD(4, 2f, 2f, false),
    POISON(5, 3f, 2f, true),
    BRICK(6, 1f, 3f, false),
    RIVER(7, 2f, 3f, false),
    DIRT(8, 3f, 3f, true),
    ;

    private int mId;
    private float mHorizontalTile;
    private float mVerticalTile;
    private boolean mIsWalkable;

    TileType(int id, float horizontalTile, float verticalTile, boolean isWalkable) {
        mId = id;
        mHorizontalTile = horizontalTile;
        mVerticalTile = verticalTile;
        mIsWalkable = isWalkable;
    }

    public static TileType get(int id) {
        for (TileType t : TileType.values()) {
            if (t.getId() == id) {
                return t;
            }
        }
        return null;
    }

    public int getId() {
        return mId;
    }

    public float getHorizontalTile() {
        return mHorizontalTile;
    }

    public float getVerticalTile() {
        return mVerticalTile;
    }

    public boolean isWalkable() {
        return mIsWalkable;
    }
}
