package snake.model;

import snake.GameConfig;

import java.util.List;
import java.util.Random;

public class DefaultFoodFactory implements FoodFactory {
    private final Random random = new Random();

    @Override
    public Food createFood(int width, int height, List<Point> occupiedPoints) {
        int chance = random.nextInt(100);
        
        if (chance < GameConfig.BONUS_FOOD_CHANCE) {
            return new BonusFood(width, height, occupiedPoints);
        } else {
            return new DefaultFood(width, height, occupiedPoints);
        }
    }
}