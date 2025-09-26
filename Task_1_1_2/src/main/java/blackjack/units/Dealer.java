package blackjack.units;

import blackjack.engine.Deck;

/**
 * Дилер в блэкджеке.
 * Автоматически добирает карты до 17 очков.
 */
public class Dealer extends Participant {

    public Dealer(String displayName) {
        super(displayName);
    }

    public void play(Deck deck) {
        while (getHand().getValue() < 17) {
            takeCard(deck);
        }
    }
}
