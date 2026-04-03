package snake;

import org.junit.jupiter.api.Test;
import snake.model.*;
import static org.junit.jupiter.api.Assertions.*;

class WinConditionTest {

    @Test
    void isWinFalseWhenLengthIsLess() {
        WinCondition winCondition = new LengthWinCondition(10);
        Game game = new Game(100, 100, 0, winCondition, (w, h, o) -> null);

        game.getSnake().grow(8);
        for (int i = 0; i < 8; i++) {
            game.getSnake().move();
        }
        assertEquals(9, game.getSnake().getBody().size());
        assertFalse(winCondition.isWin(game));
    }

    @Test
    void isWinTrueWhenLengthIsEqual() {
        WinCondition winCondition = new LengthWinCondition(10);
        Game game = new Game(100, 100, 0, winCondition, (w, h, o) -> null);
        game.getSnake().grow(9);
        for (int i = 0; i < 9; i++) {
            game.getSnake().move();
        }
        assertEquals(10, game.getSnake().getBody().size());
        assertTrue(winCondition.isWin(game));
    }

    @Test
    void isWinTrueWhenLengthIsGreater() {
        WinCondition winCondition = new LengthWinCondition(10);
        Game game = new Game(100, 100, 0, winCondition, (w, h, o) -> null);
        game.getSnake().grow(10);
        for (int i = 0; i < 10; i++) {
            game.getSnake().move();
        }
        assertEquals(11, game.getSnake().getBody().size());
        assertTrue(winCondition.isWin(game));
    }
}