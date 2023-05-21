import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Translate;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

enum PlayerActionMode {
    NORMAL,
    EDGE_HANGING,
    EDGE_PRESSING,
}

public class Player extends PhysicsEntity implements GameConstants {
    private int numTouchingGround = 0;
    private int numTouchingLeftSide = 0;
    private int numTouchingRightSide = 0;
    private int bodyNumTouchingGround = 0;
    Rectangle playerDisplayRect;
    Rectangle groundDetectorDisplayRect;

    Rectangle leftSideDetectorDisplayRect;
    Rectangle rightSideDetectorDisplayRect;
    int framesUntilCanJump = 0;
    private PlayerAnimation animation;
    public PlayerActionMode actionMode = PlayerActionMode.NORMAL;
    double timeLeftTillCanHang = 0.0;

    public Player(Pane pane, List<List<Tile>> tileMatrix) {
        super(PLAYER_START_POSITION, new Vector2(0, 0), new CollidableRect(0, 0, PLAYER_WIDTH, PLAYER_HEIGHT), tileMatrix);

        // set up player JavaFX element
        playerDisplayRect = new Rectangle(PLAYER_WIDTH * TILE_SIZE, PLAYER_HEIGHT * TILE_SIZE);
        pane.getChildren().add(playerDisplayRect);
        animation = new PlayerAnimation(this);
        pane.getChildren().add(animation);
    }

    public void move(Dictionary<UserInput, Boolean> inputsPressed, double timeDeltaSeconds) {
        Vector2 newVelocity = new Vector2(this.getVelocity());

        if (actionMode == PlayerActionMode.NORMAL) {
            if (framesUntilCanJump > 0) {
                framesUntilCanJump--;
            } else if (inputsPressed.get(UserInput.UP)) {
                if (wasTouchingGround && this.getVelocity().y < 3) {
                    newVelocity.y = PLAYER_JUMP_VELOCITY;
                    // we want velocity set for jump only 1 time
                    framesUntilCanJump = 15;
                }
            }

            if (inputsPressed.get(UserInput.DOWN)) {
                // for later
            }
            if (inputsPressed.get(UserInput.LEFT)) {
                if (this.getVelocity().x > -PLAYER_MAX_SPEED) {
                    newVelocity.x += (wasTouchingGround ? -PLAYER_GROUND_MOVE_FORCE : -PLAYER_AIR_MOVE_FORCE) * timeDeltaSeconds;
                }
            } else if (inputsPressed.get(UserInput.RIGHT)) {
                if (this.getVelocity().x < PLAYER_MAX_SPEED) {
                    newVelocity.x += (wasTouchingGround ? PLAYER_GROUND_MOVE_FORCE : PLAYER_AIR_MOVE_FORCE) * timeDeltaSeconds;
                }
            }

            newVelocity.y -= 9.81 * timeDeltaSeconds;

        } else if (actionMode == PlayerActionMode.EDGE_HANGING) {
            if (inputsPressed.get(UserInput.UP)) {
                animation.initiateClimbUpFromHanging();
            }

            if (animation.direction == Direction.RIGHT && inputsPressed.get(UserInput.LEFT)) {
                animation.initiateHangingPressOff();
                actionMode = PlayerActionMode.EDGE_PRESSING;
            }

            if (inputsPressed.get(UserInput.DOWN)) {
                animation.initiateFallFromHanging();
                endHanging();
            }
        }

        this.setVelocity(newVelocity);

        // move according to velocity, check collisions
        super.move(timeDeltaSeconds);

        if (actionMode == PlayerActionMode.NORMAL) {
            if (wasTouchingGround && newVelocity.y < -0.8 * PLAYER_JUMP_VELOCITY && Math.abs(newVelocity.x) > PLAYER_MAX_SPEED * 0.7) {
                animation.initiateRolling();
            }

            timeLeftTillCanHang = Math.max(0.0, timeLeftTillCanHang - timeDeltaSeconds);
            if (getVelocity().y < -0.1
                    && timeLeftTillCanHang <= 0.01
                    && isPointTouchingTerrain(getPosition().add(new Vector2(PLAYER_WIDTH / 2 + 0.1, PLAYER_HEIGHT * 0.2)))
                    && !isPointTouchingTerrain(getPosition().add(new Vector2(PLAYER_WIDTH / 2 + 0.1, PLAYER_HEIGHT * 0.2 + 0.1)))) {
                actionMode = PlayerActionMode.EDGE_HANGING;
                animation.initiateHanging(Direction.RIGHT);
                setVelocity(new Vector2(0, 0));
                // relocate to be perfectly far from the edge
                setPosition(new Vector2(
                    Math.floor(getPosition().x + PLAYER_WIDTH / 2 + 0.1) - PLAYER_WIDTH * 0.5,
                    Math.floor(getPosition().y + PLAYER_HEIGHT / 2 + 0.1) + PLAYER_HEIGHT * 0.05
                ));
            }
        } else if (actionMode == PlayerActionMode.EDGE_HANGING) {

        }


        if (this.getPosition().y < WORLD_BOTTOM) {
            this.setPosition(PLAYER_START_POSITION);
            System.out.println("Setting player to these coords: " + PLAYER_START_POSITION);
            this.setVelocity(new Vector2(0, 0));
        }

        animation.move(timeDeltaSeconds);
    }

    public void paint(double scrollX, double scrollY) {
//        playerDisplayRect.setX((this.getPosition().x - PLAYER_WIDTH / 2 - scrollX) * TILE_SIZE);
//        playerDisplayRect.setY((this.getPosition().y + PLAYER_HEIGHT / 2 - scrollY) * TILE_SIZE * -1);

        animation.paint(scrollX, scrollY);
    }

    public void endHanging() {
        actionMode = PlayerActionMode.NORMAL;
        timeLeftTillCanHang = 1.0;
    }
}