package snake.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class SettingsController {

    @FXML
    private Slider difficultySlider;

    @FXML
    private Slider foodSlider;

    @FXML
    private ToggleGroup difficultyToggleGroup;

    @FXML
    void startGame(ActionEvent event) throws IOException {
        int difficulty = (int) difficultySlider.getValue();
        int foodCount = (int) foodSlider.getValue();
        
        RadioButton selectedRadio = (RadioButton) difficultyToggleGroup.getSelectedToggle();
        String difficultyMode = selectedRadio.getText();

        URL fxmlLocation = getClass().getResource("/snake/view/GameView.fxml");
        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        Parent root = loader.load();

        GameController gameController = loader.getController();
        gameController.initGame(difficulty, foodCount, difficultyMode);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.show();
    }
}