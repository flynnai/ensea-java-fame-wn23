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
import org.dyn4j.dynamics.Force;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.*;

public class GUI extends Application {
    private double scrollX;
    private double scrollY;
    private double sx;
    private double sy;

    private Dictionary<String, Boolean> inputsPressed;
    long startNanoTime;

    private double cameraOffsetX;
    private double cameraOffsetY;


    @Override
    public void start(Stage primaryStage) throws Exception {
        final int TILE_SIZE = 25;
        final int STAGE_WIDTH = 1280;
        final int STAGE_HEIGHT = 720;
        primaryStage.setTitle("Jaconde Test");

        // set up scene and group
        Group root = new Group();
        Scene scene = new Scene(root, STAGE_WIDTH, STAGE_HEIGHT, true);
        primaryStage.setScene(scene);
        primaryStage.show();

        // set up pane
        Pane pane = new Pane();
        pane.setTranslateX(0);
        pane.setTranslateY(0);
        root.getChildren().add(pane);

        // set up physics world
        World world = new World();
        Body playerBody = new Body();

        // rect player shape
//        Rectangle playerRect = new Rectangle(1, 2);
//        BodyFixture playerFixture = new BodyFixture(playerRect);
//        playerBody.addFixture(playerFixture);

        // two stacked circles shape
        Circle c1 = new Circle(0.5);
        c1.translate(0, 1.5);
        Circle c2 = new Circle(0.5);
        c2.translate(0, 0.5);
        BodyFixture f1 = new BodyFixture(c1);
        BodyFixture f2 = new BodyFixture(c2);
        f1.setDensity(1);
        f2.setDensity(1);
        playerBody.addFixture(f1);
        playerBody.addFixture(f2);

        playerBody.setMass(MassType.FIXED_ANGULAR_VELOCITY);
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
        javafx.scene.shape.Rectangle playerDisplayRect = new javafx.scene.shape.Rectangle(1 * TILE_SIZE, 2 * TILE_SIZE);
        pane.getChildren().add(playerDisplayRect);

        startNanoTime = System.nanoTime();
        cameraOffsetX = 0;
        cameraOffsetY = 0;
        AnimationTimer timer = new AnimationTimer() {
            public void handle(long currentNanoTime) {
                long elapsedNanoSeconds = currentNanoTime - startNanoTime;
                startNanoTime = currentNanoTime;

                // movement
                if (inputsPressed.get("up")) {
                    playerBody.applyForce(new Force(0, 100));
                } else if (inputsPressed.get("down")) {
                    // for later
                }
                if (inputsPressed.get("left")) {
                    playerBody.applyForce(new Force(-20, 0));
                } else if (inputsPressed.get("right")) {
                    playerBody.applyForce(new Force(20, 0));
                }
//                playerBody.translate(0, 0.01);

                world.update(((double) elapsedNanoSeconds) / 10e8);
                Vector2 playerPos = playerBody.getWorldCenter();
                Vector2 playerVel = playerBody.getLinearVelocity();

                cameraOffsetX += (-playerVel.x / 10 - cameraOffsetX) / 2;
                cameraOffsetY += (playerVel.y / 10 - cameraOffsetY) / 2;

                scrollX = -playerPos.x + STAGE_WIDTH / TILE_SIZE / 2 + cameraOffsetX;
                scrollY = playerPos.y + STAGE_HEIGHT / TILE_SIZE / 2 + cameraOffsetY;

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
