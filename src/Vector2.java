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

    // utils for intersection detection
    public Vector2 normalize() {
        double mag = this.getMagnitude();
        return new Vector2(this.x / mag, this.y / mag);
    }

    public Vector2 add(Vector2 other) {
        return new Vector2(this.x + other.x, this.y + other.y);
    }
    public Vector2 subtract(Vector2 other) {
        return new Vector2(this.x - other.x, this.y - other.y);
    }
    public Vector2 scale(double amount) { return new Vector2(this.x * amount, this.y * amount); }

    public double dotProduct(Vector2 other) {
        return this.x * other.x + this.y * other.y;
    }
}
