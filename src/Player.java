import javafx.geometry.Rectangle2D;
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
    private int numTouchingLeftSide = 0;
    private int numTouchingRightSide = 0;
    PolygonShape playerShape;
    Rectangle playerDisplayRect;
    PolygonShape groundDetectorShape;
    Rectangle groundDetectorDisplayRect;

    PolygonShape leftSideDetectorShape;
    Rectangle leftSideDetectorDisplayRect;
    PolygonShape rightSideDetectorShape;
    Rectangle rightSideDetectorDisplayRect;


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

        // ===== COLLISION DETECTORS =====

        // define a small rectangle for the ground collision detector
        groundDetectorShape = new PolygonShape();
        final float DETECTOR_THICKNESS = PLAYER_HEIGHT * 0.1f;
        groundDetectorShape.setAsBox(PLAYER_WIDTH / 2, DETECTOR_THICKNESS / 2, new Vec2(0, -PLAYER_HEIGHT / 2), 0f);
        groundDetectorDisplayRect = new Rectangle(PLAYER_WIDTH * TILE_SIZE, DETECTOR_THICKNESS * TILE_SIZE);
        pane.getChildren().add(groundDetectorDisplayRect);

        FixtureDef groundDetectorFixtureDef = new FixtureDef();
        groundDetectorFixtureDef.shape = groundDetectorShape;
        groundDetectorFixtureDef.isSensor = true; // prevent physics interactions with other objects
        Fixture groundDetectorFixture = playerBody.createFixture(groundDetectorFixtureDef);

        // define a small rectangle for the left-side collision detector
        leftSideDetectorShape = new PolygonShape();
        Vec2 leftDims = new Vec2(DETECTOR_THICKNESS / 2 * 4, PLAYER_HEIGHT * 3 / 4);
        leftSideDetectorShape.setAsBox(leftDims.x / 2, leftDims.y / 2, new Vec2(-PLAYER_WIDTH / 2, PLAYER_HEIGHT / 8), 0f);
        leftSideDetectorDisplayRect = new Rectangle(leftDims.x * TILE_SIZE, leftDims.y * TILE_SIZE);
        pane.getChildren().add(leftSideDetectorDisplayRect);

        FixtureDef leftSideDetectorFixtureDef = new FixtureDef();
        leftSideDetectorFixtureDef.shape = leftSideDetectorShape;
        leftSideDetectorFixtureDef.isSensor = true;
        Fixture leftSideDetectorFixture = playerBody.createFixture(leftSideDetectorFixtureDef);

        // define a small rectangle for the right-side collision detector
        rightSideDetectorShape = new PolygonShape();
        Vec2 rightDims = new Vec2(DETECTOR_THICKNESS / 2 * 4, PLAYER_HEIGHT * 3 / 4);
        rightSideDetectorShape.setAsBox(rightDims.x / 2, rightDims.y / 2, new Vec2(PLAYER_WIDTH / 2, PLAYER_HEIGHT / 8), 0f);
        rightSideDetectorDisplayRect = new Rectangle(rightDims.x * TILE_SIZE, rightDims.y * TILE_SIZE);
        pane.getChildren().add(rightSideDetectorDisplayRect);

        FixtureDef rightSideDetectorFixtureDef = new FixtureDef();
        rightSideDetectorFixtureDef.shape = rightSideDetectorShape;
        rightSideDetectorFixtureDef.isSensor = true;
        Fixture rightSideDetectorFixture = playerBody.createFixture(rightSideDetectorFixtureDef);

        // define a class to handle collision events for the ground-detector fixture
        class SideContactListener implements ContactListener {
            public void beginContact(Contact contact) {
                // check if the collision is between the detector fixture and a ground tile
                Fixture fixtureA = contact.getFixtureA();
                Fixture fixtureB = contact.getFixtureB();
                if ((fixtureA == groundDetectorFixture && fixtureB.getUserData() == "ground") ||
                        (fixtureB == groundDetectorFixture && fixtureA.getUserData() == "ground")) {
                    // increment the number of 'ground' objects we're touching by 1
                    numTouchingGround++;
                } else if ((fixtureA == leftSideDetectorFixture && fixtureB.getUserData() == "ground") ||
                        (fixtureB == leftSideDetectorFixture && fixtureA.getUserData() == "ground")) {
                    numTouchingLeftSide++;
                } else if ((fixtureA == rightSideDetectorFixture && fixtureB.getUserData() == "ground") ||
                        (fixtureB == rightSideDetectorFixture && fixtureA.getUserData() == "ground")) {
                    numTouchingRightSide++;
                }
            }

            public void endContact(Contact contact) {
                // Check if the collision is between the detector fixture and a ground tile
                Fixture fixtureA = contact.getFixtureA();
                Fixture fixtureB = contact.getFixtureB();
                if ((fixtureA == groundDetectorFixture && fixtureB.getUserData() == "ground") ||
                        (fixtureB == groundDetectorFixture && fixtureA.getUserData() == "ground")) {
                    // increment the number of 'ground' objects we're touching by 1
                    numTouchingGround--;
                } else if ((fixtureA == leftSideDetectorFixture && fixtureB.getUserData() == "ground") ||
                        (fixtureB == leftSideDetectorFixture && fixtureA.getUserData() == "ground")) {
                    numTouchingLeftSide--;
                } else if ((fixtureA == rightSideDetectorFixture && fixtureB.getUserData() == "ground") ||
                        (fixtureB == rightSideDetectorFixture && fixtureA.getUserData() == "ground")) {
                    numTouchingRightSide--;
                }
            }

            public void preSolve(Contact contact, Manifold oldManifold) {
                // empty implementation here
            }

            public void postSolve(Contact contact, ContactImpulse impulse) {
                // empty implementation here
            }
        }

        // create a new instance of the contact listener and register it with the world
        SideContactListener contactListener = new SideContactListener();
        world.setContactListener(contactListener);

        // ===== END COLLISION DETECTORS =====


        // set up player JavaFX element
        playerDisplayRect = new Rectangle(1 * TILE_SIZE, 2 * TILE_SIZE);
        pane.getChildren().add(playerDisplayRect);


        groundDetectorDisplayRect.toFront();
    }

    public void move(Dictionary<UserInput, Boolean> inputsPressed) {
        Vec2 playerVel = playerBody.getLinearVelocity();

        if (framesUntilCanJump > 0) {
            framesUntilCanJump--;
        } else if (inputsPressed.get(UserInput.UP)) {
            if (this.isTouchingGround() && playerVel.y < 3f) {
                playerBody.applyForceToCenter(new Vec2(0, 1500));
                // we want 1 force applied to jump, only 1 time
                framesUntilCanJump = 15;
            }
        }

        if (inputsPressed.get(UserInput.DOWN)) {
            // for later
        }
        if (inputsPressed.get(UserInput.LEFT)) {
            if ((double) playerVel.x > -PLAYER_MAX_SPEED && !this.isTouchingLeftWall()) {
                playerBody.applyForceToCenter(new Vec2(-80, 0));

            }
        } else if (inputsPressed.get(UserInput.RIGHT)) {
            if ((double) playerVel.x < PLAYER_MAX_SPEED&& !this.isTouchingRightWall()) {
                playerBody.applyForceToCenter(new Vec2(80, 0));
            }
        }
    }

    public void paint(double scrollX, double scrollY) {
        Utils.transformToScrollPosition(playerBody, playerShape, playerDisplayRect, scrollX, scrollY);
        if (this.isTouchingGround()) {
            groundDetectorDisplayRect.setFill(Color.GREEN);
        } else {
            groundDetectorDisplayRect.setFill(Color.DARKRED);
        }
        Utils.transformToScrollPosition(playerBody, groundDetectorShape, groundDetectorDisplayRect, scrollX, scrollY);

        if (this.isTouchingLeftWall()) {
            leftSideDetectorDisplayRect.setFill(Color.GREEN);
        } else {
            leftSideDetectorDisplayRect.setFill(Color.DARKRED);
        }
        Utils.transformToScrollPosition(playerBody, leftSideDetectorShape, leftSideDetectorDisplayRect, scrollX, scrollY);

        if (this.isTouchingRightWall()) {
            rightSideDetectorDisplayRect.setFill(Color.GREEN);
        } else {
            rightSideDetectorDisplayRect.setFill(Color.DARKRED);
        }
        Utils.transformToScrollPosition(playerBody, rightSideDetectorShape, rightSideDetectorDisplayRect, scrollX, scrollY);
    }

    public boolean isTouchingGround() {
        return numTouchingGround > 0;
    }

    public boolean isTouchingLeftWall() {
        return numTouchingLeftSide > 0;
    }
    public boolean isTouchingRightWall() {
        return numTouchingRightSide > 0;
    }

    public Vec2 getWorldPosition() {
        return this.playerBody.getPosition();
    }
}