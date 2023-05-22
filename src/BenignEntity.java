public abstract class BenignEntity implements GameConstants {
    protected Vector2 position;
    public BenignEntity(Vector2 position) {
        this.position = position;
    }

    public abstract void move(double timeDeltaSeconds);
    public abstract void paint(double scrollX, double scrollY);
}