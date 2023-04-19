import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.jbox2d.common.Vec2;

public class DevHUD {
    private int accumulatedFrames = 0;
    private int lastSecond = 0;
    private int FPS = 0;
    Text fpsText;
    Player player;
    Text playerCoordsText;
    Camera camera;
    Text scrollCoordsText;

    public DevHUD(Pane pane, Player player, Camera camera) {
        fpsText = new Text(20, 40, "FPS: ");
        Font font = new Font("Courier New", 20);
        fpsText.setFont(font);
        fpsText.setFill(Color.BLACK);
        pane.getChildren().add(fpsText);

        this.player = player;
        playerCoordsText = new Text(20, 60, "Player x, y:");
        playerCoordsText.setFont(font);
        playerCoordsText.setFill(Color.BLACK);
        pane.getChildren().add(playerCoordsText);

        this.camera = camera;
        scrollCoordsText = new Text(20, 80, "Scroll x, y:");
        scrollCoordsText.setFont(font);
        scrollCoordsText.setFill(Color.BLACK);
        pane.getChildren().add(scrollCoordsText);
    }

    public void move(double currentTime) {
        accumulatedFrames++;
        int currSecond = (int) Math.floor(currentTime);
        if (currSecond > lastSecond) {
            FPS = accumulatedFrames;
            accumulatedFrames = 0;
            lastSecond = currSecond;
        }
    }

    public void paint() {
        fpsText.setText("FPS: " + FPS);
        Vec2 playerCoords = player.getWorldPosition();
        playerCoordsText.setText("Player x, y: "
                + (Math.round(playerCoords.x * 100) / 100f) + ", "
                + (Math.round(playerCoords.y * 100) / 100f)
        );
        scrollCoordsText.setText("Scroll x, y: "
                + (Math.round(camera.getScrollX() * 100) / 100f) + ", "
                + (Math.round(camera.getScrollY()) * 100) / 100f
        );
    }
}