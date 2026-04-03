package snake;

import org.junit.jupiter.api.Test;
import snake.model.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class FoodFactoryTest {

    @Test
    void factoryCreatesNonNullFood() {
        FoodFactory factory = new DefaultFoodFactory();
        Food food = factory.createFood(20, 20, new ArrayList<>());
        assertNotNull(food);
    }

    @Test
    void factoryRespectsBonusChance() {
        FoodFactory factory = new DefaultFoodFactory();
        int bonusFoodCount = 0;
        int totalRuns = 10000;
        
        for (int i = 0; i < totalRuns; i++) {
            Food food = factory.createFood(20, 20, new ArrayList<>());
            if (food instanceof BonusFood) {
                bonusFoodCount++;
            }
        }
        
        double observedChance = (double) bonusFoodCount / totalRuns * 100;
        
        double expectedChance = GameConfig.BONUS_FOOD_CHANCE;
        double margin = 2.0;
        
        assertTrue(observedChance >= expectedChance - margin && observedChance <= expectedChance + margin,
                "Observed chance (" + observedChance + "%) is not within the expected range [" +
                (expectedChance - margin) + ", " + (expectedChance + margin) + "]");
    }
}