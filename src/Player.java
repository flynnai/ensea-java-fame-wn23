import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Translate;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

public class Player extends PhysicsEntity implements GameConstants {
    private final float PLAYER_WIDTH = 0.5f;
    private final float PLAYER_HEIGHT = 1f;
    private int numTouchingGround = 0;
    private int numTouchingLeftSide = 0;
    private int numTouchingRightSide = 0;
    private int bodyNumTouchingGround = 0;
    Rectangle playerDisplayRect;
    Rectangle groundDetectorDisplayRect;

    Rectangle leftSideDetectorDisplayRect;
    Rectangle rightSideDetectorDisplayRect;
    int framesUntilCanJump = 0;


    public Player(Pane pane, List<List<Tile>> tileMatrix) {
        super(PLAYER_START_POSITION, new Vector2(0, 0), new ArrayList<>(), tileMatrix);

        // define rectangular player shape
        this.fixtureShapes.add(new Rectangle(-PLAYER_WIDTH / 2, -PLAYER_HEIGHT, PLAYER_WIDTH, PLAYER_HEIGHT));

        // set up player JavaFX element
        playerDisplayRect = new Rectangle(PLAYER_WIDTH * TILE_SIZE, PLAYER_HEIGHT * TILE_SIZE);
        pane.getChildren().add(playerDisplayRect);
    }

    public void move(Dictionary<UserInput, Boolean> inputsPressed, double timeDeltaSeconds) {
        Vector2 newVelocity = new Vector2(this.getVelocity());

        if (framesUntilCanJump > 0) {
            framesUntilCanJump--;
        } else if (inputsPressed.get(UserInput.UP)) {
            if (this.isTouchingGround() && this.getVelocity().y < 3) {
                newVelocity.y = 10;
                // we want velocity set for jump only 1 time
                framesUntilCanJump = 15;
            }
        }

        if (inputsPressed.get(UserInput.DOWN)) {
            // for later
        }
        if (inputsPressed.get(UserInput.LEFT)) {
            if (this.getVelocity().x > -PLAYER_MAX_SPEED) {
                newVelocity.x += (this.isTouchingGround() ? -PLAYER_GROUND_MOVE_FORCE : -PLAYER_AIR_MOVE_FORCE) * timeDeltaSeconds;
            }
        } else if (inputsPressed.get(UserInput.RIGHT)) {
            if (this.getVelocity().x < PLAYER_MAX_SPEED) {
                newVelocity.x += (this.isTouchingGround() ? PLAYER_GROUND_MOVE_FORCE : PLAYER_AIR_MOVE_FORCE) * timeDeltaSeconds;
            }
        }

        newVelocity.x *= 0.99;

        newVelocity.y -= 9.81 * timeDeltaSeconds;
        newVelocity.y *= 0.99;

        this.setVelocity(newVelocity);

        // move according to velocity, check collisions
        super.move(timeDeltaSeconds);

        if (this.getPosition().y < WORLD_BOTTOM) {
            this.setPosition(PLAYER_START_POSITION);
            System.out.println("Setting player to these coords: " + PLAYER_START_POSITION);
            this.setVelocity(new Vector2(0, 0));
        }

    }

    public void paint(double scrollX, double scrollY) {
        playerDisplayRect.setX((this.getPosition().x - PLAYER_WIDTH / 2 - scrollX) * TILE_SIZE);
        playerDisplayRect.setY((this.getPosition().y - PLAYER_HEIGHT - scrollY) * TILE_SIZE * -1);
    }

    public boolean isTouchingGround() {
        return numTouchingGround > 0;
    }
}