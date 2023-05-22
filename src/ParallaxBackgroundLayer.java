import javafx.geometry.Side;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

class ParallaxBackgroundLayer implements GameConstants {
    Pane pane;
    Image image;
    double speed;

    public ParallaxBackgroundLayer(String imagePath, double speed, Pane parentPane) {
        image = new Image("file:" + imagePath);
        pane = new Pane();
        pane.setMinWidth(STAGE_WIDTH);
        pane.setMinHeight(STAGE_HEIGHT);

        parentPane.getChildren().add(pane);
        pane.toBack();
        this.speed = speed;
    }

    public void paint(double scrollX, double scrollY) {

        BackgroundPosition bgPosition = new BackgroundPosition(
                Side.LEFT,
                -scrollX * speed * BACKGROUND_SPEED,
                false,
                Side.TOP,
                (scrollY - WORLD_BOTTOM * 0.4) * speed * BACKGROUND_SPEED,
                false
        );

        BackgroundImage backgroundImage = new BackgroundImage(
                image,
                BackgroundRepeat.REPEAT,
                BackgroundRepeat.NO_REPEAT,
                bgPosition,
                new BackgroundSize(1.0, 1.0, true, true, false, false)
        );

        pane.setBackground(new Background(backgroundImage));
    }
}

