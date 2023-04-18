import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class FPSCounter {
    private int accumulatedFrames = 0;
    private int lastSecond = 0;
    private int FPS = 0;
    Text text;

    public FPSCounter(Pane pane) {
        text = new Text(20, 40, "FPS: ");
        Font font = new Font("Serif", 25);
        text.setFont(font);
        text.setFill(Color.BLACK);
        pane.getChildren().add(text);
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
        text.setText("FPS: " + FPS);
    }
}