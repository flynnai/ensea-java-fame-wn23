import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.List;


public class Tile {
    public int tilesetRow;
    public int tilesetCol;
    public final List<CollidableShape> fixtureShapes;

    public Tile(int tilesetRow, int tilesetCol, double x, double y, boolean isCollidable) {
        this.tilesetRow = tilesetRow;
        this.tilesetCol = tilesetCol;

        this.fixtureShapes = new ArrayList<>();
        if (isCollidable) {
            CollidableRect square = new CollidableRect(0, 0, 1, 1);
            square.setPosition(new Vector2(x, y));
            this.fixtureShapes.add(square);
        }
    }
}
