public class Vector2 {
    public double x;
    public double y;
    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2(Vector2 other) {
        this.x = other.x;
        this.y = other.y;
    }

    @Override
    public String toString() {
        return "Vector2(" + this.x + ", " + this.y + ")";
    }

    public double getMagnitude() {
        return Math.hypot(x, y);
    }
}
