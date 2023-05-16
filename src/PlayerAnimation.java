import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

public class PlayerAnimation extends ImageView {
    enum AnimationMode {
        RUNNING,
        STANDING,
    }
    int animFrame = 0;
    AnimationMode animMode = AnimationMode.STANDING;
    Player player;
    double lastAnimationTime = 0;
    final double animationSpeed = 0.2;
    public PlayerAnimation(Player player) {
        super(new Image("file:./img/sprite_sheets/parkour_sprite.png"));
        this.player = player;
    }

    public void move(double timeDeltaSeconds) {
        lastAnimationTime += timeDeltaSeconds;
        if (lastAnimationTime > animationSpeed) {
            lastAnimationTime = 0;
            animFrame++;
        }
    }

    public void paint() {
        // set correct frame
        final int FRAME_SIZE = 400;
        final int SPRITESHEET_FRAMES_WIDE = 24;
        Rectangle cropArea = new Rectangle(FRAME_SIZE, FRAME_SIZE);
        cropArea.setTranslateX(FRAME_SIZE * (animFrame % SPRITESHEET_FRAMES_WIDE)); // X position of the crop area
        cropArea.setTranslateY(FRAME_SIZE * Math.floor((double) animFrame / SPRITESHEET_FRAMES_WIDE)); // Y position of the crop area

        this.setViewport(
            new Rectangle2D(
                cropArea.getTranslateX(),
                cropArea.getTranslateY(),
                cropArea.getWidth(),
                cropArea.getHeight()
            )
        );

    }
}