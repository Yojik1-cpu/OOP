package blackjack.units;

import blackjack.engine.Deck;

/**
 * Участник игры (игрок или дилер).
 * Имеет имя и руку, может брать карты из колоды.
 */
public class Participant {
    private final String displayName;
    private final Hand hand = new Hand();

    public Participant(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

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

    @Override
    public String toString() {
        return displayName
                + ": "
                + hand;
    }
}
