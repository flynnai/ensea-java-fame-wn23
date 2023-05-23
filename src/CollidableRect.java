public class CollidableRect extends CollidableShape {
    public final double height;
    public final double width;
    public final double centerX;
    public final double centerY;
    public CollidableRect(double centerX, double centerY, double width, double height) {
        super(
                new Vector2(centerX - width / 2, centerY + height / 2),
                new Vector2(centerX + width / 2, centerY + height / 2),
                new Vector2(centerX + width / 2, centerY - height / 2),
                new Vector2(centerX - width / 2, centerY - height / 2)
        );
        this.height = height;
        this.width = width;
        this.centerX = centerX;
        this.centerY = centerY;
    }

    @Override public boolean isPointInside(Vector2 point) {
        // overridden because it's faster for rectangles to do it this way
        return point.x >= position.x - width / 2 && point.x <= position.x + width / 2
                && point.y >= position.y - height / 2 && point.y <= position.y + height / 2;
    }

    public double getLeft() {
        return position.x + centerX - width / 2;
    }

    public double getRight() {
        return position.x + centerX + width / 2;
    }

    public double getTop() {
        return position.y + centerY + height / 2;
    }

    public double getBottom() {
        return position.y + centerY - height / 2;
    }

    public Vector2 getCenter() {
        return new Vector2(centerX + position.x, centerY + position.y);
    }
}