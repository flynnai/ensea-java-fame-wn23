import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import org.dyn4j.dynamics.World;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class TileMap {
    private List<TileMapLayer> layers;

    public TileMap(String spriteSheetPath, int tileSizePx, int tilePaddingPx, String tileMapPath, Pane pane, Rectangle2D gameViewport, int displayTileSizePx, World world) throws FileNotFoundException {
        // TODO we can use embedded JSON base64 spritesheet instead
        Image spriteSheet = new Image("file:" + spriteSheetPath);

        FileReader reader = new FileReader(tileMapPath);
        JSONObject tilemapJson = new JSONObject(new JSONTokener(reader));

        String mapName = "Map_1_copy_copy_copy_copy_copy_copy_copy_copy";
        JSONObject map = tilemapJson.getJSONObject("maps").getJSONObject(mapName);

        int mapWidth = map.getInt("mapWidth");
        int mapHeight = map.getInt("mapHeight");

        JSONArray layers = map.getJSONArray("layers");
        this.layers = new ArrayList<TileMapLayer>();
        for (int layerNum = 0; layerNum < layers.length(); layerNum++) {
            JSONObject layerJson = layers.getJSONObject(layerNum);
            JSONObject tilesJson = layerJson.getJSONObject("tiles");

            TileMapLayer layer = new TileMapLayer(
                    tilesJson,
                    mapWidth,
                    mapHeight,
                    gameViewport,
                    tileSizePx,
                    tilePaddingPx,
                    spriteSheet,
                    displayTileSizePx,
                    pane,
                    layerNum == 1 ? world : null // only have physics for layer 1
            );
            this.layers.add(layer);
        }
    }

    void paint(double scrollX, double scrollY) {
        // scrollX, scrollY are tile-relative (e.g. 0.5 is half a tile)
        for (TileMapLayer layer : layers) {
            layer.paint(scrollX, scrollY);
        }
    }

}
