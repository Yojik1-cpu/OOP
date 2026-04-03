package snake;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        URL fxmlLocation = getClass().getResource("/snake/view/SettingsView.fxml");
        if (fxmlLocation == null) {
            System.err.println("Cannot find FXML file. Make sure it's in src/main/resources/snake/view/");
            throw new IOException("FXML file not found.");
        }
        
        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        Pane root = loader.load();
        Scene scene = new Scene(root);

        primaryStage.setTitle("Snake");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}