public abstract class CollectableEntity implements GameConstants {
    protected Vector2 position;
    protected double collectRadius;
    public boolean hasBeenCollected = false;
    public CollectableEntity(Vector2 position, double collectRadius) {
        this.position = new Vector2(position);
        this.collectRadius = collectRadius;
    }

    public void move(double currentSecondsTime, Player player) {
        if (player.getPosition().subtract(position).getMagnitude() < collectRadius) {
            getCollected();
        }
    }

    public abstract void paint(double scrollX, double scrollY);

    public void getCollected() {
        hasBeenCollected = true;
    }
}