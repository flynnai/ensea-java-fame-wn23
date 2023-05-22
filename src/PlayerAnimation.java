import javafx.animation.Animation;
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
        PRE_JUMPING,
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
    final double animationSpeed = 0.06;
    Direction direction = Direction.RIGHT;

    public PlayerAnimation(Player player) {
        super(new Image("file:./img/sprite_sheets/parkour_sprite.png"));
        this.player = player;
    }

    public void initiateJumping() {
        mode = AnimationMode.PRE_JUMPING;
        frameNum = 81;
        lastAnimationTime = 0;
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
        SoundMixer.playSound("grab_edge.wav");
    }

    public void initiateClimbUpFromHanging() {
        mode = AnimationMode.HANGING_EDGE_CLIMBING;
        SoundMixer.playSound("climb_up.wav");
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
                // play sounds on footstep frames
                if (frameNum % 10 == 5) {
                    SoundMixer.playSound("footstep1.wav");
                } else if (frameNum % 10 == 0) {
                    SoundMixer.playSound("footstep2.wav");
                }
            } else if (mode == AnimationMode.STANDING) {
                frameNum = 0;
            } else if (mode == AnimationMode.PRE_JUMPING) {
                frameNum++;
                // actually jump
                velocity = velocity.add(new Vector2(0, PLAYER_JUMP_VELOCITY));
                player.setVelocity(velocity);
                mode = AnimationMode.JUMPING;
                SoundMixer.playSound("jump.wav");
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
                    player.setPosition(player.getPosition().add(new Vector2(0, 0.125 * PLAYER_HEIGHT)));
                }
                if (frameNum >= 75) {
                    frameNum = 0;
                    mode = AnimationMode.STANDING;
                    player.endHanging();
                    // teleport up on edge
                    Vector2 newPosition = new Vector2(
                            0,
                            Math.floor(player.getPosition().y + PLAYER_HEIGHT / 2 + 0.5) - 1 + PLAYER_HEIGHT / 2
                    );
                    if (direction == Direction.RIGHT) {
                        newPosition.x = Math.floor(player.getPosition().x + PLAYER_WIDTH * 0.75 + 0.1) + PLAYER_WIDTH * 0.3;
                    } else {
                        newPosition.x = Math.ceil(player.getPosition().x - PLAYER_WIDTH * 0.75 - 0.1) - PLAYER_WIDTH * 0.3;
                    }
                    player.setPosition(newPosition);

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




        if (player.wasTouchingGround && velocity.y < 0.1) {
            if (mode == AnimationMode.STANDING || mode == AnimationMode.JUMPING || mode == AnimationMode.RUNNING || mode == AnimationMode.FALLING) {
                if (mode == AnimationMode.FALLING || mode == AnimationMode.JUMPING) {
                    // play landing sound
                    SoundMixer.playSound("land_on_ground.wav");
                }
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
    }

    public void paint(double scrollX, double scrollY) {
        // visually set correct frame by cropping
        final int FRAME_SIZE = 400;
        final double FRAME_PADDING_RATIO = 70.0 / 400;
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

        this.setFitWidth(PLAYER_HEIGHT * (1 + FRAME_PADDING_RATIO * 2) * TILE_SIZE);
        this.setFitHeight(PLAYER_HEIGHT * (1 + FRAME_PADDING_RATIO * 2) * TILE_SIZE);
        this.setX((player.getPosition().x - PLAYER_HEIGHT / 2 - PLAYER_HEIGHT * FRAME_PADDING_RATIO - scrollX) * TILE_SIZE);
        this.setY((player.getPosition().y + PLAYER_HEIGHT / 2 + PLAYER_HEIGHT * FRAME_PADDING_RATIO - scrollY) * TILE_SIZE * -1);


    }
}