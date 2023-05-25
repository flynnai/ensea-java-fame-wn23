import javafx.application.Application;
import javafx.stage.Stage;


public class GUI extends Application implements GameConstants {
    GamePage gamePage;
    Stage primaryStage;
    @Override
    public void start(Stage primaryStage) throws Exception {
        // set up sound mixer singleton instance so sounds are pre-loaded
        SoundMixer.initialize();

        this.primaryStage = primaryStage;
        primaryStage.setTitle("Jaconde Test");
        primaryStage.setResizable(false);

        // initiate GamePage instance and show in stage
        gamePage = new GamePage(this);
        primaryStage.setScene(gamePage.getScene());
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }

    public void finishGame(double ratioBread, double ratioOfBestTime) {
        gamePage.stopLoop();

        int numStars = 0;
        if (ratioBread > 0.2 && ratioOfBestTime < 2.0) {
            numStars = 1;
        }
        if (ratioBread > 0.6 && ratioOfBestTime < 1.5) {
            numStars = 2;
        }
        if (ratioBread > 0.95 && ratioOfBestTime < 1.0) {
            numStars = 3;
        }

        FinishedPage finishedPage = new FinishedPage(numStars);

        primaryStage.setScene(finishedPage.getScene());
        primaryStage.show();
    }
}
