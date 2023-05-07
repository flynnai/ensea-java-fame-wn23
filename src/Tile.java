import javafx.scene.image.ImageView;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.World;


public class Tile {
    public int tilesetRow;
    public int tilesetCol;
    public Body body;

    public Tile(int tilesetRow, int tilesetCol, double x, double y, World world) {
        this.tilesetRow = tilesetRow;
        this.tilesetCol = tilesetCol;

        if (world == null) {
            // no physics
            body = null;
        } else {
            PolygonShape squareShape = new PolygonShape();
            squareShape.setAsBox(0.5f, 0.5f); // half-width and half-height of the rectangle
            FixtureDef squareFixture = new FixtureDef();
            squareFixture.shape = squareShape;
            squareFixture.density = 0f; // infinite mass (static object)
            squareFixture.friction = 1f;
            squareFixture.userData = "ground";
            squareFixture.filter.groupIndex = 0;

            BodyDef squareBodyDef = new BodyDef();
            squareBodyDef.type = BodyType.STATIC;
            squareBodyDef.position.set((float)x, (float)y);

            body = world.createBody(squareBodyDef);
            body.createFixture(squareFixture);
        }
    }
}
