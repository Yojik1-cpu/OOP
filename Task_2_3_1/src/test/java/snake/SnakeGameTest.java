package snake;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import snake.model.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SnakeGameTest {

    private Game game;
    private final int gameWidth = 20;
    private final int gameHeight = 20;

    @BeforeEach
    void setUp() {
        WinCondition winCondition = new LengthWinCondition(100);
        FoodFactory dummyFactory = (width, height, occupiedPoints) -> new Food() {
            @Override public Point getPosition() { return new Point(0, 0); }
            @Override public int getScoreValue() { return 1; }
            @Override public void applyEffect(Snake snake) { snake.grow(1); }
            @Override public String getVisualType() { return "DEFAULT"; }
        };
        
        game = new Game(gameWidth, gameHeight, 1, winCondition, dummyFactory);
    }

    @Test
    void snakeMovesRight() {
        Snake snake = game.getSnake();
        Point head = snake.getHead();
        snake.move();
        assertEquals(new Point(head.x + 1, head.y), snake.getHead());
    }

    @Test
    void snakeGrows() {
        Snake snake = game.getSnake();
        int initialSize = snake.getBody().size();
        snake.grow(1);
        snake.move();
        assertEquals(initialSize + 1, snake.getBody().size());
    }

    @Test
    void snakeGrowsByTen() {
        Snake snake = game.getSnake();
        int initialSize = snake.getBody().size();
        snake.grow(10);
        for (int i = 0; i < 10; i++) {
            snake.move();
        }
        assertEquals(initialSize + 10, snake.getBody().size());
    }

    @Test
    void snakeCannotReverse() {
        Snake snake = game.getSnake();
        snake.setDirection(Direction.RIGHT);
        snake.move();
        
        snake.setDirection(Direction.LEFT);
        snake.move();
        
        assertEquals(new Point(12, 10), snake.getHead());
    }

    @Test
    void snakeDirectionQueue() {
        Snake snake = game.getSnake();
        snake.setDirection(Direction.UP);
        snake.setDirection(Direction.LEFT);
        
        snake.move();
        assertEquals(new Point(10, 9), snake.getHead());
        
        snake.move();
        assertEquals(new Point(9, 9), snake.getHead());
    }

    @Test
    void collisionWithWall() {
        for (int i = 0; i < 9; i++) {
            game.update();
        }
        assertFalse(game.isGameOver());

        game.update();

        assertTrue(game.isGameOver());
        assertFalse(game.isWon());
    }

    @Test
    void collisionWithSelf() {
        Snake snake = new Snake(new Point(5, 5));
        snake.grow(4);
        for(int i=0; i<4; i++) snake.move();

        snake.setDirection(Direction.DOWN);
        snake.move();
        snake.setDirection(Direction.LEFT);
        snake.move();
        snake.setDirection(Direction.UP);
        snake.move();

        assertTrue(snake.isCollisionWithSelf());
    }

    @Test
    void scoreIncreasesDefaultFood() {
        int initialScore = game.getScore();
        Point foodPos = game.getFoods().getFirst().getPosition();
        
        game.getSnake().getBody().clear();
        game.getSnake().getBody().add(new Point(foodPos.x - 1, foodPos.y));
        game.getSnake().setDirection(Direction.RIGHT);
        
        game.update();
        
        assertEquals(initialScore + 1, game.getScore());
    }

    @Test
    void winConditionMet() {
        WinCondition winCondition = new LengthWinCondition(5);
        FoodFactory foodFactory = new DefaultFoodFactory();
        game = new Game(20, 20, 1, winCondition, foodFactory);
        
        game.getSnake().grow(4);
        for(int i=0; i<4; i++) game.getSnake().move();
        
        assertFalse(game.isGameOver());
        
        game.getSnake().grow(1);
        game.getSnake().move();
        
        game.update();
        
        assertTrue(game.isGameOver());
        assertTrue(game.isWon());
    }

    @Test
    void foodDoesNotSpawnOnSnake() {
        Snake snake = new Snake(new Point(0,0));
        for(int i=0; i < (gameWidth * gameHeight) - 5; i++) {
            snake.grow(1);
        }
        List<Point> snakeBody = snake.getBody();
        
        FoodFactory factory = new DefaultFoodFactory();
        Food newFood = factory.createFood(gameWidth, gameHeight, snakeBody);
        
        assertFalse(snakeBody.contains(newFood.getPosition()));
    }
    
    @Test
    void foodFactoryCreatesFood() {
        FoodFactory factory = new DefaultFoodFactory();
        Food food = factory.createFood(gameWidth, gameHeight, game.getSnake().getBody());
        assertNotNull(food);
    }
}