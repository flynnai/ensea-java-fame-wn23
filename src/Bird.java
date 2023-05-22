import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

public class Bird extends BenignEntity {
    ImageView imageView;
    Player player;
    int frameNum = 0;
    Direction direction = Direction.RIGHT;
    public Bird(Vector2 position, Player player, Pane pane) {
        super(position);
        imageView = new ImageView(new Image("file:img/sprite_sheets/bird.png"));
        pane.getChildren().add(imageView);
        this.player = player;
    }

    @Override
    public void move(double timeDeltaSeconds) {

    }

    @Override
    public void paint(double scrollX, double scrollY) {
        // set frame to `frameNum` and move image relative scrollX, scrollY

        final double FRAME_SIZE = 32;
        final double BIRD_SIZE = 0.4; // tiles

        Rectangle cropArea = new Rectangle(FRAME_SIZE, FRAME_SIZE);
        cropArea.setTranslateX(FRAME_SIZE * frameNum);
        cropArea.setTranslateY(0);
        imageView.setViewport(new Rectangle2D(cropArea.getTranslateX(), cropArea.getTranslateY(), cropArea.getWidth(), cropArea.getHeight()));

        imageView.setScaleX(direction == Direction.LEFT ? -1 : 1);
        imageView.setFitWidth(BIRD_SIZE * TILE_SIZE);
        imageView.setFitHeight(BIRD_SIZE * TILE_SIZE);
        imageView.setX((position.x - BIRD_SIZE / 2 - scrollX) * TILE_SIZE);
        imageView.setY((position.y + BIRD_SIZE / 2 - scrollY) * TILE_SIZE * -1);
    }
}