package snake.controller;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.LinkedList;

import snake.GameConfig;
import snake.model.*;

public class GameController {

    @FXML private BorderPane rootPane;
    @FXML private Canvas gameCanvas;
    @FXML private Label scoreLabel;
    @FXML private Label speedLabel;
    @FXML private Label modeLabel;
    @FXML private Label gameOverLabel;
    @FXML private Label pauseLabel;
    @FXML private Button pauseButton;

    private Game game;
    private GameRenderer renderer;
    private AnimationTimer timer;
    
    private long speed;
    private String difficultyMode;
    private int initialDifficulty;
    private int lastSpeedIncreaseAt = 0;
    private boolean isPaused = false;

    @FXML
    public void initialize() {
        GraphicsContext gc = gameCanvas.getGraphicsContext2D();
        gameCanvas.setWidth(GameConfig.WIDTH * GameConfig.TILE_SIZE);
        gameCanvas.setHeight(GameConfig.HEIGHT * GameConfig.TILE_SIZE);
        
        renderer = new GameRenderer(gc);
        renderer.fullRender(new Game(GameConfig.WIDTH, GameConfig.HEIGHT, 0, new LengthWinCondition(999), new DefaultFoodFactory())); 
    }

    public void initGame(int difficulty, int foodCount, String difficultyMode) {
        this.initialDifficulty = difficulty;
        this.difficultyMode = difficultyMode;
        
        WinCondition winCondition = new LengthWinCondition(GameConfig.WIN_LENGTH);
        FoodFactory foodFactory = new DefaultFoodFactory();
        
        this.game = new Game(GameConfig.WIDTH, GameConfig.HEIGHT, foodCount, winCondition, foodFactory);
        
        this.speed = GameConfig.BASE_SPEED_NS - (long) difficulty * GameConfig.SPEED_STEP_NS;

        speedLabel.setText("Speed: " + difficulty);
        modeLabel.setText("Mode: " + difficultyMode);

        rootPane.addEventFilter(KeyEvent.KEY_PRESSED, this::handleKeyPress);

        renderer.fullRender(game);
        updateUI();

        this.timer = new AnimationTimer() {
            long lastTick = 0;

            public void handle(long now) {
                if (game.isGameOver()) {
                    this.stop();
                    renderer.fullRender(game);
                    renderer.renderGameOver();
                    
                    if (game.isWon()) {
                        gameOverLabel.setText("YOU WIN!");
                        gameOverLabel.setTextFill(javafx.scene.paint.Color.GREEN);
                    } else {
                        gameOverLabel.setText("GAME OVER");
                        gameOverLabel.setTextFill(javafx.scene.paint.Color.RED);
                    }
                    gameOverLabel.setVisible(true);
                    return;
                }
                
                if (lastTick == 0) {
                    lastTick = now;
                    tick();
                    return;
                }

                if (now - lastTick > speed) {
                    lastTick = now;
                    tick();
                }
            }
        };
        timer.start();
    }

    private void tick() {
        if ("Increasing".equals(difficultyMode)) {
            int foodEaten = game.getFoodEaten();
            if (foodEaten > 0 && foodEaten % GameConfig.FOODS_TO_INCREASE_SPEED == 0 && foodEaten > lastSpeedIncreaseAt) {
                speed = (long) (speed * GameConfig.SPEED_MULTIPLIER);
                lastSpeedIncreaseAt = foodEaten;
                int currentLevel = initialDifficulty + (foodEaten / GameConfig.FOODS_TO_INCREASE_SPEED);
                speedLabel.setText("Speed: " + currentLevel);
            }
        }
        
        Point oldTail = game.getSnake().getBody().getLast();
        LinkedList<Point> oldSnakeBody = new LinkedList<>(game.getSnake().getBody());

        game.update();

        renderer.smartRender(game, oldSnakeBody, oldTail);
        updateUI();
    }
    
    private void updateUI() {
        scoreLabel.setText("Score: " + game.getScore());
    }

    @FXML
    void togglePause(ActionEvent event) {
        if (game == null || game.isGameOver()) return;
        
        isPaused = !isPaused;
        if (isPaused) {
            timer.stop();
            pauseLabel.setVisible(true);
            pauseButton.setText("▶");
        } else {
            timer.start();
            pauseLabel.setVisible(false);
            pauseButton.setText("⏸");
        }
    }

    @FXML
    void restartGame(ActionEvent event) throws IOException {
        if (timer != null) {
            timer.stop();
        }
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/snake/view/SettingsView.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    private void handleKeyPress(KeyEvent event) {
        if (game == null) return;
        
        KeyCode code = event.getCode();

        if (code == KeyCode.P) {
            togglePause(null);
            event.consume();
            return;
        }
        
        if (game.isGameOver() || isPaused) return;

        switch (code) {
            case UP:
            case DOWN:
            case LEFT:
            case RIGHT:
                game.getSnake().setDirection(Direction.valueOf(code.name()));
                event.consume();
                break;
        }
    }
}