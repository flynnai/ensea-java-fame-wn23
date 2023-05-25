import javafx.animation.Animation;
import javafx.animation.RotateTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.*;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class FinishedPage implements GameConstants {
    Scene scene;

    @FXML
    private ImageView starLeft;

    @FXML
    private ImageView starMiddle;

    @FXML
    private ImageView starRight;
    @FXML
    private Text recommendationText;

    private AnimationTimer loop;
    private long startNanoTime;
    private long prevNanoTime;

    List<Confetti> confettis;

    public FinishedPage(int numStars) {
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FinishedPage.fxml"));
            loader.setController(this);
            StackPane root = loader.load();

            // Create a Scene with the loaded FXML content
            scene = new Scene(root, STAGE_WIDTH, STAGE_HEIGHT);

            starLeft.setVisible(false);
            starMiddle.setVisible(false);
            starRight.setVisible(false);
            if (numStars > 0) {
                makeShakeTransition(starLeft, Duration.millis(1000));
                if (numStars > 1) {
                    makeShakeTransition(starMiddle, Duration.millis(2000));
                    if (numStars == 3) {
                        makeShakeTransition(starRight, Duration.millis(3000));
                    }
                }
            }


            confettis = new ArrayList<>();
            final int NUM_CONFETTIS = numStars < 2 ? 0 : (numStars == 2 ? 7 : 200);
            for (int i = 0; i < NUM_CONFETTIS; i++) {
                boolean isLeftSide = i < NUM_CONFETTIS / 2;

                double dir = Math.random() * 30 + 35;
                if (!isLeftSide) dir = 180 - dir;

                dir *= Math.PI / 180; // convert from radians

                double magnitude = Math.random() * 300 + 1100;
                double offset = Math.random() * 0.7 + 0.75;

                confettis.add(new Confetti(
                        new Vector2(STAGE_WIDTH / 2 * (isLeftSide ? -1 : 1) * offset, STAGE_HEIGHT / 2 * offset),
                        new Vector2(Math.cos(dir) * magnitude, -Math.sin(dir) * magnitude),
                        root
                ));
            }

            recommendationText.setTranslateY(300);
            recommendationText.setWrappingWidth(400);
            recommendationText.setTextAlignment(TextAlignment.CENTER);
            if (numStars == 0) {
                recommendationText.setText("You got zero stars. Try collecting more bread or getting to the flag faster.");
            } else if (numStars == 1) {
                recommendationText.setText("You got one star, not bad!");
            } else if (numStars == 2) {
                recommendationText.setText("You got two stars! Nice job. You're so close to getting three stars, try again!");
            } else if (numStars == 3) {
                recommendationText.setText("You got three stars! Nice job, you win the game.");
            }
            recommendationText.setFont(new Font("Courier New", 25));

            loop = new AnimationTimer() {
                public void handle(long currentNanoTime) {
                    // find amount of time passed since last loop run
                    long elapsedNanoSeconds = currentNanoTime - prevNanoTime;
                    prevNanoTime = currentNanoTime;
                    double timeDeltaSeconds = elapsedNanoSeconds / 1000000000.0;
                    double currentSecondsTime = (currentNanoTime - startNanoTime) / 1000000000.0;

                    if (currentSecondsTime > numStars) {
                        for (Confetti confetti : confettis) {

                            confetti.move(timeDeltaSeconds, currentSecondsTime);
                        }
                    }
                }

            };

            startLoop();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Scene getScene() {
        return scene;
    }

    public void makeShakeTransition(ImageView imageView, Duration delay) {
        // create visibility pause transition
        imageView.setVisible(false);

        // Create a PauseTransition with the desired duration
        PauseTransition pause = new PauseTransition(delay);

        // When the pause duration is finished, set the visibility of the ImageView to true
        pause.setOnFinished(event -> imageView.setVisible(true));

        pause.play();

        // Create a sequential shake transition
        SequentialTransition shakeTransition = new SequentialTransition();

        List<TranslateTransition> translations = new ArrayList<>();
        Vector2 previous = new Vector2(0, 0);
        double shakeAmount = 80;
        for (int i = 0; i < 20; i++) {
            // Create a translate transition
            TranslateTransition translateTransition = new TranslateTransition(Duration.millis(40), imageView);
            Vector2 newShake = new Vector2(Math.random() * shakeAmount * 2 - shakeAmount, Math.random() * shakeAmount * 2 - shakeAmount);
            translateTransition.setByX(newShake.subtract(previous).x);
            translateTransition.setByY(newShake.subtract(previous).y);
            translations.add(translateTransition);
            previous = newShake;
            shakeAmount *= 0.8;
        }

        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(40), imageView);
        translateTransition.setByX(-previous.x);
        translateTransition.setByY(-previous.y);
        translations.add(translateTransition);

        shakeTransition.getChildren().addAll(translations);


        // Play the shake transition
        shakeTransition.setDelay(delay);
        shakeTransition.play();


    }

    public void startLoop() {
        prevNanoTime = startNanoTime = System.nanoTime();
        loop.start();
    }

    public void stopLoop() {
        loop.stop();
    }
}
