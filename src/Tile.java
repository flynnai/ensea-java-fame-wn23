import javafx.scene.image.ImageView;

public class Tile {
    public int tilesetRow;
    public int tilesetCol;
    public int mapRow;
    public int mapCol;
    public ImageView imageView;
    public Tile(int mapRow, int mapCol, int tilesetRow, int tilesetCol, ImageView imageView) {
        this.mapRow = mapRow;
        this.mapCol = mapCol;
        this.tilesetRow = tilesetRow;
        this.tilesetCol = tilesetCol;
        this.imageView = imageView;
    }
}
