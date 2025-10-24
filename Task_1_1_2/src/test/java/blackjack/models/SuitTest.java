package blackjack.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SuitTest {

    @Test
    void values_ContainAllAndInDeclaredOrder() {
        Suit[] expected = { Suit.SPADES, Suit.HEARTS, Suit.DIAMONDS, Suit.CLUBS };
        Suit[] actual = Suit.values();
        assertArrayEquals(expected, actual);
        assertEquals(4, actual.length);
    }

    @Test
    void valueOf_WorksForEachName() {
        assertEquals(Suit.SPADES, Suit.valueOf("SPADES"));
        assertEquals(Suit.HEARTS, Suit.valueOf("HEARTS"));
        assertEquals(Suit.DIAMONDS, Suit.valueOf("DIAMONDS"));
        assertEquals(Suit.CLUBS, Suit.valueOf("CLUBS"));
    }

    @Test
    void valueOf_ThrowsOnUnknownName() {
        assertThrows(IllegalArgumentException.class, () -> Suit.valueOf("UNKNOWN"));
    }

    @Test
    void toString_DefaultEqualsName() {
        for (Suit s : Suit.values()) {
            assertNotNull(s.toString());
            assertEquals(s.name(), s.toString());
        }
    }

    @Test
    void ordinal_OrderIsDeclared() {
        assertEquals(0, Suit.SPADES.ordinal());
        assertEquals(1, Suit.HEARTS.ordinal());
        assertEquals(2, Suit.DIAMONDS.ordinal());
        assertEquals(3, Suit.CLUBS.ordinal());
    }
}
