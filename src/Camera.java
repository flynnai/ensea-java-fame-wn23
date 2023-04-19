import org.jbox2d.common.Vec2;

public class Camera implements GameConstants {
    private double scrollX;
    private double scrollY;
    private Player player;

    public Camera(Player player) {
        this.player = player;
        Vec2 playerPos = player.getWorldPosition();
        scrollX = playerPos.x - STAGE_WIDTH / TILE_SIZE / 2;
        scrollY = playerPos.y + STAGE_HEIGHT / TILE_SIZE / 2;
    }

    public void move(double timeDeltaSeconds) {
        // scroll x, y is the coordinates of the top left of the screen
        Vec2 playerPos = player.getWorldPosition();
        // TODO this timeDeltaSeconds logic will break for >1 second timeDeltaSeconds's
        double targetScrollX = (playerPos.x - STAGE_WIDTH / TILE_SIZE / 2);
        double targetScrollY = (playerPos.y + STAGE_HEIGHT / TILE_SIZE / 2);

        // don't scroll past bottom of stage
        targetScrollY = Math.max(targetScrollY, WORLD_BOTTOM + STAGE_HEIGHT / TILE_SIZE);

        scrollX += (targetScrollX - scrollX) * timeDeltaSeconds * 2;
        scrollY += (targetScrollY - scrollY) * timeDeltaSeconds * 2;
    }

    public double getScrollX() {
        return scrollX;
    }

    public double getScrollY() {
        return scrollY;
    }
}