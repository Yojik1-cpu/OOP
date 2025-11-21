package blackjack.models;

import blackjack.UiIo.I18n;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CardTest {

    private static int id(int suitIdx, int rankIdx) {
        return suitIdx * 13 + rankIdx;
    }

    @Test
    void ctorById_Lowest_RU_EN() {
        Card c = new Card(id(0, 0));

        assertEquals(Suit.SPADES, c.getSuit());
        assertEquals(Rank.TWO, c.getRank());

        I18n.Lang ru = I18n.Lang.RU;
        I18n.Lang en = I18n.Lang.EN;
        assertEquals("2 Пики", ru.card(c));
        assertEquals("2 Spades", en.card(c));
    }

    @Test
    void ctorById_Highest_RU_EN() {
        Card c = new Card(id(3, 12));

        assertEquals(Suit.CLUBS, c.getSuit());
        assertEquals(Rank.ACE, c.getRank());

        I18n.Lang ru = I18n.Lang.RU;
        I18n.Lang en = I18n.Lang.EN;
        assertEquals("Туз Крести", ru.card(c));
        assertEquals("Ace Clubs", en.card(c));
    }

    @Test
    void rankValuesAndAces() {
        assertEquals(2, Rank.TWO.value());
        assertEquals(10, Rank.TEN.value());
        assertEquals(10, Rank.JACK.value());
        assertEquals(10, Rank.QUEEN.value());
        assertEquals(10, Rank.KING.value());
        assertEquals(11, Rank.ACE.value());
        assertTrue(Rank.ACE.isAce());
        assertFalse(Rank.TEN.isAce());
    }

    @Test
    void debugToStringIsNotNullOrEmpty() {
        Card c = new Card(Suit.HEARTS, Rank.QUEEN);
        String s = c.toString();
        assertNotNull(s);
        assertFalse(s.isEmpty());
    }
}
