import org.jbox2d.common.Vec2;

public interface GameConstants {
    // global constants that we will want to access everywhere
    public static final int TILE_SIZE = 40;
    public static final int STAGE_WIDTH = 1280;
    public static final int STAGE_HEIGHT = 720;
    public static final double PLAYER_MAX_SPEED = 7;
    public static final float PLAYER_GROUND_MOVE_FORCE = 80;
    public static final float PLAYER_AIR_MOVE_FORCE = 20;
    public static final Vec2 PLAYER_START_POSITION = new Vec2(15, 2);
    // the bottom of the map, where the player will fall and die
    public static final int WORLD_BOTTOM = -30;
    // parallax background speed
    public static final double BACKGROUND_SPEED = 30;
}