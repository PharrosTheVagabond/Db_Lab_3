import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class LogInController {
    private Stage stage;
    private Scene scene;
    @FXML
    private TextField urlField;
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button logInButton;
    @FXML
    private Label infoLabel;

    public LogInController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/LogInScene.fxml"));
        fxmlLoader.setController(this);
        try {
            Parent parent = fxmlLoader.load();
            scene = new Scene(parent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void logInButtonPressed() {
        Connection connection;
        try {
            connection = DriverManager.getConnection(urlField.getText(), loginField.getText(), passwordField.getText());
            MainController controller = new MainController();
            controller.setConnection(connection);
            controller.showScene(stage);
        } catch (SQLException sqlEx) {
            infoLabel.setText(sqlEx.getMessage());
            sqlEx.printStackTrace();
        }
    }

    @FXML
    private void clearLabel() {
        infoLabel.setText("");
    }

    public void showScene(Stage stage) {
        stage.hide();
        this.stage = stage;
        stage.setTitle("Log in");
        stage.setScene(scene);
        stage.setResizable(false);
        urlField.setText("jdbc:mysql://localhost:3306/hs_decks?useUnicode=true&serverTimezone=UTC");
        stage.show();
    }
}
