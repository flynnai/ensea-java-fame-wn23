import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;

public class PlayerAnimation extends ImageView implements GameConstants {
    enum AnimationMode {
        RUNNING,
        STANDING,
        JUMPING,
        ROLLING,
    }
    enum Direction {
        LEFT,
        RIGHT
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
            }
        }



        if (velocity.y > 0.2 && (mode == AnimationMode.RUNNING || mode == AnimationMode.STANDING)) {
            if (mode != AnimationMode.JUMPING) {
                frameNum = 81;
            }
            mode = AnimationMode.JUMPING;
        } else if (player.wasTouchingGround && mode == AnimationMode.JUMPING) {
            mode = AnimationMode.STANDING;
        }

        if (player.wasTouchingGround && mode != AnimationMode.ROLLING) {
            if (Math.abs(velocity.x) < 1.0) {
                mode = AnimationMode.STANDING;
            } else {
                if (mode != AnimationMode.RUNNING) {
                    frameNum = 12;
                }
                mode = AnimationMode.RUNNING;
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
        final int FRAME_PADDING = 50;
        final int SPRITESHEET_FRAMES_WIDE = 24;
        Rectangle cropArea = new Rectangle(FRAME_SIZE - FRAME_PADDING * 2, FRAME_SIZE - FRAME_PADDING * 2);
        cropArea.setTranslateX(FRAME_SIZE * (frameNum % SPRITESHEET_FRAMES_WIDE) + FRAME_PADDING); // X position of the crop area
        cropArea.setTranslateY(FRAME_SIZE * Math.floor((double) frameNum / SPRITESHEET_FRAMES_WIDE) + FRAME_PADDING); // Y position of the crop area

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
        this.setY((player.getPosition().y + PLAYER_HEIGHT / 2 - scrollY) * TILE_SIZE * -1);


    }
}