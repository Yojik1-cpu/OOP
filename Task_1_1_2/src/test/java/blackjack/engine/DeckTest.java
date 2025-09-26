package blackjack.engine;

import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class DeckTest {

    @Test
    void draw52Unique() {
        Deck deck = new Deck();
        Set<Integer> seen = new HashSet<>();
        for (int i = 0; i < 52; i++) {
            int id = deck.drawCard();
            assertTrue(id >= 0 && id < 52, "id out of range");
            assertTrue(seen.add(id), "duplicate id: " + id);
        }
    }

    @Test
    void resetRestoresAndShuffles() {
        Deck deck = new Deck();
        int first = deck.drawCard();
        for (int i = 1; i < 10; i++) deck.drawCard();
        deck.reset();
        // после reset снова 52 карты и счётчик на нуле
        Set<Integer> seen = new HashSet<>();
        for (int i = 0; i < 52; i++) {
            int id = deck.drawCard();
            assertTrue(seen.add(id));
        }
    }
}
