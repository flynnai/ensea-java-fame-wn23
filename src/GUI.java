import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class GUI extends Application{
        @Override
        public void start(Stage primaryStage) throws Exception {
            primaryStage.setTitle("FAME Rulez");

            Group root = new Group();
            Scene scene = new Scene(root, 600, 400,true);
            primaryStage.setScene(new Scene);
            primaryStage.show();
        }

        public static void main(String[] args) {
            launch(args);
        }
    }
}
