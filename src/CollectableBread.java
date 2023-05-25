import javafx.scene.image.ImageView;

enum BreadType {
    BAGUETTE,
    BATARD,
    CROISSANT
}
public class CollectableBread extends FloatingCollectableEntity {
    private final GamePage gamePage;
    private final BreadType breadType;
    public CollectableBread(Vector2 position, BreadType breadType, ImageView imageView, GamePage gamePage) {
        super(position, new CollidableRect(position.x, position.y, 0.3, 0.3), imageView);
        this.gamePage = gamePage;
        this.breadType = breadType;
    }

    @Override
    public void getCollected() {
        SoundMixer.playSound("ding.wav");
        gamePage.numBreadCollected++;
        super.getCollected();
    }
}