import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.List;


public class Tile {
    public int tilesetRow;
    public int tilesetCol;
    public final List<Shape> fixtureShapes;

    public Tile(int tilesetRow, int tilesetCol, double x, double y, boolean isCollidable) {
        this.tilesetRow = tilesetRow;
        this.tilesetCol = tilesetCol;

        this.fixtureShapes = new ArrayList<>();
        if (isCollidable) {
            this.fixtureShapes.add(new Rectangle(x, y, 1, 1));
        }
    }
}
