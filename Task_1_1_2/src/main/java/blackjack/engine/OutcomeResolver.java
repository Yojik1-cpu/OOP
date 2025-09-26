package blackjack.engine;

import blackjack.units.Hand;

/**
 * Вспомогательный класс для определения исхода раунда.
 * Проверяет блэкджек после раздачи и определяет победителя в финале.
 */
public final class OutcomeResolver {
    private OutcomeResolver() {
    }

    //отсматриваем моментальные блэкджеки
    public static Game.Outcome resolveAfterInitialDeal(Hand player, Hand dealer) {
        boolean pBj = player.isBlackjack();
        boolean dBj = dealer.isBlackjack();

        if (pBj && dBj) {
            return Game.Outcome.PUSH;
        }
        if (pBj) {
            return Game.Outcome.PLAYER_BLACKJACK;
        }
        if (dBj) {
            return Game.Outcome.DEALER_BLACKJACK;
        }
        return null;
    }

    //остальные исходы
    public static Game.Outcome resolveFinal(Hand player, Hand dealer) {
        // перебор
        if (player.isBust()) {
            return Game.Outcome.PLAYER_BUST;
        }
        if (dealer.isBust()) {
            return Game.Outcome.DEALER_BUST;
        }

        // сравнение очков
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
