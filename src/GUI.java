import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.contacts.Contact;

import java.util.*;


public class GUI extends Application implements GameConstants {
    private double scrollX;
    private double scrollY;
    private double cameraOffsetX;
    private double cameraOffsetY;

    private Dictionary<UserInput, Boolean> inputsPressed;
    long startNanoTime;






    @Override
    public void start(Stage primaryStage) throws Exception {
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
        World world = new World(new Vec2(0, -9.81f));

        Player player = new Player(world, pane);





        // set up tilemap
        TileMap tilemap = new TileMap(
                "./img/sprite_sheets/mario_tileset.png",
                32,
                1,
                "./data/tile_maps/mario4_format2.json",
                pane,
                new Rectangle2D(0, 0, scene.getWidth(), scene.getHeight()),
                world
        );



        // set up inputs
        inputsPressed = new Hashtable<>();
        InputManager inputManager = new InputManager(scene, inputsPressed);

        startNanoTime = System.nanoTime();
        cameraOffsetX = 0;
        cameraOffsetY = 0;
        AnimationTimer timer = new AnimationTimer() {
            public void handle(long currentNanoTime) {
                long elapsedNanoSeconds = currentNanoTime - startNanoTime;
                startNanoTime = currentNanoTime;

                // movement
                player.move(inputsPressed);

                world.step((float) elapsedNanoSeconds / 1000000000.0f, 6, 2);
                Vec2 playerPos = player.getWorldPosition();

//                cameraOffsetX += (-playerVel.x / 10 - cameraOffsetX) / 2;
//                cameraOffsetY += (playerVel.y / 10 - cameraOffsetY) / 2;

                // scrollX, scrollY are coordinates of top left corner of screen
                scrollX = playerPos.x - STAGE_WIDTH / TILE_SIZE / 2 + cameraOffsetX;
                scrollY = playerPos.y + STAGE_HEIGHT / TILE_SIZE / 2 + cameraOffsetY;

                tilemap.paint(scrollX, scrollY);
                player.paint(scrollX, scrollY);

            }

        };
        timer.start();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
