package blackjack.models;

import blackjack.engine.Deck;

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
