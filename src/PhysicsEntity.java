import javafx.scene.shape.Shape;

import java.util.List;

public class PhysicsEntity implements GameConstants {
    private Vector2 position;
    private Vector2 velocity;
    public List<Shape> fixtureShapes;
    private final List<List<Tile>> tileMatrix;
    public PhysicsEntity(Vector2 position, Vector2 velocity, List<Shape> fixtureShapes, List<List<Tile>> tileMatrix) {
        this.setPosition(position);
        this.setVelocity(velocity);
        this.fixtureShapes = fixtureShapes;
        this.tileMatrix = tileMatrix;
    }
    boolean isTouchingTerrain() {
        // TODO make this work for any size entity, efficient
        int i = 0;
        for (List<Tile> row : tileMatrix) {
            int j = 0;
            for (Tile tile : row) {
                if (tile == null) {
                    j++;

                    continue;
                }

                for (Shape tileFixtureShape : tile.fixtureShapes) {
                    for (Shape myFixtureShape : fixtureShapes) {
                        myFixtureShape.setTranslateX(this.position.x);
                        myFixtureShape.setTranslateY(this.position.y);
                        if (tileFixtureShape.intersects(myFixtureShape.getBoundsInParent())) {
                            System.out.println("I'm intersecting this tile: " + i + ", " + j);
                            return true;
                        }
                    }
                }
                j++;

            }
            i++;
        }
        return false;
    }

    void move(double timeDeltaSeconds) {
        position.x += velocity.x * timeDeltaSeconds;
        position.y += velocity.y * timeDeltaSeconds;
        // TODO collisions
        if (isTouchingTerrain()) {
            // move in opposite direction till not touching
            int maxTries = 100;
            Vector2 opposite = new Vector2(velocity.x / -velocity.getMagnitude() / maxTries, velocity.y / -velocity.getMagnitude() / maxTries);
            System.out.println("Opposite is " + opposite);
            while(isTouchingTerrain()) {
                position.x += opposite.x;
                position.y += opposite.y;
                if (--maxTries < 0) {
                    break;
                }
            }

            double damping =0;// 0.3;
            velocity.x *= -damping;
            velocity.y *= -damping;
        }
    }

    public void setPosition(Vector2 newPos) {
        this.position = new Vector2(newPos);
    }

    public void setVelocity(Vector2 newVel) {
        this.velocity = new Vector2(newVel);
    }
    public final Vector2 getPosition() {
        return this.position;
    }

    public final Vector2 getVelocity() {
        return this.velocity;
    }
}
