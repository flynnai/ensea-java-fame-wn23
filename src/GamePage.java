import javafx.animation.AnimationTimer;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

public class GamePage implements GameConstants {
    private Dictionary<UserInput, Boolean> inputsPressed;
    private long startNanoTime;
    private Scene scene;

    private AnimationTimer gameLoop;

    private Player player;
    private Camera camera;
    private TileMap tileMap;
    DevHUD fpsCounter;
    ParallaxBackground parallaxBackground;
    private List<CollectableEntity> collectableEntities = new ArrayList<>();
    private List<BenignEntity> benignEntities = new ArrayList<>();

    public GamePage() throws Exception {
        // set up root node and create scene with it
        Group root = new Group();
        scene = new Scene(root, STAGE_WIDTH, STAGE_HEIGHT, true);

        // set up pane
        Pane pane = new Pane();
        pane.setTranslateX(0);
        pane.setTranslateY(0);
        root.getChildren().add(pane);

        // set up tilemap
        tileMap = new TileMap(
                "./img/sprite_sheets/parisian_tileset.png",
                32,
                0,
                "./data/tile_maps/map1.json",
                pane,
                new Rectangle2D(0, 0, scene.getWidth(), scene.getHeight()),
                collectableEntities
        );

        // set up player in world
        player = new Player(pane, tileMap.getCollidableLayerMatrix());

        // move foreground layer in front of player
        tileMap.moveLayerToForeground(FOREGROUND_LAYER_NUMBER);

        // set up camera to follow player
        camera = new Camera(player);

        // set up FPS counter
        fpsCounter = new DevHUD(pane, player, camera);

        // set up parallax backgrounds
        parallaxBackground = new ParallaxBackground(pane);

        // set up input keypress listeners
        inputsPressed = new Hashtable<>();
        InputManager inputManager = new InputManager(scene, inputsPressed);

        // set up sound mixer singleton instance so sounds are pre-loaded
        SoundMixer.initialize();

        // set up benign entities that populate our stage
        benignEntities = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            benignEntities.add(new Bird(new Vector2(20, -8.9), player, pane, tileMap.getCollidableLayerMatrix()));
        }

        gameLoop = new AnimationTimer() {
            public void handle(long currentNanoTime) {
                // find amount of time passed since last loop run
                long elapsedNanoSeconds = currentNanoTime - startNanoTime;
                startNanoTime = currentNanoTime;
                double timeDeltaSeconds = elapsedNanoSeconds / 1000000000.0;
                double currentSecondsTime = currentNanoTime / 1000000000.0;
                move(currentSecondsTime, Math.min(timeDeltaSeconds, 0.3));

                paint();
            }

        };

        this.startLoop();
    }

    private void move(double currentSecondsTime, double timeDeltaSeconds) {
        // MOVE all entities, and the camera
        // apply physics to bodies
        player.move(inputsPressed, currentSecondsTime, timeDeltaSeconds);
        // move the camera according to new positions
        camera.move(timeDeltaSeconds);
        // update the FPS counter's frame count
        fpsCounter.move(currentSecondsTime);
        // see if any entities are touching player
        for (CollectableEntity entity : collectableEntities) entity.move(currentSecondsTime, player);
        // move our benign entities around
        for (BenignEntity entity : benignEntities) entity.move(timeDeltaSeconds);
    }

    private void paint() {
        // PAINT all entities in the correct positions
        // note: scrollX, scrollY are coordinates of top left corner of screen
        double scrollX = camera.getScrollX();
        double scrollY = camera.getScrollY();

        tileMap.paint(scrollX, scrollY);
        player.paint(scrollX, scrollY);
        fpsCounter.paint();
        parallaxBackground.paint(scrollX, scrollY);
        for (CollectableEntity entity : collectableEntities) entity.paint(scrollX, scrollY);
        for (BenignEntity entity : benignEntities) entity.paint(scrollX, scrollY);
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