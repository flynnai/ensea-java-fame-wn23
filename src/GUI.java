import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
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


public class GUI extends Application {
    private double scrollX;
    private double scrollY;
    private double sx;
    private double sy;

    private Dictionary<String, Boolean> inputsPressed;
    long startNanoTime;

    private double cameraOffsetX;
    private double cameraOffsetY;

    private boolean isTouchingGround = false;


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
        World world = new World(new Vec2(0, -9.81f));
        BodyDef playerBodyDef = new BodyDef();
        playerBodyDef.fixedRotation = true;
        Body playerBody = world.createBody(playerBodyDef);


        // rect player shape
        PolygonShape playerShape = new PolygonShape();
        final float PLAYER_WIDTH = 1f;
        final float PLAYER_HEIGHT = 2f;
        playerShape.setAsBox(PLAYER_WIDTH / 2, PLAYER_HEIGHT / 2, new Vec2(PLAYER_WIDTH / 2, PLAYER_HEIGHT / 2), 0f);
        FixtureDef playerFixture = new FixtureDef();
        playerFixture.shape = playerShape;
        playerFixture.density = 1f;
        playerBody.createFixture(playerFixture);

        playerBody.setType(BodyType.DYNAMIC);
        Vec2 playerStart = new Vec2(15, 2);
        float playerAngle = 0f;
        playerBody.setTransform(playerStart, playerAngle);


        // Define a small rectangle to act as the collision detector
        PolygonShape detectorShape = new PolygonShape();
        detectorShape.setAsBox(PLAYER_WIDTH / 2 * 0.9f, PLAYER_HEIGHT / 2 * 0.1f, new Vec2(0, -PLAYER_HEIGHT / 2), 0f); // Adjust dimensions and position as needed

// Create a fixture for the collision detector and attach it to the player body
        FixtureDef detectorFixtureDef = new FixtureDef();
        detectorFixtureDef.shape = detectorShape;
        detectorFixtureDef.isSensor = true; // prevent physics interactions with other objcts
        Fixture detectorFixture = playerBody.createFixture(detectorFixtureDef);

// Define a class to handle collision events for the detector fixture
        class GroundContactListener implements ContactListener {
            public void beginContact(Contact contact) {
                // Check if the collision is between the detector fixture and a ground tile
                Fixture fixtureA = contact.getFixtureA();
                Fixture fixtureB = contact.getFixtureB();
                if ((fixtureA == detectorFixture && fixtureB.getUserData() == "ground") ||
                        (fixtureB == detectorFixture && fixtureA.getUserData() == "ground")) {
                    // Set a flag to indicate that the player is touching the ground
                    isTouchingGround = true;
                    System.out.println("Touched the ground!!!");
                }
            }

            public void endContact(Contact contact) {
                // Check if the collision is between the detector fixture and a ground tile
                Fixture fixtureA = contact.getFixtureA();
                Fixture fixtureB = contact.getFixtureB();
                if ((fixtureA == detectorFixture && fixtureB.getUserData() == "ground") ||
                        (fixtureB == detectorFixture && fixtureA.getUserData() == "ground")) {
                    // Clear the flag to indicate that the player is no longer touching the ground
                    isTouchingGround = false;
                    System.out.println("Stopped touching the ground");
                }
            }

            public void preSolve(Contact contact, Manifold oldManifold) {
                // empty implementation here
            }

            public void postSolve(Contact contact, ContactImpulse impulse) {
                // empty implementation here
            }
        }

// Create a new instance of the contact listener and register it with the world
        GroundContactListener contactListener = new GroundContactListener();
        world.setContactListener(contactListener);


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
                    if (isTouchingGround) {
                        isTouchingGround = false; // TODO need better method of preventing 2-frame jump
                        playerBody.applyForceToCenter(new Vec2(0, 1000));
                    }
                } else if (inputsPressed.get("down")) {
                    // for later
                }
                if (inputsPressed.get("left")) {
                    playerBody.applyForceToCenter(new Vec2(-20, 0));
                } else if (inputsPressed.get("right")) {
                    playerBody.applyForceToCenter(new Vec2(20, 0));
                }

                world.step((float) elapsedNanoSeconds / 1000000000.0f, 6, 2);
                Vec2 playerPos = playerBody.getPosition();
                Vec2 playerVel = playerBody.getLinearVelocity();

                cameraOffsetX += (-playerVel.x / 10 - cameraOffsetX) / 2;
                cameraOffsetY += (playerVel.y / 10 - cameraOffsetY) / 2;

                scrollX = -playerPos.x + STAGE_WIDTH / TILE_SIZE / 2 + cameraOffsetX;
                scrollY = playerPos.y + STAGE_HEIGHT / TILE_SIZE / 2 + cameraOffsetY;

                tilemap.paint(scrollX, scrollY);
//                playerDisplayRect.setTranslateX((scrollX + playerBody.getWorldCenter().x) * TILE_SIZE);
//                playerDisplayRect.setTranslateY((scrollY - playerBody.getWorldCenter().y) * TILE_SIZE);
                Utils.transformToScrollPosition(playerBody, playerDisplayRect, scrollX, scrollY, TILE_SIZE);
//                Rotate rotation = new Rotate();
//                rotation.setPivotX(0.5 * TILE_SIZE);
//                rotation.setPivotY(1 * TILE_SIZE);
//                rotation.setAngle(-Math.toDegrees(playerBody.getAngle()));
//
//                playerDisplayRect.getTransforms().clear();
//                playerDisplayRect.getTransforms().add(new Translate(TILE_SIZE * -0.5, TILE_SIZE * -1));
//                playerDisplayRect.getTransforms().add(rotation);


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
