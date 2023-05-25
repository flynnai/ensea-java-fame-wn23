import javafx.geometry.Rectangle2D;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.List;


public class Tile {
    public int tilesetRow;
    public int tilesetCol;
    public final List<CollidableShape> fixtureShapes;
    public boolean isSlope = false;

    public Tile(int tilesetRow, int tilesetCol, double x, double y, boolean isCollidable) {
        this.tilesetRow = tilesetRow;
        this.tilesetCol = tilesetCol;

        this.fixtureShapes = new ArrayList<>();
        if (isCollidable) {
            if (tilesetRow == 1 && tilesetCol == 8) {
                // 45deg slope up to the right
                CollidableShape triangle = new CollidableShape(
                        new Vector2(-0.5, -0.5),
                        new Vector2(0.5, 0.5),
                        new Vector2(0.5, -0.5)
                );
                triangle.setPosition(new Vector2(x, y));
                this.fixtureShapes.add(triangle);
                isSlope = true;
            } else if (tilesetRow == 1 && tilesetCol == 9) {
                // 45deg slope up to the left
                CollidableShape triangle = new CollidableShape(
                        new Vector2(-0.5, 0.5),
                        new Vector2(0.5, -0.5),
                        new Vector2(-0.5, -0.5)
                );
                triangle.setPosition(new Vector2(x, y));
                this.fixtureShapes.add(triangle);
                isSlope = true;
            } else if (tilesetRow == 13 && tilesetCol >= 10 && tilesetCol <= 13) {
                CollidableShape shape = null;
                if (tilesetCol == 10) {
                    // 22.5deg slope up to the right
                    shape = new CollidableShape(
                            new Vector2(-0.5, -0.5),
                            new Vector2(0.5, 0),
                            new Vector2(0.5, -0.5)
                    );
                } else if (tilesetCol == 11) {
                    shape = new CollidableShape(
                            new Vector2(-0.5, -0.5),
                            new Vector2(-0.5, 0),
                            new Vector2(0.5, 0.5),
                            new Vector2(0.5, -0.5)
                    );
                } else if (tilesetCol == 12) {
                    // 22.5deg slope up to the left
                    shape = new CollidableShape(
                            new Vector2(-0.5, 0.5),
                            new Vector2(0.5, 0),
                            new Vector2(0.5, -0.5),
                            new Vector2(0.5, -0.5)
                    );
                } else if (tilesetCol == 13) {
                    shape = new CollidableShape(
                            new Vector2(-0.5, 0),
                            new Vector2(0.5, -0.5),
                            new Vector2(-0.5, -0.5)
                    );
                }
                shape.setPosition(new Vector2(x, y));
                this.fixtureShapes.add(shape);
                isSlope = true;
            } else {
                CollidableRect square = new CollidableRect(0, 0, 1, 1);
                square.setPosition(new Vector2(x, y));
                this.fixtureShapes.add(square);
            }
        }
    }
}
