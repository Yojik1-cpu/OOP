package snake.model;

import snake.GameConfig;

import java.util.List;
import java.util.Random;

public class BonusFood implements Food {
    private final Point position;

    public BonusFood(int width, int height, List<Point> occupiedPoints) {
        Random random = new Random();
        Point p;
        do {
            p = new Point(random.nextInt(width), random.nextInt(height));
        } while (occupiedPoints.contains(p));
        this.position = p;
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