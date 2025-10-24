package blackjack.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import blackjack.models.Card;
import blackjack.models.Hand;
import org.junit.jupiter.api.Test;


class OutcomeResolverTest {

    private static int id(int s, int r) {
        return s * 13 + r;
    }

    @Test
    void resolveAfterInitialDeal_AllCases() {
        Hand p1 = new Hand();
        p1.addCard(new Card(id(0, 8)));
        p1.addCard(new Card(id(1, 12)));
        Hand d1 = new Hand();
        d1.addCard(new Card(id(2, 0)));
        d1.addCard(new Card(id(3, 0)));
        assertEquals(Game.Outcome.PLAYER_BLACKJACK,
                OutcomeResolver.resolveAfterInitialDeal(p1, d1));

        Hand p2 = new Hand();
        p2.addCard(new Card(id(0, 0)));
        p2.addCard(new Card(id(1, 0)));
        Hand d2 = new Hand();
        d2.addCard(new Card(id(2, 8)));
        d2.addCard(new Card(id(3, 12)));
        assertEquals(Game.Outcome.DEALER_BLACKJACK,
                OutcomeResolver.resolveAfterInitialDeal(p2, d2));

        Hand p3 = new Hand();
        p3.addCard(new Card(id(0, 8)));
        p3.addCard(new Card(id(1, 12)));
        Hand d3 = new Hand();
        d3.addCard(new Card(id(2, 8)));
        d3.addCard(new Card(id(3, 12)));
        assertEquals(Game.Outcome.BOTH_BLACKJACK,
                OutcomeResolver.resolveAfterInitialDeal(p3, d3));

        Hand p4 = new Hand();
        p4.addCard(new Card(id(0, 0)));
        p4.addCard(new Card(id(1, 1)));
        Hand d4 = new Hand();
        d4.addCard(new Card(id(2, 0)));
        d4.addCard(new Card(id(3, 1)));
        assertNull(OutcomeResolver.resolveAfterInitialDeal(p4, d4));
    }

    @Test
    void resolveFinal_AllCases() {
        Hand p = new Hand();
        Hand d = new Hand();

        // перебор игрока
        p.clear();
        d.clear();
        p.addCard(new Card(id(0, 8)));
        p.addCard(new Card(id(1, 8)));
        p.addCard(new Card(id(2, 6))); // 10+10+8 = 28
        d.addCard(new Card(id(0, 0)));
        d.addCard(new Card(id(1, 0)));
        assertEquals(Game.Outcome.PLAYER_BUST, OutcomeResolver.resolveFinal(p, d));

        // перебор дилера
        p.clear();
        d.clear();
        p.addCard(new Card(id(0, 8)));
        p.addCard(new Card(id(1, 2))); // 10 + 4 = 14
        d.addCard(new Card(id(0, 8)));
        d.addCard(new Card(id(1, 8)));
        d.addCard(new Card(id(2, 6)));
        assertEquals(Game.Outcome.DEALER_BUST, OutcomeResolver.resolveFinal(p, d));

        // сравнения очков
        p.clear();
        d.clear();
        p.addCard(new Card(id(0, 7)));
        d.addCard(new Card(id(1, 6))); // 9 vs 8
        assertEquals(Game.Outcome.PLAYER_WIN, OutcomeResolver.resolveFinal(p, d));

        p.clear();
        d.clear();
        p.addCard(new Card(id(0, 6)));
        d.addCard(new Card(id(1, 7))); // 8 vs 9
        assertEquals(Game.Outcome.DEALER_WIN, OutcomeResolver.resolveFinal(p, d));

        p.clear();
        d.clear();
        p.addCard(new Card(id(0, 6)));
        d.addCard(new Card(id(1, 6))); // 8 vs 8
        assertEquals(Game.Outcome.PUSH, OutcomeResolver.resolveFinal(p, d));
    }
}
