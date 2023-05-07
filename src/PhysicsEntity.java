import javafx.scene.shape.Shape;

import java.util.List;

public class PhysicsEntity {
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
        this.position.x += this.velocity.x * timeDeltaSeconds;
        this.position.y += this.velocity.y * timeDeltaSeconds;
        // TODO collisions
        if (isTouchingTerrain()) {
            this.velocity.y = -this.velocity.y;
        } else {
            System.out.println("Hmm.");
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
