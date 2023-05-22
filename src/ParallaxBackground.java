import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParallaxBackground {
    List<ParallaxBackgroundLayer> layers;
    public ParallaxBackground(Pane parentPane) {
        layers = new ArrayList<>();
        // closest to player is earliest in array
        layers.add(new ParallaxBackgroundLayer("./img/parallax/parallax1.png", 0.6, parentPane));
        layers.add(new ParallaxBackgroundLayer("./img/parallax/parallax2.png", 0.4, parentPane));
        layers.add(new ParallaxBackgroundLayer("./img/parallax/parallax3.png", 0.2, parentPane));
        layers.add(new ParallaxBackgroundLayer("./img/parallax/parallax4.png", 0.1, parentPane));
        layers.add(new ParallaxBackgroundLayer("./img/parallax/parallax5.png", 0.0, parentPane));
    }
    public void paint(double scrollX, double scrollY) {
        for (ParallaxBackgroundLayer layer : layers) {
            layer.paint(scrollX, scrollY);
        }
    }
}
