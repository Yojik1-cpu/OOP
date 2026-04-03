package snake.model;

import java.util.List;

public interface FoodFactory {
    Food createFood(int width, int height, List<Point> occupiedPoints);
}