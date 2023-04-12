import javafx.collections.transformation.TransformationList;
import javafx.scene.Node;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

public class Utils {
    public static void transformToScrollPosition(Body body, Node fxNode, double scrollX, double scrollY, int TILE_SIZE) {
        Rotate rotation = new Rotate();
        // TODO in case it's needed, but shouldn't be, here's the rect-finding logic:
//        float rectWidth = shape.getVertices()[0].x * -2;
//        float rectHeight = shape.getVertices()[0].y * -2;
        Vec2 locCenter = body.getLocalCenter();
        rotation.setPivotX(locCenter.x * TILE_SIZE);
        rotation.setPivotY(locCenter.y * TILE_SIZE);
        rotation.setAngle(-Math.toDegrees(body.getAngle()));

        Translate translation = new Translate(TILE_SIZE * -locCenter.x, TILE_SIZE * -locCenter.y);

        Vec2 worldCenter = body.getWorldCenter();
        fxNode.setTranslateX((scrollX + worldCenter.x) * TILE_SIZE);
        fxNode.setTranslateY((scrollY - worldCenter.y) * TILE_SIZE);

        fxNode.getTransforms().clear();
        fxNode.getTransforms().add(translation);
        fxNode.getTransforms().add(rotation);

    }
}