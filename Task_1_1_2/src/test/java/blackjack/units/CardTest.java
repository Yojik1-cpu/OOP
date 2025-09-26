package blackjack.units;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CardTest {
    private static int id(int suitIdx, int rankIdx) { return suitIdx * 13 + rankIdx; }

    @Test
    void ctorById_Lowest() {
        Card c = new Card(id(0, 0)); // 0 = 2 Пики
        assertEquals("Пики", c.getSuit());
        assertEquals("2", c.getRank());
        assertEquals("2 Пики", c.toString());
    }

    @Test
    void ctorById_Highest() {
        Card c = new Card(id(3, 12)); // 51 = Туз Крести
        assertEquals("Крести", c.getSuit());
        assertEquals("Туз", c.getRank());
        assertEquals("Туз Крести", c.toString());
    }
}
