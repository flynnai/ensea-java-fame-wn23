import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;


// IMPORTING TILESET PSEUDO LOGIC
//map = obj["maps"]["MAP_NAME"]
//width = map["mapWidth"]
//height = map["mapHeight"]
//
//
//for layer in map["layers"]:
//    tiles = layer["tiles"]
//    outputLayerTiles = [[] * width] * height
//    for key in tiles.keys():
//        tile = tiles[key]
//        col, row = [int(elt) for elt in key.split("-")]
//        tilesetCol, tilesetRow = tile["x"], tile["y"]
//        outputLayerTiles.append(Tile(
//            mapRow=row,
//            mapCol=col,
//            tilesetRow=tilesetRow,
//            tilesetCol=tilesetCol
//        ))


public class TileMap {
    private Image spriteSheet;
    private int spriteSheetNumCols;
    private int spriteSheetNumRows;
    private List<TileMapLayer> layers;

    private int tilesetTileSizePx;
    private int tilesetPaddingSizePx;

    public TileMap(String spriteSheetPath, int tileSizePx, int tilePaddingPx, String tileMapPath, Pane pane, Rectangle2D gameViewport) throws FileNotFoundException {
        // TODO we can use embedded JSON base64 spritesheet instead
        this.tilesetTileSizePx = tileSizePx;
        this.tilesetPaddingSizePx = tilePaddingPx;
        spriteSheet = new Image("file:" + spriteSheetPath);
        spriteSheetNumCols = (int) Math.floor((spriteSheet.getWidth() + tilePaddingPx) / (tileSizePx + tilePaddingPx));
        spriteSheetNumRows = (int) Math.floor((spriteSheet.getHeight() + tilePaddingPx) / (tileSizePx + tilePaddingPx));

        FileReader reader = new FileReader(tileMapPath);
        JSONObject tilemapJson = new JSONObject(new JSONTokener(reader));

        String mapName = "Map_1_copy_copy_copy_copy_copy_copy_copy_copy";
        JSONObject map = tilemapJson.getJSONObject("maps").getJSONObject(mapName);

        int mapWidth = map.getInt("mapWidth");
        int mapHeight = map.getInt("mapHeight");

        JSONArray layers = map.getJSONArray("layers");
        this.layers = new ArrayList<TileMapLayer>();
        for (int layerNum = 0; layerNum < layers.length(); layerNum++) {
            JSONObject layer = layers.getJSONObject(layerNum);
            JSONObject tiles = layer.getJSONObject("tiles");
            List<List<Tile>> tileMatrix = new ArrayList<>();
            // initialize to all `null`'s
            for (int i = 0; i < mapHeight; i++) {
                List<Tile> matrixRow = new ArrayList<>();
                for (int j = 0; j < mapWidth; j++) {
                    matrixRow.add(null);
                }
                tileMatrix.add(matrixRow);
            }

            Iterator<String> tileKeys = tiles.keys();
            while (tileKeys.hasNext()) {
                String tileKey = tileKeys.next();
                JSONObject tileJson = tiles.getJSONObject(tileKey);

                String[] coords = tileKey.split("-");
                int mapCol = Integer.parseInt(coords[0]);
                int mapRow = Integer.parseInt(coords[1]);
                int tilesetCol = tileJson.getInt("x");
                int tilesetRow = tileJson.getInt("y");

                tileMatrix.get(mapRow).set(mapCol, new Tile(tilesetRow, tilesetCol));
            }

            this.layers.add(new TileMapLayer(tileMatrix, gameViewport, tileSizePx, spriteSheet, pane));
        }

        // TODO call paint() so everything's in the right position
    }

    void paint(double scrollX, double scrollY) {
        // scrollX, scrollY are tile-relative (e.g. 0.5 is half a tile)
        double tileSize = 32; // TODO magic number, should pass in
        for (TileMapLayer layer : layers) {
            int layerWidth = layer.tileMatrix.get(0).size();
            int layerHeight = layer.tileMatrix.size();
//            for (int rowNum = 0; rowNum < layer.tileMatrix.size(); rowNum++) {
//                List<Tile> row = layer.tileMatrix.get(rowNum);
//                for (int colNum = 0; colNum < row.size(); colNum++) {
//                    Tile tile = row.get(colNum);
//                    // check if empty tile
//                    if (tile != null) {
//                        ImageView tileView = tile.imageView;
//                        tileView.setFitWidth(tileSize);
//                        tileView.setFitHeight(tileSize);
//                        tileView.setX(tileSize * colNum - scrollX);
//                        tileView.setY(tileSize * rowNum - scrollY);
//                    }
//                }
//            }


            // given scrollX, scrollY...
            // tileScrollX is scrollX / tileSizePx, same for tileScrollY
            // intertileOffsetX is tileScrollX mod 1, same for y
            // startCol is (int) (tileScrollX - intertileOffsetX)
            // run thru all imageViews and update, offset by startCol, startRow

            double interTileOffsetX = scrollX % 1;
            double interTileOffsetY = scrollY % 1;
            int startCol = (int) (scrollX - interTileOffsetX);
            int startRow = (int) (scrollY - interTileOffsetY);
            for (int viewportRowNum = 0; viewportRowNum < layer.tileViews.size(); viewportRowNum++) {
                List<ImageView> viewportRow = layer.tileViews.get(viewportRowNum);
                for (int viewportColNum = 0; viewportColNum < viewportRow.size(); viewportColNum++) {
                    int mapRowNum = viewportRowNum - startRow;
                    int mapColNum = viewportColNum - startCol;
                    ImageView tileView = viewportRow.get(viewportColNum);

                    boolean isOffScreen = !(mapRowNum >= 0 && mapRowNum < layerHeight && mapColNum >= 0 && mapColNum < layerWidth);
                    Tile tile = isOffScreen ? null : layer.tileMatrix.get(mapRowNum).get(mapColNum);
                    if (tile == null) {
                        // offscreen, or empty spot in map
                        tileView.setVisible(false);
                        continue;
                    }
                    tileView.setVisible(true);
                    tileView.setViewport(new Rectangle2D(
                            tile.tilesetCol * (this.tilesetTileSizePx + this.tilesetPaddingSizePx),
                            tile.tilesetRow * (this.tilesetTileSizePx + this.tilesetPaddingSizePx),
                            this.tilesetTileSizePx,
                            this.tilesetTileSizePx
                    ));
                    tileView.relocate(tileSize * (viewportColNum + interTileOffsetX), tileSize * (viewportRowNum + interTileOffsetY));
                }
            }
        }
    }

}
