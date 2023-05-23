import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Confetti implements GameConstants {
    Vector2 position;
    Vector2 velocity;
    Rectangle rectangle;
    double wiggleTimeOffset;
    boolean hasAscended = false;
    private static final Color[] PASTEL_COLORS = {
            Color.web("#FFD1DC"), // Pastel Pink
            Color.web("#FFB347"), // Pastel Orange
            Color.web("#B39EB5"), // Pastel Purple
            Color.web("#77DD77")  // Pastel Green
    };
    public Confetti(Vector2 position, Vector2 velocity, Pane pane) {
        this.position = position;
        this.velocity = velocity;

        Color color = PASTEL_COLORS[(int)Math.floor(Math.random() * PASTEL_COLORS.length)];
        rectangle = new Rectangle(9, 6, color);
        pane.getChildren().add(rectangle);

        wiggleTimeOffset = Math.random() * 2 * Math.PI;

        rectangle.setVisible(false);

    }

    public void move(double timeDeltaSeconds, double currentSecondsTime) {
        rectangle.setVisible(true);

        velocity.y += 650 * timeDeltaSeconds;
        velocity.x *= 0.99;
        velocity.y *= 0.99;
        position.x += velocity.x * timeDeltaSeconds;
        position.y += velocity.y * timeDeltaSeconds;

        rectangle.setTranslateX(position.x + Math.sin(wiggleTimeOffset + currentSecondsTime * 15) * 7);
        rectangle.setTranslateY(position.y);

        if (position.y < STAGE_HEIGHT / 2) {
            if (!hasAscended) hasAscended = true;
        } else {

            if (hasAscended){
                // respawn
                position.y = -STAGE_HEIGHT / 2;
                position.x = Math.random() * STAGE_WIDTH - STAGE_WIDTH / 2;
            }

        }
    }
}