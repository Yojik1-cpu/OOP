package snake.model;

public class LengthWinCondition implements WinCondition {
    private final int targetLength;

    public LengthWinCondition(int targetLength) {
        this.targetLength = targetLength;
    }

    @Override
    public boolean isWin(Game game) {
        return game.getSnake().getBody().size() >= targetLength;
    }
}