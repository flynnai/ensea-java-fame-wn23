import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TileMapLayer implements GameConstants {
    public List<List<Tile>> tileMatrix;
    public List<List<ImageView>> tileViews;
    public Rectangle2D viewport;
    private Image spriteSheet;
    private int tilePaddingPx;

    public TileMapLayer(JSONArray tilesJson, int mapWidth, int mapHeight, Rectangle2D viewport, int tilePaddingPx, Image spriteSheet, Pane pane, boolean isCollidable, List<CollectableEntity> collectableEntities) {
        List<List<Tile>> tileMatrix = new ArrayList<>();
        // initialize to all `null`'s
        for (int i = 0; i < mapHeight; i++) {
            List<Tile> matrixRow = new ArrayList<>();
            for (int j = 0; j < mapWidth; j++) {
                matrixRow.add(null);
            }
            tileMatrix.add(matrixRow);
        }

        int spriteSheetNumTilesWide = (int) Math.floor((spriteSheet.getWidth() + 1) / (TILE_SIZE + tilePaddingPx));
        for (int i = 0; i < mapWidth * mapHeight; i++) {
            int mapRow = (int) Math.floor(i / mapWidth);
            int mapCol = i % mapWidth;
            int rawTileNumber = tilesJson.getInt(i) - 1;
            if (rawTileNumber == -1) {
                continue;
            }
            int tilesetRow = (int) Math.floor(rawTileNumber / spriteSheetNumTilesWide);
            int tilesetCol = rawTileNumber % spriteSheetNumTilesWide;

            BreadType breadType = null;
            if (tilesetRow == 0 && tilesetCol == 10) breadType = BreadType.BATARD;
            if (tilesetRow == 0 && tilesetCol == 11) breadType = BreadType.BAGUETTE;
            if (tilesetRow == 1 && tilesetCol == 10) breadType = BreadType.CROISSANT;
            if (breadType != null) {
                // make a collectable entity, instead of a tile
                ImageView imageView = new ImageView(spriteSheet);
                imageView.setFitWidth(TILE_SIZE + 1);
                imageView.setFitHeight(TILE_SIZE + 1);
                imageView.setViewport(new Rectangle2D(
                        tilesetCol * (TILE_SIZE + this.tilePaddingPx),
                        tilesetRow * (TILE_SIZE + this.tilePaddingPx),
                        TILE_SIZE,
                        TILE_SIZE
                ));
                pane.getChildren().add(imageView);

                collectableEntities.add(new CollectableBread(new Vector2(mapCol + 0.5, -mapRow - 0.5), breadType, imageView));
            } else {
                // add a new tile to the tilemap
                tileMatrix.get(mapRow).set(mapCol, new Tile(tilesetRow, tilesetCol, mapCol + 0.5, -mapRow - 0.5, isCollidable));
            }
        }

        this.tileMatrix = tileMatrix;
        int viewportColCount = (int) Math.ceil(viewport.getWidth() / TILE_SIZE) + 1;
        int viewportRowCount = (int) Math.ceil(viewport.getHeight() / TILE_SIZE) + 1;
        this.tilePaddingPx = tilePaddingPx;
        tileViews = new ArrayList<>();
        for (int i = 0; i < viewportRowCount; i++) {
            ArrayList<ImageView> row = new ArrayList<>();
            for (int j = 0; j < viewportColCount; j++) {
                ImageView tileView = new ImageView(spriteSheet);
                // Add 1 to tile size to remove 1px lines between
                tileView.setFitWidth(TILE_SIZE + 1);
                tileView.setFitHeight(TILE_SIZE + 1);
                pane.getChildren().add(tileView);
                row.add(tileView);
            }
            tileViews.add(row);
        }
    }

    public void paint(double scrollX, double scrollY) {
        int layerWidth = this.tileMatrix.get(0).size();
        int layerHeight = this.tileMatrix.size();
        double interTileOffsetX = (-scrollX) % 1;
        // since javaFX draws down, scrollY is already "negative" and doesn't need to be flipped
        double interTileOffsetY = scrollY % 1;
        int startCol = (int) ((-scrollX) - interTileOffsetX);
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
                        tile.tilesetCol * (TILE_SIZE + this.tilePaddingPx),
                        tile.tilesetRow * (TILE_SIZE + this.tilePaddingPx),
                        TILE_SIZE,
                        TILE_SIZE
                ));
                tileView.relocate(
                        TILE_SIZE * (viewportColNum + interTileOffsetX),
                        TILE_SIZE * (viewportRowNum + interTileOffsetY)
                );
            }
        }
    }

    public void moveToFront() {
        for (List<ImageView> row : tileViews) {
            for (ImageView node : row) {
                node.toFront();
            }
        }
    }
}
