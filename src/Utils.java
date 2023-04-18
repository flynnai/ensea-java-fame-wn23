import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

public class Utils implements GameConstants {
    public static void transformToScrollPosition(Body body, PolygonShape shape, Rectangle fxRectNode, double scrollX, double scrollY) {
        Vec2 polygonCenter = shape.m_centroid;

        Vec2 rectScreenCenter = new Vec2(
                ((body.getWorldCenter().x - body.getLocalCenter().x + polygonCenter.x - (float) scrollX) * TILE_SIZE),
                ((body.getWorldCenter().y - body.getLocalCenter().y + polygonCenter.y - (float) scrollY) * TILE_SIZE * -1)
        );

        fxRectNode.getTransforms().clear();

        // ALMOST works. Uncomment the following lines to display rectangle at body rotation
        //        Vec2 bodyLocalCenterOfMass = body.getLocalCenter();
        //        Rotate rotation = new Rotate();
        //        rotation.setPivotX(rectScreenCenter.x - (bodyLocalCenterOfMass.x * TILE_SIZE));
        //        rotation.setPivotY(rectScreenCenter.y - (bodyLocalCenterOfMass.y * TILE_SIZE));
        //        rotation.setAngle(-Math.toDegrees(body.getAngle()));
        //        fxRectNode.getTransforms().add(rotation);


        Translate translation = new Translate(
                rectScreenCenter.x - fxRectNode.getWidth() / 2,
                rectScreenCenter.y - fxRectNode.getHeight() / 2
        );

        fxRectNode.getTransforms().add(translation);
    }
}