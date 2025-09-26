package blackjack.units;

import blackjack.engine.Deck;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DealerTest {

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
    void play_HitsTo17() {
        Dealer d = new Dealer("Дилер");
        // стартовая рука 6 + 5 = 11; колода выдаст 6, потом Даму => итог >= 17
        d.getHand().addCard(new Card(id(0, 4))); // 6
        d.getHand().addCard(new Card(id(1, 3))); // 5
        Deck deck = new FakeDeck(id(2, 4), id(3, 10)); // 6, Дама
        d.play(deck);
        assertTrue(d.getHand().getValue() >= 17);
    }
}
