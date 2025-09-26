package blackjack.units;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class HandTest {

    private static int id(int suitIdx, int rankIdx) {
        return suitIdx * 13 + rankIdx;
    }

    @Test
    void value_NoAces() {
        Hand h = new Hand();
        h.addCard(new Card(id(0, 8)));  // 10 Пики
        h.addCard(new Card(id(1, 0)));  // 2 Черви
        assertEquals(12, h.getValue());
        assertFalse(h.isBlackjack());
        assertFalse(h.isBust());
    }

    @Test
    void blackjack_10_Ace() {
        Hand h = new Hand();
        h.addCard(new Card(id(0, 8)));  // 10
        h.addCard(new Card(id(2, 12))); // туз
        assertEquals(21, h.getValue());
        assertTrue(h.isBlackjack());
        assertFalse(h.isBust());
    }

    @Test
    void aces_Flexible() {
        Hand h = new Hand();
        h.addCard(new Card(id(0, 12))); // туз
        h.addCard(new Card(id(1, 7)));  // 9
        assertEquals(20, h.getValue());

        h.addCard(new Card(id(2, 3)));  // 5
        // теперь туз = 1 => 1 + 9 + 5 = 15
        assertEquals(15, h.getValue());
        assertFalse(h.isBlackjack());
        assertFalse(h.isBust());
    }

    @Test
    void bust() {
        Hand h = new Hand();
        h.addCard(new Card(id(0, 8)));  // 10
        h.addCard(new Card(id(1, 8)));  // 10
        h.addCard(new Card(id(2, 5)));  // 7
        assertTrue(h.isBust());
        assertTrue(h.getValue() > 21);
        h.clear();
        assertEquals(0, h.getCards().size());
    }
}
