public class CollidableRect extends CollidableShape {
    public double height;
    public double width;
    public CollidableRect(double centerX, double centerY, double width, double height) {
        super(
                new Vector2(centerX - width / 2, centerY + height / 2),
                new Vector2(centerX + width / 2, centerY + height / 2),
                new Vector2(centerX + width / 2, centerY - height / 2),
                new Vector2(centerX - width / 2, centerY - height / 2)
        );
        this.height = height;
        this.width = width;
    }

    @Override public boolean isPointInside(Vector2 point) {
        return point.x >= position.x - width / 2 && point.x <= position.x + width / 2
                && point.y >= position.y - height / 2 && point.y <= position.y + height / 2;
    }
}