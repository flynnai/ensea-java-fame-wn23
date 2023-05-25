import javafx.scene.image.ImageView;

public class FloatingCollectableEntity extends CollectableEntity {
    ImageView imageView;
    double floatOffset = 0;
    static final double FLOAT_INTENSITY = 0.05;
    static final double FLOAT_SPEED = 2; // seconds per full oscillation
    static final double GLIMMER_REPEAT_NUM_TILES = 5; // tiles after which the glimmer resets

    public FloatingCollectableEntity(Vector2 position, CollidableShape hitArea, ImageView imageView) {
        super(position, hitArea);
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

}