import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class HUD implements GameConstants {
    Text breadText;
    Text timeText;
    GamePage gamePage;
    private double timeElapsedSeconds;
    private ImageView breadIcon;

    public HUD(Pane pane, GamePage gamePage) {
        breadText = new Text(STAGE_WIDTH - 200 - 20 - 34, 40, "BREAD: ");
        Font font = new Font("Courier New", 20);
        breadText.setFont(font);
        breadText.setFill(Color.BLACK);
        breadText.setWrappingWidth(200);
        breadText.setTextAlignment(TextAlignment.RIGHT);
        pane.getChildren().add(breadText);

        breadIcon = new ImageView(new Image("file:img/bread_counter_icon.png"));
        breadIcon.setFitWidth(24);
        breadIcon.setFitHeight(24);
        breadIcon.setX(STAGE_WIDTH - 20 - 24);
        breadIcon.setY(22);
        pane.getChildren().add(breadIcon);

        timeText = new Text(STAGE_WIDTH - 200 - 20, 80, "TIME: ");
        timeText.setFont(font);
        timeText.setFill(Color.BLACK);
        timeText.setWrappingWidth(200);
        timeText.setTextAlignment(TextAlignment.RIGHT);
        pane.getChildren().add(timeText);

        this.gamePage = gamePage;
        timeElapsedSeconds = 0;
    }

    public void move(double timeDeltaSeconds) {
        timeElapsedSeconds += timeDeltaSeconds;
    }

    public void paint() {
        breadText.setText("" + gamePage.numBreadCollected);
        timeText.setText("TIME: " + String.format("%.1f", timeElapsedSeconds) + "s");
    }

    public final double getTimeElapsedSeconds() {
        return timeElapsedSeconds;
    }
}