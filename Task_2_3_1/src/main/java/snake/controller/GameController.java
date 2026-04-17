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
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.text.Font;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

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
    @FXML private StackPane gameStackPane;

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
        
        gameStackPane.widthProperty().addListener((obs, oldVal, newVal) 
                -> resizeGame());
        gameStackPane.heightProperty().addListener((obs, oldVal, newVal) 
                -> resizeGame());
        
        renderer = new GameRenderer(gc);
    }

    public void initGame(int difficulty, int foodCount, String difficultyMode) {
        this.initialDifficulty = difficulty;
        this.difficultyMode = difficultyMode;
        
        WinCondition winCondition = new LengthWinCondition(GameConfig.WIN_LENGTH);
        FoodFactory foodFactory = new RandomFoodFactory();
        
        this.game = new Game(GameConfig.WIDTH, GameConfig.HEIGHT, foodCount, winCondition, foodFactory);
        
        this.speed = GameConfig.BASE_SPEED_NS - (long) difficulty * GameConfig.SPEED_STEP_NS;

        speedLabel.setText("Speed: " + difficulty);
        modeLabel.setText("Mode: " + difficultyMode);

        rootPane.addEventFilter(KeyEvent.KEY_PRESSED, this::handleKeyPress);

        resizeGame();
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
            if (foodEaten > 0 && foodEaten % GameConfig.FOODS_TO_INCREASE_SPEED == 0 
                    && foodEaten > lastSpeedIncreaseAt) {
                speed = (long) (speed * GameConfig.SPEED_MULTIPLIER);
                lastSpeedIncreaseAt = foodEaten;
                int currentLevel = initialDifficulty + (foodEaten / GameConfig.FOODS_TO_INCREASE_SPEED);
                speedLabel.setText("Speed: " + currentLevel);
            }
        }
        
        List<Point> snakeBody = game.getSnake().getBody();
        Point oldTail = snakeBody.get(snakeBody.size() - 1);
        LinkedList<Point> oldSnakeBody = new LinkedList<>(snakeBody);

        game.update();

        renderer.smartRender(game, oldSnakeBody, oldTail);
        updateUI();
    }
    
    private void updateUI() {
        scoreLabel.setText("Score: " + game.getScore());
    }

    private void resizeGame() {
        if (game == null || gameCanvas == null || gameStackPane == null) return;
        
        double containerWidth = gameStackPane.getWidth();
        double containerHeight = gameStackPane.getHeight();
        if (containerWidth <= 0 || containerHeight <= 0) return;

        double maxCellWidth = containerWidth / GameConfig.WIDTH;
        double maxCellHeight = containerHeight / GameConfig.HEIGHT;
        
        double actualTileSize = Math.min(maxCellWidth, maxCellHeight);
        
        actualTileSize = Math.min(actualTileSize, GameConfig.TILE_SIZE);

        double renderWidth = GameConfig.WIDTH * actualTileSize;
        double renderHeight = GameConfig.HEIGHT * actualTileSize;
        
        gameCanvas.setWidth(renderWidth);
        gameCanvas.setHeight(renderHeight);

        renderer.setTileSize(actualTileSize);

        double baseFontSize = Math.max(12, actualTileSize * 1.5); 
        gameOverLabel.setFont(Font.font("System Bold", baseFontSize * 2));
        pauseLabel.setFont(Font.font(baseFontSize * 3)); 
        
        GraphicsContext gc = gameCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());

        renderer.fullRender(game);
        
        if (game.isGameOver()) {
            renderer.renderGameOver();
        }
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