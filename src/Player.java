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
    WALL_RUNNING,
    SLIDING,
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
    private double lastWallJumpedTime = 0;
    private Direction lastWallJumpedDirection = null;
    private double slideStartTime = 0;
    public Player(Pane pane, List<List<Tile>> tileMatrix) {
        super(PLAYER_START_POSITION, new Vector2(0, 0), playerStandingHitBox, tileMatrix);

        // set up player JavaFX element
        playerDisplayRect = new Rectangle(PLAYER_WIDTH * TILE_SIZE, PLAYER_HEIGHT * TILE_SIZE);
//        pane.getChildren().add(playerDisplayRect);
        animation = new PlayerAnimation(this);
        pane.getChildren().add(animation);

    }

    private void checkForEdgeHang() {
        Vector2 sensorOffset = new Vector2(PLAYER_WIDTH / 2 + 0.1, PLAYER_HEIGHT * 0.4);
        double jumpHandle = 0.2; // amount of space to check "touching below, not touching above" for
        boolean isTouchingRight = isPointTouchingTerrain(getPosition().add(sensorOffset))
                && !isPointTouchingTerrain(getPosition().add(sensorOffset.add(new Vector2(0, jumpHandle))));
        boolean isTouchingLeft = isPointTouchingTerrain(getPosition().add(sensorOffset.flipX()))
                && !isPointTouchingTerrain(getPosition().add(sensorOffset.flipX().add(new Vector2(0, jumpHandle))));
        if (isTouchingLeft || isTouchingRight) {
            actionMode = PlayerActionMode.EDGE_HANGING;
            animation.initiateHanging(isTouchingRight ? Direction.RIGHT : Direction.LEFT);
            setVelocity(new Vector2(0, 0));
            // relocate to be perfectly far from the edge
            Vector2 newPosition = new Vector2(0, Math.floor(getPosition().y + PLAYER_HEIGHT / 2 + jumpHandle) - PLAYER_HEIGHT * 0.1);
            if (isTouchingRight) {
                newPosition.x = Math.floor(getPosition().x + PLAYER_WIDTH / 2 + 0.1) - PLAYER_WIDTH * 0.7;
            } else {
                newPosition.x = Math.ceil(getPosition().x - PLAYER_WIDTH / 2 - 0.1) + PLAYER_WIDTH * 0.69;
            }
            setPosition(newPosition);
        }
    }

    private Vector2 getWallRunCheckPoint(Direction direction) {
        return getPosition().add(new Vector2(
                (PLAYER_WIDTH / 2 + 0.1) * (direction == Direction.RIGHT ? 1 : -1),
                -PLAYER_HEIGHT / 2
        ));
    }

    private void checkForWallRun(Direction direction, Vector2 newVelocity, boolean hasJumpedRecently) {
        Vector2 checkPoint = getWallRunCheckPoint(direction);
        boolean playerIsMovingUpFast = getVelocity().getMagnitude() > PLAYER_MAX_SPEED * 0.5
                && getVelocity().y > PLAYER_MAX_SPEED * 0.1;

        if (isPointTouchingTerrain(checkPoint) && (playerIsMovingUpFast
                || (hasJumpedRecently && lastWallJumpedDirection != direction && getVelocity().y > 0)
        )) {
            actionMode = PlayerActionMode.WALL_RUNNING;
            animation.initiateWallRunning(direction);
            // redirect force upwards
            newVelocity.y = newVelocity.getMagnitude() * WALL_RUN_BOOSTER;
            newVelocity.x = 0;

            // move perfectly far from edge
            Vector2 newPosition = getPosition();
            if (direction == Direction.RIGHT) {
                // align hitbox to right side
                newPosition.x = Math.floor(checkPoint.x) - PLAYER_WIDTH / 2;
                // move left a bit
                newPosition.x -= PLAYER_WIDTH * 0.15;
            } else {
                // align hitbox to left side
                newPosition.x = Math.ceil(checkPoint.x) + PLAYER_WIDTH / 2;
                // move right a bit
                newPosition.x += PLAYER_WIDTH * 0.15;
            }
            setPosition(newPosition);
        }
    }

    public void move(Dictionary<UserInput, Boolean> inputsPressed, double currentSecondsTime, double timeDeltaSeconds) {
        Vector2 newVelocity = new Vector2(this.getVelocity());

        if (actionMode == PlayerActionMode.NORMAL) {
            if (framesUntilCanJump > 0) {
                framesUntilCanJump--;
            } else if (inputsPressed.get(UserInput.UP)) {
                if (wasTouchingGround && this.getVelocity().y < 3) {
//                    newVelocity.y = PLAYER_JUMP_VELOCITY;
                    animation.initiateJumping();
                    // we want velocity set for jump only 1 time
                    framesUntilCanJump = 15;
                }
            }

            if (wasTouchingGround && Math.abs(getVelocity().x) > 0.5 * PLAYER_MAX_SPEED && inputsPressed.get(UserInput.DOWN)) {
                if (animation.mode != PlayerAnimation.AnimationMode.SLIDING) {
                    animation.initiateSliding();
                    // reassign to smaller hitbox for sliding under things
                    reassignHitBox(playerSlidingHitBox);

                    slideStartTime = currentSecondsTime;
                    actionMode = PlayerActionMode.SLIDING;
                }
            }
            if (inputsPressed.get(UserInput.LEFT)) {
                if (this.getVelocity().x > -PLAYER_MAX_SPEED) {
                    newVelocity.x += (wasTouchingGround ? -PLAYER_GROUND_MOVE_FORCE : -PLAYER_AIR_MOVE_FORCE) * timeDeltaSeconds;
                    if (wasTouchingGround) {
                        // friction interferes with movement, undo it
                        newVelocity.x /= GROUND_FRICTION;
                        newVelocity.x *= RUNNING_FRICTION;
                    }
                }

                checkForWallRun(Direction.LEFT, newVelocity, (currentSecondsTime - lastWallJumpedTime) < REPEAT_WALL_JUMP_THRESHOLD);
            } else if (inputsPressed.get(UserInput.RIGHT)) {
                if (this.getVelocity().x < PLAYER_MAX_SPEED) {
                    newVelocity.x += (wasTouchingGround ? PLAYER_GROUND_MOVE_FORCE : PLAYER_AIR_MOVE_FORCE) * timeDeltaSeconds;
                    if (wasTouchingGround) {
                        // friction interferes with movement, undo it
                        newVelocity.x /= GROUND_FRICTION;
                        newVelocity.x *= RUNNING_FRICTION;
                    }
                }

                checkForWallRun(Direction.RIGHT, newVelocity, (currentSecondsTime - lastWallJumpedTime) < REPEAT_WALL_JUMP_THRESHOLD);
            }

            newVelocity.y -= GRAVITY * timeDeltaSeconds;

        } else if (actionMode == PlayerActionMode.EDGE_HANGING) {
            if (inputsPressed.get(UserInput.UP)) {
                if (animation.mode != PlayerAnimation.AnimationMode.HANGING_EDGE_CLIMBING) {
                    animation.initiateClimbUpFromHanging();
                }
            }

            if (animation.direction == Direction.RIGHT && inputsPressed.get(UserInput.LEFT)
                    || animation.direction == Direction.LEFT && inputsPressed.get(UserInput.RIGHT)) {
                animation.initiateHangingPressOff();
                actionMode = PlayerActionMode.EDGE_PRESSING;
            }

            if (inputsPressed.get(UserInput.DOWN)) {
                animation.initiateFallFromHanging();
                setPosition(getPosition().add(new Vector2(0, -0.15 * PLAYER_HEIGHT)));
                endHanging();
            }
        } else if (actionMode == PlayerActionMode.WALL_RUNNING) {
            newVelocity.y -= GRAVITY * timeDeltaSeconds;
            Direction dir = animation.direction;
            if (newVelocity.y < 0 || !isPointTouchingTerrain(getWallRunCheckPoint(dir))) {
                actionMode = PlayerActionMode.NORMAL;
                animation.endWallRunning();
                setPosition(getPosition().add(
                        new Vector2(PLAYER_WIDTH * 0.15 * (dir == Direction.RIGHT ? 1 : -1), 0)
                ));
                checkForEdgeHang();
            } else if (dir == Direction.RIGHT && inputsPressed.get(UserInput.LEFT)
                    || dir == Direction.LEFT && inputsPressed.get(UserInput.RIGHT)) {
                // end wall running and push off to opposite side
                actionMode = PlayerActionMode.NORMAL;
                animation.initiateHangingPressOff();

                newVelocity.x = PLAYER_JUMP_VELOCITY * 0.5 * (dir == Direction.RIGHT ? 1 : -1);
                newVelocity.y *= 0.3;
                lastWallJumpedTime = currentSecondsTime;
                lastWallJumpedDirection = dir;
            }
        } else if (actionMode == PlayerActionMode.SLIDING) {

            boolean isHeadBlocked = isPointTouchingTerrain(getPosition().add(new Vector2(-PLAYER_WIDTH / 2, PLAYER_HEIGHT / 2)))
                    || isPointTouchingTerrain(getPosition().add(new Vector2(PLAYER_WIDTH / 2, PLAYER_HEIGHT / 2)));
            newVelocity.y -= GRAVITY * timeDeltaSeconds;
            newVelocity.x /= GROUND_FRICTION;
            if (!isHeadBlocked) {
                newVelocity.x *= 0.99;
            } else {
                newVelocity.x = Math.max(Math.abs(newVelocity.x), PLAYER_MAX_SPEED / 3) * (newVelocity.x > 0 ? 1 : -1);
            }

            boolean isFalling = !wasTouchingGround && getVelocity().y < -0.5;
            double slideInterval = currentSecondsTime - slideStartTime;

            final double SLIDE_MIN_LENGTH = 0.6;
            if (!isHeadBlocked && (isFalling || (!inputsPressed.get(UserInput.DOWN) && slideInterval > SLIDE_MIN_LENGTH))) {
                actionMode = PlayerActionMode.NORMAL;
                animation.endSliding();
                reassignHitBox(playerStandingHitBox);
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
            if (getVelocity().y < -0.1 && timeLeftTillCanHang <= 0.01) {
                checkForEdgeHang();
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
        timeLeftTillCanHang = 0.5;
    }
}