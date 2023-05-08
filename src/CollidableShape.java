import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CollidableShape {
    List<Vector2> vertices;

    Vector2 position;

    // note: MUST be a convex shape. To create concave shapes, use multiple convex shapes.
    public CollidableShape(Vector2... vertices) {
        this.vertices = new ArrayList<>(Arrays.asList(vertices));
        this.setPosition(new Vector2(0, 0));
    }

    boolean intersectsWith(CollidableShape other) {
        List<Vector2> axes = new ArrayList<>();

        // translate to correct position
        List<Vector2> myTransVertices = new ArrayList<>();
        for (Vector2 vertex : vertices) {
            myTransVertices.add(vertex.add(getPosition()));
        }

        List<Vector2> otherTransVertices = new ArrayList<>();
        for (Vector2 vertex : other.vertices) {
            otherTransVertices.add(vertex.add(other.getPosition()));
        }

        // add the normals of the edges of this shape to the list of axes
        for (int i = 0; i < myTransVertices.size(); i++) {
            Vector2 p1 = myTransVertices.get(i);
            Vector2 p2 = myTransVertices.get((i + 1) % myTransVertices.size());
            Vector2 edge = p2.subtract(p1);
            axes.add(new Vector2(-edge.y, edge.x).normalize());
        }

        // add the normals of the edges of the other shape to the list of axes
        for (int i = 0; i < otherTransVertices.size(); i++) {
            Vector2 p1 = otherTransVertices.get(i);
            Vector2 p2 = otherTransVertices.get((i + 1) % otherTransVertices.size());
            Vector2 edge = p2.subtract(p1);
            axes.add(new Vector2(-edge.y, edge.x).normalize());
        }

        // project both shapes onto each axis and check for overlap
        for (Vector2 axis : axes) {
            double minThis = Double.MAX_VALUE, maxThis = -Double.MAX_VALUE;
            double minOther = Double.MAX_VALUE, maxOther = -Double.MAX_VALUE;

            for (Vector2 vertex : myTransVertices) {
                double projection = vertex.dotProduct(axis);
                minThis = Math.min(minThis, projection);
                maxThis = Math.max(maxThis, projection);
            }

            for (Vector2 vertex : otherTransVertices) {
                double projection = vertex.dotProduct(axis);
                minOther = Math.min(minOther, projection);
                maxOther = Math.max(maxOther, projection);
            }

            if (maxThis <= minOther || maxOther <= minThis) {
                // shapes do not overlap on this axis, therefore they do not intersect
                return false;
            }
        }

        // all axes overlap, therefore the shapes intersect
        return true;
    }

    void setPosition(Vector2 newPos) {
        this.position = new Vector2(newPos);
    }

    final Vector2 getPosition() {
        return this.position;
    }

    @Override
    public String toString() {
        String output = "CollidableShape(";
        for (Vector2 vec : vertices) {
            output += (vec.add(getPosition())) + " ";
        }
        return output + ")";
    }

    public boolean isPointInside(Vector2 point) {
        double side = Double.NaN;
        double epsilon = 1e-10;
        // let's go around the shape, and test the side this point is on for each edge
        // if it's all the same side, it's inside. Else, outside.
        for (int i = 0; i < vertices.size(); i++) {
            Vector2 v1 = position.add(vertices.get(i));
            Vector2 v2 = position.add(vertices.get((i + 1) % vertices.size()));
            double result = (point.y - v1.y) * (v2.x - v1.x)
                    - (point.x - v1.x) * (v2.y - v1.y);
            if (Math.abs(result) < epsilon) {
                // point is on edge
                return true;
            } else if (Double.isNaN(side)) {
                // no side assigned yet
                side = result;
            } else if (result * side < 0.0) {
                // opposite signs are negative
                return false;
            }
        }
        return true;
    }
}
