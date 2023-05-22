public abstract class CollectableEntity implements GameConstants {
    protected Vector2 position;
    protected CollidableShape hitArea;
    public boolean hasBeenCollected = false;
    public CollectableEntity(Vector2 position, CollidableShape hitArea) {
        this.position = new Vector2(position);
        this.hitArea = hitArea;
    }

    public void move(double currentSecondsTime, Player player) {
        if (!hasBeenCollected && player.getHitBox().intersectsWith(hitArea)) {
            getCollected();
        }
    }

    public abstract void paint(double scrollX, double scrollY);

    public void getCollected() {
        hasBeenCollected = true;
    }
}