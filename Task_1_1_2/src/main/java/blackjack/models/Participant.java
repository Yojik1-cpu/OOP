package blackjack.models;

import blackjack.engine.Deck;

public class Participant {
    private final Hand hand = new Hand();

    public Participant() {}

    public void takeCard(Deck deck) {
        int id = deck.drawCard();
        Card card = new Card(id);
        hand.addCard(card);
    }

    public Hand getHand() {
        return hand;
    }

    public void clearHand() {
        hand.clear();
    }
}
