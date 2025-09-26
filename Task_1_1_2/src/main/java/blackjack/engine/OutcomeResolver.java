package blackjack.engine;

import blackjack.units.Hand;

public final class OutcomeResolver {
    private OutcomeResolver() {}

    //отсматриваем моментальные блэкджеки
    public static Game.Outcome resolveAfterInitialDeal(Hand player, Hand dealer) {
        boolean pBJ = player.isBlackjack();
        boolean dBJ = dealer.isBlackjack();

        if (pBJ && dBJ) return Game.Outcome.PUSH;
        if (pBJ)        return Game.Outcome.PLAYER_BLACKJACK;
        if (dBJ)        return Game.Outcome.DEALER_BLACKJACK;

        return null;
    }

    //остальные исходы
    public static Game.Outcome resolveFinal(Hand player, Hand dealer) {
        // перебор
        if (player.isBust()) return Game.Outcome.PLAYER_BUST;
        if (dealer.isBust()) return Game.Outcome.DEALER_BUST;

        // сравнение очков
        int pv = player.getValue();
        int dv = dealer.getValue();

        if (pv > dv)  return Game.Outcome.PLAYER_WIN;
        if (pv < dv)  return Game.Outcome.DEALER_WIN;
        return Game.Outcome.PUSH;
    }
}
