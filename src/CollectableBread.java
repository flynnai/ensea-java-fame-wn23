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
    public CollectableBread(Vector2 position, BreadType type, ImageView imageView) {
        super(position, 0.3);
        this.imageView = imageView;
    }

    @Override
    public void move(Player player) {
        super.move(player);
    }

    @Override
    public void paint(double scrollX, double scrollY) {
        // hide if collected
        imageView.setVisible(!hasBeenCollected);
        imageView.relocate(
                TILE_SIZE * (position.x - 0.5 - scrollX),
                TILE_SIZE * (position.y + 0.5 - scrollY) * -1
        );
    }

}