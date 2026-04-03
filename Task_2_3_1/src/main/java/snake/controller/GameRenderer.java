package snake.controller;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import snake.GameConfig;
import snake.model.Food;
import snake.model.Game;
import snake.model.Point;

import java.util.LinkedList;

public class GameRenderer {
    private final GraphicsContext gc;
    private final int tileSize;
    private final int width;
    private final int height;

    public GameRenderer(GraphicsContext gc) {
        this.gc = gc;
        this.tileSize = GameConfig.TILE_SIZE;
        this.width = GameConfig.WIDTH;
        this.height = GameConfig.HEIGHT;
    }

    public void fullRender(Game game) {
        renderBackground();
        renderFoods(game);
        renderSnake(game);
    }

    public void smartRender(Game game, LinkedList<Point> oldSnakeBody, Point oldTail) {
        if (game.isGameOver()) {
            return;
        }

        if (oldSnakeBody.size() == game.getSnake().getBody().size()) {
            eraseCell(oldTail);
        }

        Point newHead = game.getSnake().getHead();
        gc.setFill(GameConfig.COLOR_SNAKE);
        gc.fillRect(newHead.x * tileSize, newHead.y * tileSize, tileSize, tileSize);
        renderFoods(game);
    }
    
    public void renderGameOver() {
        gc.setStroke(Color.RED);
        gc.setLineWidth(3);
        gc.strokeRect(0, 0, width * tileSize, height * tileSize);
    }

    private void renderBackground() {
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                eraseCell(new Point(col, row));
            }
        }
    }

    private void eraseCell(Point p) {
        if ((p.y + p.x) % 2 == 0) {
            gc.setFill(GameConfig.COLOR_LIGHT_GREEN);
        } else {
            gc.setFill(GameConfig.COLOR_DARK_GREEN);
        }
        gc.fillRect(p.x * tileSize, p.y * tileSize, tileSize, tileSize);
    }

    private void renderSnake(Game game) {
        gc.setFill(GameConfig.COLOR_SNAKE);
        for (Point point : game.getSnake().getBody()) {
            gc.fillRect(point.x * tileSize, point.y * tileSize, tileSize, tileSize);
        }
    }

    private void renderFoods(Game game) {
        for (Food food : game.getFoods()) {
            Point foodPos = food.getPosition();
            if ("DEFAULT".equals(food.getVisualType())) {
                gc.setFill(GameConfig.COLOR_FOOD_DEFAULT);
            } else {
                gc.setFill(GameConfig.COLOR_FOOD_BONUS);
            }
            gc.fillRect(foodPos.x * tileSize, foodPos.y * tileSize, tileSize, tileSize);
        }
    }
}