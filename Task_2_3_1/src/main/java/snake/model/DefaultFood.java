package snake.model;

import java.util.List;

public class DefaultFood implements Food {
    private final Point position;

    public DefaultFood(int width, int height, List<Point> occupiedPoints) {
        this.position = FoodFactory.generateRandomFreePosition(width, height, occupiedPoints);
    }

    @Override
    public Point getPosition() {
        return position;
    }

    @Override
    public int getScoreValue() {
        return 1;
    }

    @Override
    public void applyEffect(Snake snake) {
        snake.grow(1);
    }

    @Override
    public String getVisualType() {
        return "DEFAULT";
    }
}