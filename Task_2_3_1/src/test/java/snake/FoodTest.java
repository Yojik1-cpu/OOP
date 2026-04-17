package snake;

import org.junit.jupiter.api.Test;
import snake.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FoodTest {

    private final int width = 20;
    private final int height = 20;

    @Test
    void defaultFoodScoreValue() {
        DefaultFood food = new DefaultFood(width, height, new ArrayList<>());
        assertEquals(1, food.getScoreValue());
    }

    @Test
    void defaultFoodApplyEffect() {
        Snake snake = new Snake(new Point(10, 10));
        DefaultFood food = new DefaultFood(width, height, new ArrayList<>());
        
        food.applyEffect(snake);
        
        int initialSize = snake.getBody().size();
        snake.move();
        assertEquals(initialSize + 1, snake.getBody().size());
    }

    @Test
    void bonusFoodScoreValue() {
        BonusFood food = new BonusFood(width, height, new ArrayList<>());
        assertEquals(GameConfig.BONUS_FOOD_SCORE, food.getScoreValue());
    }

    @Test
    void bonusFoodApplyEffect() {
        Snake snake = new Snake(new Point(10, 10));
        BonusFood food = new BonusFood(width, height, new ArrayList<>());
        
        food.applyEffect(snake);
        
        int initialSize = snake.getBody().size();
        for (int i = 0; i < 10; i++) {
            snake.move();
        }
        assertEquals(initialSize + 10, snake.getBody().size());
    }

    @Test
    void foodSpawnsValidPosition() {
        List<Point> occupied = Arrays.asList(new Point(0, 0), new Point(1, 1));
        DefaultFood food = new DefaultFood(width, height, occupied);
        
        assertNotNull(food.getPosition());
        assertTrue(food.getPosition().x >= 0 && food.getPosition().x < width);
        assertTrue(food.getPosition().y >= 0 && food.getPosition().y < height);
        assertFalse(occupied.contains(food.getPosition()));
    }

    @Test
    void foodDoesNotSpawnOnOccupied() {
        List<Point> occupiedPoints = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (!(x == 0 && y == 0)) {
                    occupiedPoints.add(new Point(x, y));
                }
            }
        }

        DefaultFood food = new DefaultFood(width, height, occupiedPoints);
        assertEquals(new Point(0, 0), food.getPosition());
    }
}