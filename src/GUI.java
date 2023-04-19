import javafx.application.Application;
import javafx.stage.Stage;

public class GUI extends Application implements GameConstants {
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Jaconde Test");
        primaryStage.setResizable(false);
        
        // initiate GamePage instance and show in stage
        GamePage gamePage = new GamePage();
        primaryStage.setScene(gamePage.getScene());
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
