package blackjack.units;

import blackjack.engine.Deck;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParticipantTest {
    static class FakeDeck extends Deck {
        private final int[] seq;
        private int i = 0;

        FakeDeck(int... seq) {
            this.seq = seq;
        }

        @Override
        public int drawCard() {
            return seq[i++];
        }
    }

    private static int id(int suitIdx, int rankIdx) {
        return suitIdx * 13 + rankIdx;
    }

    @Test
    void takeCard_AddCard() {
        Participant p = new Participant("Игрок");
        Deck deck = new FakeDeck(id(0, 12)); // Туз Пики
        p.takeCard(deck);
        assertEquals(1, p.getHand().getCards().size());
        assertEquals("Туз Пики", p.getHand().getCards().get(0).toString());
        assertEquals("Игрок", p.getDisplayName());
    }

    @Test
    void clearHand() {
        Participant p = new Participant("X");
        Deck deck = new FakeDeck(0, 1, 2);
        p.takeCard(deck);
        p.clearHand();
        assertTrue(p.getHand().getCards().isEmpty());
    }
}
