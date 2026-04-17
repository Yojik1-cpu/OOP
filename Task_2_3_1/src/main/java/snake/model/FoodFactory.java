package snake.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public interface FoodFactory {
    Food createFood(int width, int height, List<Point> occupiedPoints);

    static Point generateRandomFreePosition(int width, int height, List<Point> occupiedPoints) {
        List<Point> freePoints = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Point p = new Point(x, y);
                if (!occupiedPoints.contains(p)) {
                    freePoints.add(p);
                }
            }
        }

        if (freePoints.isEmpty()) {
            return null;
        }

        Random random = new Random();
        return freePoints.get(random.nextInt(freePoints.size()));
    }
}