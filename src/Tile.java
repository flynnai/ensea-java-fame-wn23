import javafx.scene.image.ImageView;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.*;

public class Tile {
    public int tilesetRow;
    public int tilesetCol;

    public Tile(int tilesetRow, int tilesetCol, double x, double y, World world) {
        this.tilesetRow = tilesetRow;
        this.tilesetCol = tilesetCol;

        Rectangle squareShape = new Rectangle(1, 1);
        Body body = new Body();
        body.addFixture(new BodyFixture(squareShape));
        body.setMass(new Mass(new Vector2(0, 0), 0, 0));
        body.setMassType(MassType.INFINITE);

        Transform worldToLocal = new Transform();
        worldToLocal.setTranslation(new Vector2(x,y));
        body.setTransform(worldToLocal);
        world.addBody(body);
    }
}
