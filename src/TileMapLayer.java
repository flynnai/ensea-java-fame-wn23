import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TileMapLayer {
    public List<List<Tile>> tileMatrix;
    public List<List<ImageView>> tileViews;
    public Rectangle2D viewport;
    private Image spriteSheet;
    private int tileSizePx;
    private int tilePaddingPx;
    private int displayTileSizePx;

    public TileMapLayer(JSONObject tilesJson, int mapWidth, int mapHeight, Rectangle2D viewport, int tileSizePx, int tilePaddingPx, Image spriteSheet, int displayTileSizePx, Pane pane) {
        List<List<Tile>> tileMatrix = new ArrayList<>();
        // initialize to all `null`'s
        for (int i = 0; i < mapHeight; i++) {
            List<Tile> matrixRow = new ArrayList<>();
            for (int j = 0; j < mapWidth; j++) {
                matrixRow.add(null);
            }
            tileMatrix.add(matrixRow);
        }

        Iterator<String> tileKeys = tilesJson.keys();
        while (tileKeys.hasNext()) {
            String tileKey = tileKeys.next();
            JSONObject tileJson = tilesJson.getJSONObject(tileKey);

            String[] coords = tileKey.split("-");
            int mapCol = Integer.parseInt(coords[0]);
            int mapRow = Integer.parseInt(coords[1]);
            int tilesetCol = tileJson.getInt("x");
            int tilesetRow = tileJson.getInt("y");

            tileMatrix.get(mapRow).set(mapCol, new Tile(tilesetRow, tilesetCol));
        }

        this.tileMatrix = tileMatrix;
        int viewportColCount = (int) Math.ceil(viewport.getWidth() / displayTileSizePx) + 1;
        int viewportRowCount = (int) Math.ceil(viewport.getHeight() / displayTileSizePx) + 1;
        this.tileSizePx = tileSizePx;
        this.tilePaddingPx = tilePaddingPx;
        this.displayTileSizePx = displayTileSizePx;
        tileViews = new ArrayList<>();
        for (int i = 0; i < viewportRowCount; i++) {
            ArrayList<ImageView> row = new ArrayList<>();
            for (int j = 0; j < viewportColCount; j++) {
                ImageView tileView = new ImageView(spriteSheet);
                tileView.setFitWidth(displayTileSizePx);
                tileView.setFitHeight(displayTileSizePx);
                pane.getChildren().add(tileView);
                row.add(tileView);
            }
            tileViews.add(row);
        }
    }

    public void paint(double scrollX, double scrollY) {
        int layerWidth = this.tileMatrix.get(0).size();
        int layerHeight = this.tileMatrix.size();

        double interTileOffsetX = scrollX % 1;
        double interTileOffsetY = scrollY % 1;
        int startCol = (int) (scrollX - interTileOffsetX);
        int startRow = (int) (scrollY - interTileOffsetY);
        for (int viewportRowNum = 0; viewportRowNum < this.tileViews.size(); viewportRowNum++) {
            List<ImageView> viewportRow = this.tileViews.get(viewportRowNum);
            for (int viewportColNum = 0; viewportColNum < viewportRow.size(); viewportColNum++) {
                int mapRowNum = viewportRowNum - startRow;
                int mapColNum = viewportColNum - startCol;
                ImageView tileView = viewportRow.get(viewportColNum);

                boolean isOffScreen = !(mapRowNum >= 0 && mapRowNum < layerHeight && mapColNum >= 0 && mapColNum < layerWidth);
                Tile tile = isOffScreen ? null : this.tileMatrix.get(mapRowNum).get(mapColNum);
                if (tile == null) {
                    // offscreen, or empty spot in map
                    tileView.setVisible(false);
                    continue;
                }
                tileView.setVisible(true);
                tileView.setViewport(new Rectangle2D(
                        tile.tilesetCol * (this.tileSizePx + this.tilePaddingPx),
                        tile.tilesetRow * (this.tileSizePx + this.tilePaddingPx),
                        this.tileSizePx,
                        this.tileSizePx
                ));
                tileView.relocate(
                        displayTileSizePx * (viewportColNum + interTileOffsetX),
                        displayTileSizePx * (viewportRowNum + interTileOffsetY)
                );
            }
        }
    }
}
