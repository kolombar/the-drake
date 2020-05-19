package thedrake.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientApplication extends Application {

    private static Stage pStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("ScreenView.fxml"));
        primaryStage.setTitle("The Drake");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

    }

    public static Stage getPrimaryStage() {
        return pStage;
    }

    private void setPrimaryStage(Stage pStage) {
        this.pStage = pStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

