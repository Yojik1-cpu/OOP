package blackjack.engine;

import blackjack.models.Hand;

public final class OutcomeResolver {
    private OutcomeResolver() {
    }

    public static Game.Outcome resolveAfterInitialDeal(Hand player, Hand dealer) {
        boolean playerBlackjack = player.isBlackjack();
        boolean dealerBlackjack = dealer.isBlackjack();

        if (playerBlackjack && dealerBlackjack) {
            return Game.Outcome.BOTH_BLACKJACK;
        }
        if (playerBlackjack) {
            return Game.Outcome.PLAYER_BLACKJACK;
        }
        if (dealerBlackjack) {
            return Game.Outcome.DEALER_BLACKJACK;
        }
        return null;
    }

    public static Game.Outcome resolveFinal(Hand player, Hand dealer) {
        if (player.isBust()) {
            return Game.Outcome.PLAYER_BUST;
        }
        if (dealer.isBust()) {
            return Game.Outcome.DEALER_BUST;
        }

        int pv = player.getValue();
        int dv = dealer.getValue();

        if (pv > dv) {
            return Game.Outcome.PLAYER_WIN;
        }
        if (pv < dv) {
            return Game.Outcome.DEALER_WIN;
        }
        return Game.Outcome.PUSH;
    }
}
