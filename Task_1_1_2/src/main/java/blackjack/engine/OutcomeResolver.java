package blackjack.engine;

import blackjack.units.Hand;

/**
 * Вспомогательный класс для определения исхода раунда.
 * Проверяет блэкджек после раздачи и определяет победителя в финале.
 */
public final class OutcomeResolver {
    private OutcomeResolver() {
    }

    /**
     * Определяет исход после начальной раздачи:
     * проверяет наличие блэкджека у игрока и дилера.
     */
    public static Game.Outcome resolveAfterInitialDeal(Hand player, Hand dealer) {
        boolean playerBlackjack = player.isBlackjack();
        boolean dealerBlackjack = dealer.isBlackjack();

        if (playerBlackjack && dealerBlackjack) {
            return Game.Outcome.PUSH;
        }
        if (playerBlackjack) {
            return Game.Outcome.PLAYER_BLACKJACK;
        }
        if (dealerBlackjack) {
            return Game.Outcome.DEALER_BLACKJACK;
        }
        return null;
    }

    /**
     * Определяет остальные возможные исходы.
     */
    public static Game.Outcome resolveFinal(Hand player, Hand dealer) {
        // перебор
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
