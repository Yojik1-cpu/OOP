package snake.model;

public interface Food {
    Point getPosition();
    int getScoreValue();
    void applyEffect(Snake snake);
    String getVisualType();
}