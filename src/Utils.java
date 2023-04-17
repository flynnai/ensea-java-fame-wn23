import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

public class Utils {
    public static void transformToScrollPosition(Body body, PolygonShape shape, Rectangle fxRectNode, double scrollX, double scrollY, int TILE_SIZE) {

        // find rectangle dimensions for polygon shape
        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE;
        float maxY = Float.MIN_VALUE;

        Vec2 polygonCenter = new Vec2(0, 0);
        for (int i = 0; i < shape.getVertexCount(); i++) {
            Vec2 vertex = shape.getVertex(i);
            if (vertex.x < minX) minX = vertex.x;
            if (vertex.y < minY) minY = vertex.y;
            if (vertex.x > maxX) maxX = vertex.x;
            if (vertex.y > maxY) maxY = vertex.y;

            polygonCenter.addLocal(vertex);
        }

        polygonCenter.mulLocal(1f / shape.getVertexCount());

        Vec2 rectScreenCenter = new Vec2(
                ((body.getWorldCenter().x + polygonCenter.x - (float) scrollX) * TILE_SIZE),
                ((body.getWorldCenter().y + polygonCenter.y - (float) scrollY) * TILE_SIZE * -1)
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