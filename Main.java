import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        LogInController logInController = new LogInController();
        logInController.showScene(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
