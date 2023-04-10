import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

import java.util.*;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;

public class GUI extends Application {
    private double scrollX;
    private double scrollY;
    private double sx;
    private double sy;

    private Dictionary<String, Boolean> inputsPressed;
    long startNanoTime;


    @Override
    public void start(Stage primaryStage) throws Exception {
        final int TILE_SIZE = 25;
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

        // set up physics world
        World world = new World();
        Rectangle playerRect = new Rectangle(1, 2);
        Body playerBody = new Body();
        BodyFixture playerFixture = new BodyFixture(playerRect);
        playerFixture.setDensity(1);
        Mass bodyMass = playerFixture.createMass();
        playerBody.addFixture(playerFixture);
        playerBody.setMass(bodyMass);
        Transform playerStart = new Transform();
        playerStart.setTranslation(new Vector2(15, 2));
        playerBody.setTransform(playerStart);
        world.addBody(playerBody);


        // set up tilemap
        TileMap tilemap = new TileMap(
                "./img/sprite_sheets/mario_tileset.png",
                32,
                1,
                "./data/tile_maps/mario4_format2.json",
                pane,
                new Rectangle2D(0, 0, scene.getWidth(), scene.getHeight()),
                TILE_SIZE,
                world
        );


        // set up player JavaFX element
        javafx.scene.shape.Rectangle playerDisplayRect = new javafx.scene.shape.Rectangle(1 * TILE_SIZE, 2 *  TILE_SIZE);
        pane.getChildren().add(playerDisplayRect);

        startNanoTime = System.nanoTime();
        AnimationTimer timer = new AnimationTimer() {
            public void handle(long currentNanoTime) {
                long elapsedNanoSeconds = currentNanoTime - startNanoTime;
                startNanoTime = currentNanoTime;

                // movement
                if (inputsPressed.get("up")) {
                    sy -= 0.01;
                } else if (inputsPressed.get("down")) {
                    sy += 0.01;
                }
                if (inputsPressed.get("left")) {
                    sx -= 0.01;
                } else if (inputsPressed.get("right")) {
                    sx += 0.01;
                }
                sx *= 0.9;
                sy *= 0.9;
                scrollX += sx;
                scrollY += sy;

                world.update(((double) elapsedNanoSeconds) / 10e8);
                Vector2 position = playerBody.getWorldCenter();
                scrollX = -position.x + 5;
                scrollY = position.y + 10;

                tilemap.paint(scrollX, scrollY);
                playerDisplayRect.setTranslateX((scrollX + playerBody.getWorldCenter().x) * TILE_SIZE);
                playerDisplayRect.setTranslateY((scrollY - playerBody.getWorldCenter().y) * TILE_SIZE);
                Rotate rotation = new Rotate();
                rotation.setPivotX(0.5 * TILE_SIZE);
                rotation.setPivotY(1 * TILE_SIZE);
                rotation.setAngle(-Math.toDegrees(playerBody.getTransform().getRotation()));
                playerDisplayRect.getTransforms().clear();
                playerDisplayRect.getTransforms().add(new Translate(TILE_SIZE * -0.5, TILE_SIZE * -1));
                playerDisplayRect.getTransforms().add(rotation);
                System.out.println(playerBody.getWorldCenter() + " scroll: " + scrollX + " " + scrollY);

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
