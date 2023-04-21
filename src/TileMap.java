import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import org.jbox2d.dynamics.World;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class TileMap implements GameConstants {
    private List<TileMapLayer> layers;

    public TileMap(String spriteSheetPath, int tileSizePx, int tilePaddingPx, String tileMapPath, Pane pane, Rectangle2D gameViewport, World world) throws FileNotFoundException {
        // TODO we can use embedded JSON base64 spritesheet instead
        // get a demo version of the file to see its dimensions, then scale
        Image demoImage = new Image("file:" + spriteSheetPath);
        float scaleFactor = (float) TILE_SIZE / (tileSizePx + tilePaddingPx);
        Image spriteSheet = new Image(
                "file:" + spriteSheetPath,
                demoImage.getWidth() * scaleFactor,
                demoImage.getHeight() * scaleFactor,
                true,
                false
        );

        FileReader reader = new FileReader(tileMapPath);
        JSONObject mapJson = new JSONObject(new JSONTokener(reader));

        int mapWidth = mapJson.getInt("width");
        int mapHeight = mapJson.getInt("height");

        JSONArray layersJson = mapJson.getJSONArray("layers");

        this.layers = new ArrayList<TileMapLayer>();
        for (int layerNum = 0; layerNum < layersJson.length(); layerNum++) {
            JSONObject layerJson = layersJson.getJSONObject(layerNum);
            JSONArray tilesJson = layerJson.getJSONArray("data");
            String layerName = layerJson.getString("name");
            TileMapLayer layer = new TileMapLayer(
                    tilesJson,
                    mapWidth,
                    mapHeight,
                    gameViewport,
                    tilePaddingPx,
                    spriteSheet,
                    pane,
                    // only have physics for layer named "collidable"
                    layerName.equals("collidable") ? world : null
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
