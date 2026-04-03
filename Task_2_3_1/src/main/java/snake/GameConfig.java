package snake;

import javafx.scene.paint.Color;

public class GameConfig {

    public static final int TILE_SIZE = 20;
    public static final int WIDTH = 20;
    public static final int HEIGHT = 20;

    public static final long BASE_SPEED_NS = 250_000_000L;
    public static final long SPEED_STEP_NS = 20_000_000L;
    
    public static final int FOODS_TO_INCREASE_SPEED = 5;
    public static final double SPEED_MULTIPLIER = 0.7;

    public static final int WIN_LENGTH = (WIDTH * HEIGHT) / 2;

    public static final int BONUS_FOOD_CHANCE = 5;
    public static final int BONUS_FOOD_SCORE = 10;

    public static final Color COLOR_LIGHT_GREEN = Color.web("#aad751");
    public static final Color COLOR_DARK_GREEN = Color.web("#a2d149");
    public static final Color COLOR_SNAKE = Color.web("#0080ff");
    public static final Color COLOR_FOOD_DEFAULT = Color.RED;
    public static final Color COLOR_FOOD_BONUS = Color.GOLD;
}