import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.contacts.Contact;

import java.util.Dictionary;

public class Player implements GameConstants {
    private final float PLAYER_WIDTH = 1f;
    private final float PLAYER_HEIGHT = 2f;
    private Body playerBody;
    private int numTouchingGround = 0;
    PolygonShape playerShape;
    Rectangle playerDisplayRect;
    PolygonShape detectorShape;
    Rectangle detectorDisplayRect;

    int framesUntilCanJump = 0;


    public Player(World world, Pane pane) {
        BodyDef playerBodyDef = new BodyDef();
        playerBodyDef.fixedRotation = true;
        playerBody = world.createBody(playerBodyDef);

        // rectangular player shape
        //        playerShape = new PolygonShape();
        //        playerShape.setAsBox(PLAYER_WIDTH / 2, PLAYER_HEIGHT / 2, new Vec2(0, 0), 0f);
        //        FixtureDef playerFixture = new FixtureDef();
        //        playerFixture.shape = playerShape;
        //        playerFixture.density = 1f;
        //        playerFixture.friction = 1f;
        //        playerBody.createFixture(playerFixture);

        // rectangle with rounded bottom player shape
        PolygonShape rectShape = new PolygonShape();
        rectShape.setAsBox(PLAYER_WIDTH / 2, PLAYER_HEIGHT * 3 / 4 / 2, new Vec2(0, PLAYER_HEIGHT / 8), 0f);
        FixtureDef playerFixture = new FixtureDef();
        playerFixture.shape = rectShape;
        playerFixture.density = 1f;
        playerFixture.friction = 1f;

        // circular bottom to allow smooth movement
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(PLAYER_WIDTH / 2);
        circleShape.m_p.set(0, -PLAYER_HEIGHT * 1 / 4);

        FixtureDef circleFixture = new FixtureDef();
        circleFixture.shape = circleShape;
        circleFixture.density = 1f;
        circleFixture.friction = 1f;

        playerBody.createFixture(playerFixture);
        playerBody.createFixture(circleFixture);

        // rectangular player "display area" shape, no physics, for mapping the sprite to
        playerShape = new PolygonShape();
        playerShape.setAsBox(PLAYER_WIDTH / 2, PLAYER_HEIGHT / 2, new Vec2(0, 0), 0f);

        playerBody.setType(BodyType.DYNAMIC);
        Vec2 playerStart = new Vec2(15, 2);
        float playerAngle = 0f;
        playerBody.setTransform(playerStart, playerAngle);

        // define a small rectangle to act as the collision detector
        detectorShape = new PolygonShape();
        final float DETECTOR_WIDTH = PLAYER_WIDTH * 0.9f;
        final float DETECTOR_HEIGHT = PLAYER_HEIGHT * 0.1f;
        detectorShape.setAsBox(DETECTOR_WIDTH / 2, DETECTOR_HEIGHT / 2, new Vec2(0, -PLAYER_HEIGHT / 2), 0f); // Adjust dimensions and position as needed
        detectorDisplayRect = new Rectangle(DETECTOR_WIDTH * TILE_SIZE, DETECTOR_HEIGHT * TILE_SIZE);
        pane.getChildren().add(detectorDisplayRect);

        // create a fixture for the collision detector and attach it to the player body
        FixtureDef detectorFixtureDef = new FixtureDef();
        detectorFixtureDef.shape = detectorShape;
        detectorFixtureDef.isSensor = true; // prevent physics interactions with other objects
        Fixture detectorFixture = playerBody.createFixture(detectorFixtureDef);

        // define a class to handle collision events for the ground-detector fixture
        class GroundContactListener implements ContactListener {
            public void beginContact(Contact contact) {
                // check if the collision is between the detector fixture and a ground tile
                Fixture fixtureA = contact.getFixtureA();
                Fixture fixtureB = contact.getFixtureB();
                if ((fixtureA == detectorFixture && fixtureB.getUserData() == "ground") ||
                        (fixtureB == detectorFixture && fixtureA.getUserData() == "ground")) {
                    // increment the number of 'ground' objects we're touching by 1
                    numTouchingGround++;
                }
            }

            public void endContact(Contact contact) {
                // Check if the collision is between the detector fixture and a ground tile
                Fixture fixtureA = contact.getFixtureA();
                Fixture fixtureB = contact.getFixtureB();
                if ((fixtureA == detectorFixture && fixtureB.getUserData() == "ground") ||
                        (fixtureB == detectorFixture && fixtureA.getUserData() == "ground")) {
                    // increment the number of 'ground' objects we're touching by 1
                    numTouchingGround--;
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


        // set up player JavaFX element
        playerDisplayRect = new Rectangle(1 * TILE_SIZE, 2 * TILE_SIZE);
        pane.getChildren().add(playerDisplayRect);


        detectorDisplayRect.toFront();
    }

    public void move(Dictionary<UserInput, Boolean> inputsPressed) {
        if (framesUntilCanJump > 0) {
            framesUntilCanJump--;
        } else if (inputsPressed.get(UserInput.UP)) {
            if (this.isTouchingGround()) {
                playerBody.applyForceToCenter(new Vec2(0, 1500));
                // we want 1 force applied to jump, only 1 time
                framesUntilCanJump = 15;
            }
        }

        if (inputsPressed.get(UserInput.DOWN)) {
            // for later
        }
        Vec2 playerVel = playerBody.getLinearVelocity();
        if (inputsPressed.get(UserInput.LEFT)) {
            if ((double) playerVel.x > -PLAYER_MAX_SPEED) {
                playerBody.applyForceToCenter(new Vec2(-80, 0));

            }
        } else if (inputsPressed.get(UserInput.RIGHT)) {
            if ((double) playerVel.x < PLAYER_MAX_SPEED) {
                playerBody.applyForceToCenter(new Vec2(80, 0));
            }
        }
    }

    public void paint(double scrollX, double scrollY) {
        Utils.transformToScrollPosition(playerBody, playerShape, playerDisplayRect, scrollX, scrollY);
        if (this.isTouchingGround()) {
            detectorDisplayRect.setFill(Color.GREEN);
        } else {
            detectorDisplayRect.setFill(Color.DARKRED);
        }
        Utils.transformToScrollPosition(playerBody, detectorShape, detectorDisplayRect, scrollX, scrollY);
    }

    public boolean isTouchingGround() {
        return numTouchingGround > 0;
    }

    public Vec2 getWorldPosition() {
        return this.playerBody.getPosition();
    }
}