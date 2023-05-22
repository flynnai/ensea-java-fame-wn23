import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

enum BreadType {
    BAGUETTE,
    BATARD,
    CROISSANT
}
public class CollectableBread extends CollectableEntity {
    ImageView imageView;
    double floatOffset = 0;
    static final double FLOAT_INTENSITY = 0.05;
    static final double FLOAT_SPEED = 2; // seconds per full oscillation
    static final double GLIMMER_REPEAT_NUM_TILES = 5; // tiles after which the glimmer resets
    public CollectableBread(Vector2 position, BreadType type, ImageView imageView) {
        super(position, new CollidableRect(position.x, position.y, 0.3, 0.3));
        this.imageView = imageView;
    }

    @Override
    public void move(double currentSecondsTime, Player player) {
        super.move(currentSecondsTime, player);

        double normalizedTime = currentSecondsTime / FLOAT_SPEED;
        double glimmerOffsetRatio = ((position.x % GLIMMER_REPEAT_NUM_TILES) / GLIMMER_REPEAT_NUM_TILES);
        normalizedTime += glimmerOffsetRatio;
        floatOffset = Math.sin(normalizedTime * 2 * Math.PI) * FLOAT_INTENSITY;
    }

    @Override
    public void paint(double scrollX, double scrollY) {
        // hide if collected
        imageView.setVisible(!hasBeenCollected);
        imageView.relocate(
                TILE_SIZE * (position.x - 0.5 - scrollX),
                TILE_SIZE * (position.y + 0.5 + floatOffset - scrollY) * -1
        );
    }

    @Override
    public void getCollected() {
        SoundMixer.playSound("ding.wav");
        super.getCollected();
    }
}