package snake.model;

import snake.GameConfig;
import java.util.List;

public class BonusFood implements Food {
    private final Point position;

    public BonusFood(int width, int height, List<Point> occupiedPoints) {
        this.position = FoodFactory.generateRandomFreePosition(width, height, occupiedPoints);
    }

    @Override
    public Point getPosition() {
        return position;
    }

    @Override
    public int getScoreValue() {
        return GameConfig.BONUS_FOOD_SCORE;
    }

    @Override
    public void applyEffect(Snake snake) {
        snake.grow(10);
    }

    @Override
    public String getVisualType() {
        return "BONUS";
    }
}