import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParallaxBackground {
    List<ParallaxBackgroundLayer> layers;
    public ParallaxBackground(Pane parentPane) {
        layers = new ArrayList<>();
        layers.add(new ParallaxBackgroundLayer("./img/parallax/layer_03_1920 x 1080.png", 0.6, parentPane));
        layers.add(new ParallaxBackgroundLayer("./img/parallax/layer_04_1920 x 1080.png", 0.4, parentPane));
        layers.add(new ParallaxBackgroundLayer("./img/parallax/layer_06_1920 x 1080.png", 0.2, parentPane));
        layers.add(new ParallaxBackgroundLayer("./img/parallax/layer_08_1920 x 1080.png", 0.0, parentPane));
    }
    public void paint(double scrollX, double scrollY) {
        for (ParallaxBackgroundLayer layer : layers) {
            layer.paint(scrollX, scrollY);
        }
    }
}
