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

    public DevHUD(Pane pane, Player player) {
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
        Vec2 coords = player.getWorldPosition();
        playerCoordsText.setText("Player x, y: " + (Math.round(coords.x * 100) / 100f) + ", " + (Math.round(coords.y * 100) / 100f));
    }
}