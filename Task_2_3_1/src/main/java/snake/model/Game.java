package snake.model;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private final int width;
    private final int height;
    private final Snake snake;
    private final List<Food> foods = new ArrayList<>();
    
    private boolean gameOver = false;
    private boolean isWon = false;
    
    private int score = 1;
    private int foodEaten = 0;

    private final WinCondition winCondition;
    private final FoodFactory foodFactory;

    public Game(int width, int height, int foodCount, WinCondition winCondition, FoodFactory foodFactory) {
        this.width = width;
        this.height = height;
        this.winCondition = winCondition;
        this.foodFactory = foodFactory;
        
        this.snake = new Snake(new Point(width / 2, height / 2));
        
        for (int i = 0; i < foodCount; i++) {
            foods.add(foodFactory.createFood(width, height, getOccupiedPoints()));
        }
    }

    public void update() {
        if (gameOver) return;

        snake.move();
        checkCollisions();
    }

    private void checkCollisions() {
        Point head = snake.getHead();

        if (head.x < 0 || head.x >= width || head.y < 0 || head.y >= height) {
            gameOver = true;
            return;
        }

        if (snake.isCollisionWithSelf()) {
            gameOver = true;
            return;
        }

        Food eatenFood = null;
        for (Food food : foods) {
            if (head.equals(food.getPosition())) {
                eatenFood = food;
                break;
            }
        }

        if (eatenFood != null) {
            eatenFood.applyEffect(snake);
            score += eatenFood.getScoreValue();
            foodEaten++;
            
            foods.remove(eatenFood);
            foods.add(foodFactory.createFood(width, height, getOccupiedPoints()));
        }

        if (winCondition.isWin(this)) {
            gameOver = true;
            isWon = true;
        }
    }
    
    private List<Point> getOccupiedPoints() {
        List<Point> occupied = new ArrayList<>(snake.getBody());
        for (Food f : foods) {
            occupied.add(f.getPosition());
        }
        return occupied;
    }

    public Snake getSnake() {
        return snake;
    }

    public List<Food> getFoods() {
        return foods;
    }

    public boolean isGameOver() {
        return gameOver;
    }
    
    public boolean isWon() {
        return isWon;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getFoodEaten() {
        return foodEaten;
    }
    
    public int getScore() {
        return score;
    }
}