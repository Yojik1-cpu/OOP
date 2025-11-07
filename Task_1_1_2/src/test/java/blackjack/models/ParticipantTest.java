package blackjack.models;

import static org.junit.jupiter.api.Assertions.*;

import blackjack.UiIo.I18n;
import blackjack.engine.Deck;
import org.junit.jupiter.api.Test;

class ParticipantTest {

    static class FakeDeck extends Deck {
        private final int[] seq;
        private int index = 0;

        FakeDeck(int... seq) {
            this.seq = seq;
        }

        @Override
        public int drawCard() {
            return seq[index++];
        }
    }

    private static int id(int suitIdx, int rankIdx) {
        return suitIdx * 13 + rankIdx;
    }

    @Test
    void takeCard_AddCard() {
        Participant p = new Participant();
        Deck deck = new FakeDeck(id(0, 12));

        p.takeCard(deck);

        assertEquals(1, p.getHand().getCards().size());

        Card c = p.getHand().getCards().get(0);

        assertEquals(Suit.SPADES, c.getSuit());
        assertEquals(Rank.ACE, c.getRank());
        assertEquals("Туз Пики", I18n.Lang.RU.card(c));
        assertEquals("Ace Spades", I18n.Lang.EN.card(c));
    }

    @Test
    void clearHand() {
        Participant p = new Participant();
        Deck deck = new FakeDeck(0, 1, 2);

        p.takeCard(deck);
        assertFalse(p.getHand().getCards().isEmpty());

        p.clearHand();
        assertTrue(p.getHand().getCards().isEmpty());
    }
}
