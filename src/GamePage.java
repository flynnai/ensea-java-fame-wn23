import javafx.animation.AnimationTimer;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import java.util.Dictionary;
import java.util.Hashtable;

public class GamePage implements GameConstants {
    private Dictionary<UserInput, Boolean> inputsPressed;
    private long startNanoTime;
    private Scene scene;

    private AnimationTimer gameLoop;

    private Player player;
    private World world;
    private Camera camera;
    private TileMap tileMap;
    DevHUD fpsCounter;
    ParallaxBackground parallaxBackground;
    public GamePage() throws Exception {
        // set up root node and create scene with it
        Group root = new Group();
        scene = new Scene(root, STAGE_WIDTH, STAGE_HEIGHT, true);

        // set up pane
        Pane pane = new Pane();
        pane.setTranslateX(0);
        pane.setTranslateY(0);
        root.getChildren().add(pane);


        // set up physics world
        world = new World(new Vec2(0, -9.81f));
        world.setGravity(new Vec2(0, 0));

        // set up tilemap
        tileMap = new TileMap(
                "./img/sprite_sheets/parisian_tileset_test_2.png",
                32,
                0,
                "./data/tile_maps/map1.json",
                pane,
                new Rectangle2D(0, 0, scene.getWidth(), scene.getHeight()),
                world
        );

        // set up player in world
        player = new Player(world, pane);

        // set up camera to follow player
        camera = new Camera(player);

        // set up FPS counter
        fpsCounter = new DevHUD(pane, player, camera);

        // set up parallax backgrounds
        parallaxBackground = new ParallaxBackground(pane);

        // set up input keypress listeners
        inputsPressed = new Hashtable<>();
        InputManager inputManager = new InputManager(scene, inputsPressed);

        gameLoop = new AnimationTimer() {
            public void handle(long currentNanoTime) {
                // find amount of time passed since last loop run
                long elapsedNanoSeconds = currentNanoTime - startNanoTime;
                startNanoTime = currentNanoTime;
                double timeDeltaSeconds = elapsedNanoSeconds / 1000000000.0;
                double currentSecondsTime = currentNanoTime / 1000000000.0;
                move(currentSecondsTime, timeDeltaSeconds);

                paint();
            }

        };

        this.startLoop();
    }

    private void move(double currentSecondsTime, double timeDeltaSeconds) {
        // MOVE all entities, and the camera
        // apply physics to bodies
        player.move(inputsPressed, timeDeltaSeconds);
        // move the world
        world.step((float) timeDeltaSeconds, 6, 2);
        // move the camera according to new positions
        camera.move(timeDeltaSeconds);
        // update the FPS counter's frame count
        fpsCounter.move(currentSecondsTime);

    }

    private void paint() {
        // PAINT all entities in the correct positions
        // note: scrollX, scrollY are coordinates of top left corner of screen
        double scrollX = camera.getScrollX();
        double scrollY = camera.getScrollY();

        tileMap.paint(scrollX, scrollY);
        player.paint(scrollX, scrollY);
        fpsCounter.paint();
//        parallaxBackground.paint(scrollX, scrollY);
    }

    public Scene getScene() {
        return scene;
    }

    public void startLoop() {
        // initialize time
        startNanoTime = System.nanoTime();
        gameLoop.start();
    }

    public void stopLoop() {
        gameLoop.stop();
    }
}