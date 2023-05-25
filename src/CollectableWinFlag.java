import javafx.scene.image.ImageView;

public class CollectableWinFlag extends FloatingCollectableEntity {
    private final GamePage gamePage;
    public CollectableWinFlag(Vector2 position, ImageView imageView, GamePage gamePage) {
        super(position, new CollidableRect(position.x, position.y, 0.3, 0.3), imageView);
        this.gamePage = gamePage;
    }

    @Override
    public void getCollected() {
        SoundMixer.playSound("win_flag.wav");
        gamePage.finishGame();
        super.getCollected();
    }
}