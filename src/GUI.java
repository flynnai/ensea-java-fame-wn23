import javafx.animation.AnimationTimer;
import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.FileInputStream;
import java.util.*;
import java.util.List;

import javafx.scene.image.Image;
import javafx.util.Duration;
import javafx.scene.input.KeyCode;

public class GUI extends Application {
    private double scrollX;
    private double scrollY;
    private double sx;
    private double sy;

    private Dictionary<String, Boolean> inputsPressed;

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
                new Rectangle2D(0, 0, scene.getWidth(), scene.getHeight()),
                45
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
                // movement
                if (inputsPressed.get("up")) {
                    sy -= 0.01;
                } else if (inputsPressed.get("down")) {
                    sy += 0.01;
                }
                if (inputsPressed.get("left")) {
                    sx -= 0.01;
                } else  if (inputsPressed.get("right")){
                    sx += 0.01;
                }
                sx *= 0.9;
                sy *= 0.9;
                scrollX += sx;
                scrollY += sy;

                tilemap.paint(scrollX, scrollY);
            }

        };
        timer.start();


        // set up inputs
        inputsPressed = new Hashtable<>();
        inputsPressed.put("up", false);
        inputsPressed.put("down", false);
        inputsPressed.put("left", false);
        inputsPressed.put("right", false);

        scene.setOnKeyPressed(keyEvent -> {
            switch (keyEvent.getCode()) {
                case UP:
                case W:
                    inputsPressed.put("up", true);
                    break;
                case DOWN:
                case S:
                    inputsPressed.put("down", true);
                    break;
                case LEFT:
                case A:
                    inputsPressed.put("left", true);
                    break;
                case RIGHT:
                case D:
                    inputsPressed.put("right", true);
                    break;
                default:
                    break;
            }

        });

        scene.setOnKeyReleased(keyEvent -> {
            switch (keyEvent.getCode()) {
                case UP:
                case W:
                    inputsPressed.put("up", false);
                    break;
                case DOWN:
                case S:
                    inputsPressed.put("down", false);
                    break;
                case LEFT:
                case A:
                    inputsPressed.put("left", false);
                    break;
                case RIGHT:
                case D:
                    inputsPressed.put("right", false);
                    break;
                default:
                    break;
            }

        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
