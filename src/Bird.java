import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

import java.util.List;

public class Bird extends BenignEntity {
    enum Mode {
        PECKING,
        PERCHING,
        FLYING,
        LANDING,
    }

    ImageView imageView;
    Player player;
    enum Frame {
        FLY_WINGS_UP,
        FLY_WINGS_MIDDLE,
        FLY_WINGS_DOWN,
        PERCH,
        LEAN_TRANSITION,
        LEAN,
        PECK
    }
    Frame frame = Frame.PERCH;
    Direction direction = Direction.RIGHT;
    private Mode mode = Mode.PECKING;
    private double behaviorTimer = 0.0;
    private final double animationSpeed = 0.1; // seconds
    private final List<List<Tile>> tileMatrix;
    // animation simulated local variables
    private int pecksLeft = 10;
    private double flyDirection;
    private double flyAngularVelocity;
    private double flySpeed;
    private boolean wasHeadingForWall = false;
    private int numFlyingLoops = 0;
    private double flySwing = 0;
    public Bird(Vector2 position, Player player, Pane pane, List<List<Tile>> tileMatrix) {
        super(position);
        imageView = new ImageView(new Image("file:img/sprite_sheets/bird.png"));
        pane.getChildren().add(imageView);
        this.player = player;
        this.tileMatrix = tileMatrix;
    }

    boolean isPointInvalid(Vector2 point) {
        if (point.x < 0 || point.x > tileMatrix.get(0).size()) {
            return true;
        } else if (point.y < -tileMatrix.size() || point.y > 6) {
            return true;
        }
        int row = (int) -Math.floor(point.y) - 1;
        int col = (int) Math.floor(point.x);
        Tile tile = null;
        try {
            tile = tileMatrix.get(row).get(col);
        } catch (IndexOutOfBoundsException e) {
            // tile doesn't exist in map
        }
        if (tile == null || tile.isSlope) return false;

        for (CollidableShape shape : tile.fixtureShapes) {
            if (shape.isPointInside(point)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void move(double timeDeltaSeconds) {
        behaviorTimer -= timeDeltaSeconds;
        if (behaviorTimer < 0.0) {
            behaviorTimer = animationSpeed;

            if (mode == Mode.PECKING) {
                if (frame == Frame.LEAN_TRANSITION) {
                    frame = Frame.LEAN;
                    pecksLeft = (int) Math.floor(Math.random() * 10) + 5;
                } else if (frame == Frame.LEAN) {
                    frame = Frame.PECK;
                } else {
                    pecksLeft--;
                    if (pecksLeft > 0) {
                        frame = Frame.LEAN;
                        behaviorTimer = Math.ceil(Math.random() * 2) * animationSpeed;
                    } else {
                        // reconsider our actions
                        frame = Frame.LEAN_TRANSITION;
                        mode = Mode.PERCHING;
                    }
                }
            } else if (mode == Mode.PERCHING) {
                if (frame == Frame.LEAN_TRANSITION) {
                    frame = Frame.PERCH;
                    // wait a bit before doing something else
                    behaviorTimer = Math.random() * 5;
                } else {
                    // since this is running, the animation timer has run out. Let's do something.
                    if (false) {
                        mode = Mode.PECKING;
                        frame = Frame.LEAN_TRANSITION;
                    } else {
                        mode = Mode.FLYING;
                        boolean isPlayerToTheLeft = player.getPosition().x < position.x;
                        flyDirection = isPlayerToTheLeft ? (Math.random() * 0.5 + 0.2) : (Math.PI - (Math.random() * 0.5 + 0.2));
                        flyAngularVelocity = 0;
                        frame = Frame.FLY_WINGS_UP;
                        behaviorTimer = Math.random() * 5 + 2;
                        numFlyingLoops = 500;
                        flySwing = 0;
                    }
                }
            } else if (mode == Mode.FLYING) {
                // behavior timer ran out
                if (--numFlyingLoops > 0) {
//                    flySwing = Math.random() > 0 ? 10.0 : -10.0;
//                    behaviorTimer = Math.random() * 5 + 2;
                } else {
                    // TODO
                }
            }
        }

        if (mode == Mode.FLYING) {
            final double maxFlySpeed = 0.05;
            final double maxTurnSpeed = 0.2;
            flySpeed = Math.min(flySpeed + 0.5 * timeDeltaSeconds, maxFlySpeed);
            flyDirection += flyAngularVelocity;

            boolean shouldTurnLeft = (flyAngularVelocity > 0);

            boolean wallAhead = distanceToWall(flyDirection) > 0;
            if (wallAhead) {
                if (!wasHeadingForWall) {
                    wasHeadingForWall = true;
                    // just found this wall, find best direction
                    double leftWallDist = distanceToWall(flyDirection - 0.2);
                    double rightWallDist = distanceToWall(flyDirection + 0.2);
                    if (leftWallDist > rightWallDist) {
                        flyAngularVelocity = maxTurnSpeed * 0.1;
                    } else if (rightWallDist > leftWallDist) {
                        flyAngularVelocity = maxTurnSpeed * -0.1;
                    } else {
                        // turn around
                        flyDirection += Math.PI / 2;
                    }

                    shouldTurnLeft = flyAngularVelocity > 0;

                    System.out.println("We're running this. I can turn left: " + leftWallDist + ", and right: " + rightWallDist + " , since flyAngularVelocity is " + flyAngularVelocity);
                }
                flyAngularVelocity = Math.min(flyAngularVelocity + (shouldTurnLeft ? 1 : -1) * maxTurnSpeed * timeDeltaSeconds, maxTurnSpeed * (shouldTurnLeft ? 1 : -1));
            } else {
                wasHeadingForWall = false;

                if (Math.abs(flySwing) > 0.1) {
                    flySwing *= 0.95;
                    shouldTurnLeft = flySwing > 0;
                    flyAngularVelocity = Math.min(flyAngularVelocity + (shouldTurnLeft ? 1 : -1) * maxTurnSpeed * timeDeltaSeconds, maxTurnSpeed * (shouldTurnLeft ? 1 : -1));
                } else {
                    flyAngularVelocity *= 0.95;
                }
            }


            position.x += Math.cos(flyDirection) * flySpeed;
            position.y += Math.sin(flyDirection) * flySpeed;

        }
    }

    private double distanceToWall(double direction) {
        for (double mag = 0.5; mag < 3.0; mag++) {
            if (isPointInvalid(position.add(new Vector2(Math.cos(direction) * mag, Math.sin(direction) * mag)))) {
                return mag;
            }
        }
        return -1;
    }

    @Override
    public void paint(double scrollX, double scrollY) {
        // set frame to `frameNum` and move image relative scrollX, scrollY

        final double FRAME_SIZE = 32;
        final double BIRD_SIZE = 0.4; // tiles

        Rectangle cropArea = new Rectangle(FRAME_SIZE, FRAME_SIZE);
        cropArea.setTranslateX(FRAME_SIZE * frame.ordinal());
        cropArea.setTranslateY(0);
        imageView.setViewport(new Rectangle2D(cropArea.getTranslateX(), cropArea.getTranslateY(), cropArea.getWidth(), cropArea.getHeight()));

        imageView.setScaleX(direction == Direction.LEFT ? -1 : 1);
        imageView.setFitWidth(BIRD_SIZE * TILE_SIZE);
        imageView.setFitHeight(BIRD_SIZE * TILE_SIZE);
        imageView.setX((position.x - BIRD_SIZE / 2 - scrollX) * TILE_SIZE);
        imageView.setY((position.y + BIRD_SIZE / 2 - scrollY) * TILE_SIZE * -1);
    }
}