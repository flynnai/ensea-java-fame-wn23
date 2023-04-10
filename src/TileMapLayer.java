import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TileMapLayer {
    public List<List<Tile>> tileMatrix;
    public List<List<ImageView>> tileViews;
    public Rectangle2D viewport;

    public TileMapLayer(List<List<Tile>> tileMatrix, Rectangle2D viewport, int tileSizePx, Image spriteSheet, Pane pane) {
        this.tileMatrix = tileMatrix;
        int viewportColCount = (int) Math.ceil(viewport.getWidth() / tileSizePx);
        int viewportRowCount = (int) Math.ceil(viewport.getHeight() / tileSizePx);
        tileViews = new ArrayList<>();
        for (int i = 0; i < viewportRowCount; i++) {
            ArrayList<ImageView> row = new ArrayList<>();
            for (int j = 0; j < viewportColCount; j++) {
                ImageView tileView = new ImageView(spriteSheet);
                tileView.setFitWidth(tileSizePx);
                tileView.setFitHeight(tileSizePx);
                pane.getChildren().add(tileView);
                row.add(tileView);
            }
            tileViews.add(row);
        }
    }
}
