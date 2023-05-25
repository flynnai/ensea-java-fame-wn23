import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

public class GamePage implements GameConstants {
    private Dictionary<UserInput, Boolean> inputsPressed;
    private long startNanoTime;
    private long prevNanoTime;
    private Scene scene;

    private AnimationTimer gameLoop;

    private Player player;
    private Camera camera;
    private TileMap tileMap;
    DevHUD fpsCounter;
    ParallaxBackground parallaxBackground;
    private List<CollectableEntity> collectableEntities = new ArrayList<>();
    private List<BenignEntity> benignEntities = new ArrayList<>();
    public int numBreadCollected = 0;
    private HUD hud;
    private GUI parentGui;

    public GamePage(GUI parentGui) throws Exception {
        // remember our parent gui to call meta-methods on it later
        this.parentGui = parentGui;

        // set up root node and create scene with it
        Group root = new Group();
        scene = new Scene(root, STAGE_WIDTH, STAGE_HEIGHT, true);

        // set up pane
        Pane pane = new Pane();
        pane.setTranslateX(0);
        pane.setTranslateY(0);
        root.getChildren().add(pane);
        // fade transition
        Rectangle fadeRect = new Rectangle(STAGE_WIDTH, STAGE_HEIGHT);
        fadeRect.setFill(Color.BLACK);
        pane.getChildren().add(fadeRect);
        FadeTransition ft = new FadeTransition(Duration.millis(1000), fadeRect);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);

        // set up tilemap
        tileMap = new TileMap(
                "./img/sprite_sheets/parisian_tileset.png",
                32,
                0,
                "./data/tile_maps/map1.json",
                pane,
                new Rectangle2D(0, 0, scene.getWidth(), scene.getHeight()),
                collectableEntities,
                this
        );

        // set up player in world
        player = new Player(pane, tileMap.getCollidableLayerMatrix());

        // move foreground layer in front of player
        tileMap.moveLayerToForeground(FOREGROUND_LAYER_NUMBER);

        // set up camera to follow player
        camera = new Camera(player);

        // set up FPS counter
        fpsCounter = new DevHUD(pane, player, camera);

        // user's heads-up-display
        hud = new HUD(pane, this);

        // set up parallax backgrounds
        parallaxBackground = new ParallaxBackground(pane);

        // set up input keypress listeners
        inputsPressed = new Hashtable<>();
        InputManager inputManager = new InputManager(scene, inputsPressed, fpsCounter);

        // set up benign entities that populate our stage
        benignEntities = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            benignEntities.add(new Bird(new Vector2(18 + Math.random() * 2, -19.9), player, pane, tileMap.getCollidableLayerMatrix()));
        }

        for (int i = 0; i < 7; i++) {
            benignEntities.add(new Bird(new Vector2(55 + Math.random() * 2, -20.9), player, pane, tileMap.getCollidableLayerMatrix()));
        }

        gameLoop = new AnimationTimer() {
            public void handle(long currentNanoTime) {
                // find amount of time passed since last loop run
                long elapsedNanoSeconds = currentNanoTime - prevNanoTime;
                if (elapsedNanoSeconds >= (long) Math.round(1.0/65 * 1e9)) {
                    prevNanoTime = currentNanoTime;
                    double timeDeltaSeconds = elapsedNanoSeconds / 1000000000.0;
                    double currentSecondsTime = (currentNanoTime - startNanoTime) / 1000000000.0;

                    move(currentSecondsTime, Math.min(timeDeltaSeconds, 0.3));

                    paint();
                }
            }

        };


        fadeRect.toFront();
        ft.play();

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
        // update the HUD timer
        hud.move(timeDeltaSeconds);
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
        hud.paint();
        parallaxBackground.paint(scrollX, scrollY);
        for (CollectableEntity entity : collectableEntities) entity.paint(scrollX, scrollY);
        for (BenignEntity entity : benignEntities) entity.paint(scrollX, scrollY);
    }

    public Scene getScene() {
        return scene;
    }

    public void startLoop() {
        // initialize time
        prevNanoTime = startNanoTime = System.nanoTime();
        gameLoop.start();
    }

    public void stopLoop() {
        gameLoop.stop();
    }

    public void finishGame() {
        int totalBread = collectableEntities.size() - 1; // subtract 1 for win flag
        parentGui.finishGame(
                (double) numBreadCollected / totalBread,
                hud.getTimeElapsedSeconds() / BEST_TIME
        );
    }
}