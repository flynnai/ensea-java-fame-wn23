import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;

enum Direction {
    LEFT,
    RIGHT
}

public class PlayerAnimation extends ImageView implements GameConstants {
    enum AnimationMode {
        RUNNING,
        STANDING,
        JUMPING,
        ROLLING,
        EDGE_HANGING,
        HANGING_EDGE_CLIMBING,
        FALLING,
        HANGING_PRESS_OFF,
    }

    int frameNum = 0;
    AnimationMode mode = AnimationMode.STANDING;
    Player player;
    double lastAnimationTime = 0;
    final double animationSpeed = 0.07;
    Direction direction = Direction.RIGHT;

    public PlayerAnimation(Player player) {
        super(new Image("file:./img/sprite_sheets/parkour_sprite.png"));
        this.player = player;
    }

    public void initiateRolling() {
        // rolling is frames 35-40
        mode = AnimationMode.ROLLING;
        frameNum = 35;
        lastAnimationTime = 0; // give us a full frame before change
    }

    public void initiateHanging(Direction direction) {
        mode = AnimationMode.EDGE_HANGING;
        frameNum = 68;
        this.direction = direction;
    }

    public void initiateClimbUpFromHanging() {
        mode = AnimationMode.HANGING_EDGE_CLIMBING;
    }
    public void initiateHangingPressOff() {
        mode = AnimationMode.HANGING_PRESS_OFF;
        frameNum = 67;
        lastAnimationTime = 0;
    }
    public void initiateFallFromHanging() {
        mode = AnimationMode.FALLING;
        frameNum = 119;
    }

    public void move(double timeDeltaSeconds) {
        Vector2 velocity = player.getVelocity();

        // change animation frame
        lastAnimationTime += timeDeltaSeconds;
        if (lastAnimationTime > animationSpeed) {
            lastAnimationTime = 0;

            if (mode == AnimationMode.RUNNING) {
                boolean isSlow = Math.abs(velocity.x / PLAYER_MAX_SPEED) < 0.8;
                frameNum++;
                if (isSlow) {
                    // frames 12-21
                    if (frameNum >= 22) {
                        // fast frames, subtract to get to slow
                        frameNum -= 10;
                    }
                } else {
                    // frames 22-31
                    if (frameNum <= 21) {
                        // slow frames, add to get to fast
                        frameNum += 10;
                    }
                    if (frameNum >= 32) {
                        frameNum = 22;
                    }
                }
            } else if (mode == AnimationMode.STANDING) {
                frameNum = 0;
            } else if (mode == AnimationMode.JUMPING) {
                frameNum++;
                if (frameNum > 88) {
                    frameNum = 88;
                }
            } else if (mode == AnimationMode.ROLLING) {
                frameNum++;
                if (frameNum >= 40) {
                    frameNum = 0;
                    mode = AnimationMode.STANDING;
                }
            } else if (mode == AnimationMode.HANGING_EDGE_CLIMBING) {
                frameNum++;
                if (frameNum == 70) {
                    player.setPosition(player.getPosition().add(new Vector2(0, 0.1 * PLAYER_HEIGHT)));
                }
                if (frameNum >= 75) {
                    frameNum = 0;
                    mode = AnimationMode.STANDING;
                    player.endHanging();
                    if (direction == Direction.RIGHT) {
                        // teleport up on edge to the right
                        player.setPosition(new Vector2(
                                Math.floor(player.getPosition().x + PLAYER_WIDTH / 2 + 0.1) + PLAYER_WIDTH * 0.3,
                                Math.floor(player.getPosition().y + PLAYER_HEIGHT / 2 + 0.1) - 1 + PLAYER_HEIGHT / 2
                        ));
                    }
                }
            } else if (mode == AnimationMode.FALLING) {
                frameNum++;
                if (frameNum >= 123) {
                    frameNum = 119;
                }
            } else if (mode == AnimationMode.HANGING_PRESS_OFF) {
                frameNum--;
                if (frameNum <= 66) {
                    mode = AnimationMode.JUMPING;
                    frameNum = 81;
                    Vector2 jumpVelocity = new Vector2(4, 4);
                    player.setVelocity(new Vector2(
                        jumpVelocity.x * (direction == Direction.RIGHT ? -1 : 1),
                        jumpVelocity.y
                    ));

                    player.endHanging();
                }
            }
        }



        if (velocity.y > 0.2 && (mode == AnimationMode.RUNNING || mode == AnimationMode.STANDING)) {
            if (mode != AnimationMode.JUMPING) {
                frameNum = 81;
            }
            mode = AnimationMode.JUMPING;
        } else if (player.wasTouchingGround && (mode == AnimationMode.JUMPING || mode == AnimationMode.FALLING)) {
            mode = AnimationMode.STANDING;
        }

        if (player.wasTouchingGround) {
            if (mode == AnimationMode.STANDING || mode == AnimationMode.JUMPING || mode == AnimationMode.RUNNING) {
                if (Math.abs(velocity.x) < 1.0) {
                    mode = AnimationMode.STANDING;
                } else {
                    if (mode != AnimationMode.RUNNING) {
                        frameNum = 12;
                    }
                    mode = AnimationMode.RUNNING;
                }
            }
        } else {
            if (velocity.y < -2 && mode != AnimationMode.FALLING) {
                mode = AnimationMode.FALLING;
                frameNum = 119;
            }
        }


        if (velocity.x > 0.1) {
            direction = Direction.RIGHT;
        } else if (velocity.x < -0.1){
            direction = Direction.LEFT;
        }

        // 35 - 40 for rolling
    }

    public void paint(double scrollX, double scrollY) {
        // visually set correct frame by cropping
        final int FRAME_SIZE = 400;
        final double FRAME_PADDING_PERCENT = 50.0 / 400;
        final int SPRITESHEET_FRAMES_WIDE = 24;
        Rectangle cropArea = new Rectangle(FRAME_SIZE, FRAME_SIZE);
        cropArea.setTranslateX(FRAME_SIZE * (frameNum % SPRITESHEET_FRAMES_WIDE)); // X position of the crop area
        cropArea.setTranslateY(FRAME_SIZE * Math.floor((double) frameNum / SPRITESHEET_FRAMES_WIDE)); // Y position of the crop area

        this.setViewport(
                new Rectangle2D(
                        cropArea.getTranslateX(),
                        cropArea.getTranslateY(),
                        cropArea.getWidth(),
                        cropArea.getHeight()
                )
        );

        this.setScaleX(direction == Direction.LEFT ? -1 : 1);

        this.setFitWidth(PLAYER_HEIGHT * TILE_SIZE);
        this.setFitHeight(PLAYER_HEIGHT * TILE_SIZE);
        this.setX((player.getPosition().x - PLAYER_HEIGHT / 2 - scrollX) * TILE_SIZE);
        this.setY((player.getPosition().y + PLAYER_HEIGHT / 2 - FRAME_PADDING_PERCENT * PLAYER_HEIGHT - scrollY) * TILE_SIZE * -1);


    }
}