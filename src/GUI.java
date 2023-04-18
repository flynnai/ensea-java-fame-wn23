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

        // set up player in world
        Player player = new Player(world, pane);

        // set up camera to follow player
        Camera camera = new Camera(player);

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

        // set up input keypress listeners
        inputsPressed = new Hashtable<>();
        InputManager inputManager = new InputManager(scene, inputsPressed);

        // initialize time
        startNanoTime = System.nanoTime();
        AnimationTimer timer = new AnimationTimer() {
            public void handle(long currentNanoTime) {
                // find amount of time passed since last loop run
                long elapsedNanoSeconds = currentNanoTime - startNanoTime;
                startNanoTime = currentNanoTime;
                double timeDeltaSeconds = elapsedNanoSeconds / 1000000000.0f;


                // MOVE all entities, and the camera
                // apply physics to bodies
                player.move(inputsPressed);
                // move the world
                world.step((float) timeDeltaSeconds, 6, 2);
                // move the camera according to new positions
                camera.move(timeDeltaSeconds);

                // PAINT all entities in the correct positions
                // note: scrollX, scrollY are coordinates of top left corner of screen
                double scrollX = camera.getScrollX();
                double scrollY = camera.getScrollY();

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
