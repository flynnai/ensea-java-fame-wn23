public interface GameConstants {
    // global constants that we will want to access everywhere
    public static final int TILE_SIZE = 80;
    public static final int STAGE_WIDTH = 1280;
    public static final int STAGE_HEIGHT = 720;
    public static final double PLAYER_MAX_SPEED = 6;
    public static final double PLAYER_GROUND_MOVE_FORCE = 45;
    public static final double GROUND_FRICTION = 0.92;
    public static final double AIR_FRICTION = 0.99;
    public static final double PLAYER_AIR_MOVE_FORCE = 5;
    public static final Vector2 PLAYER_START_POSITION = new Vector2(15, 2);
    // the bottom of the map, where the player will fall and die
    public static final int WORLD_BOTTOM = -17;
    // parallax background speed
    public static final double BACKGROUND_SPEED = 30;
    public static final double PLAYER_JUMP_VELOCITY = 7;
    public static final int COLLIDABLE_LAYER_NUMBER = 1;
    public static final int FOREGROUND_LAYER_NUMBER = 3;
    public final float PLAYER_WIDTH = 0.62f;
    public final float PLAYER_HEIGHT = 1.4f;
}