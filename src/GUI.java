import javafx.animation.AnimationTimer;
import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.image.Image;
import javafx.util.Duration;

public class GUI extends Application {
    private double scrollX;
    private double scrollY;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Jaconde Test");

        // set up scene and group
        Group root = new Group();
        Scene scene = new Scene(root, 600, 400, true);
        primaryStage.setScene(scene);
        primaryStage.show();

        // set up pane
        Pane pane = new Pane();
        pane.setTranslateX(0);
        pane.setTranslateY(0);
        root.getChildren().add(pane);

        // set up tilemap
        TileMap tilemap = new TileMap(
                "./img/sprite_sheets/mario_tileset.png",
                32,
                1,
                "./data/tile_maps/mario4_format2.json",
                pane,
                new Rectangle2D(0, 0, scene.getWidth(), scene.getHeight())
        );


        // sprite images
//            Image earthImg = new Image("file:./img/earth.png");
//            ImageView earthView = new ImageView(earthImg);
//            earthView.setX(300);
//            earthView.setY(200);
//            double earthSize = earthImg.getWidth() * 0.5;
//            earthView.setFitWidth(earthSize);
//            earthView.setFitHeight(earthSize);
//            pane.getChildren().add(earthView);

        scrollX = 0;
        scrollY = 0;
        AnimationTimer timer = new AnimationTimer() {
            public void handle(long currentNanoTime) {
                scrollX -= 0.01;
                scrollY -= 0.01;

                tilemap.paint(scrollX, scrollY);
            }

        };
        timer.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
