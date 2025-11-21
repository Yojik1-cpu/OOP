package blackjack.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RankTest {

    @Test
    void values_AreOrderedAndHaveCorrectBaseValues() {
        assertEquals(2, Rank.TWO.value());
        assertEquals(3, Rank.THREE.value());
        assertEquals(4, Rank.FOUR.value());
        assertEquals(5, Rank.FIVE.value());
        assertEquals(6, Rank.SIX.value());
        assertEquals(7, Rank.SEVEN.value());
        assertEquals(8, Rank.EIGHT.value());
        assertEquals(9, Rank.NINE.value());
        assertEquals(10, Rank.TEN.value());
        assertEquals(10, Rank.JACK.value());
        assertEquals(10, Rank.QUEEN.value());
        assertEquals(10, Rank.KING.value());
        assertEquals(11, Rank.ACE.value());
    }

    @Test
    void isAce_WorksCorrectly() {
        assertTrue(Rank.ACE.isAce());
        assertFalse(Rank.TWO.isAce());
        assertFalse(Rank.KING.isAce());
    }

    @Test
    void ordinal_OrderIsStandard() {
        Rank[] ranks = Rank.values();
        assertEquals(Rank.TWO, ranks[0]);
        assertEquals(Rank.ACE, ranks[ranks.length - 1]);
    }
}
