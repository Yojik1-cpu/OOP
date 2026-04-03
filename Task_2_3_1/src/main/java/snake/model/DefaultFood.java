package snake.model;

import java.util.List;
import java.util.Random;

public class DefaultFood implements Food {
    private final Point position;

    public DefaultFood(int width, int height, List<Point> occupiedPoints) {
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