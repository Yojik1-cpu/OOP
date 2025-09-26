package blackjack.engine;

import blackjack.units.Dealer;
import blackjack.units.Participant;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


class GameTest {

    @Test
    void startRound_DealsTwoEach() {
        Game g = new Game();
        g.startRound();
        Participant p = g.getPlayer();
        Dealer d = g.getDealer();

        assertEquals(2, p.getHand().getCards().size());
        assertEquals(2, d.getHand().getCards().size());
    }

    @Test
    void hitThenPossiblyStand_EndsWithOutcome() {
        Game g = new Game();
        g.startRound();

        int safety = 0;
        while (!g.isFinished() && safety++ < 5) {
            g.playerHit();
        }
        if (!g.isFinished()) {
            g.playerStand(); // дилер до 17
        }
        assertTrue(g.isFinished());
        assertNotNull(g.getOutcome());
    }
}
