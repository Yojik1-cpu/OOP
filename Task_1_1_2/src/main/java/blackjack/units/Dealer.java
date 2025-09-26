package blackjack.units;

import blackjack.engine.Deck;

public class Dealer extends Participant {

    public Dealer(String displayName) {
        super(displayName);
    }

    //дилер играет, пока сумма карт не равна 17
    public void play(Deck deck) {
        while (getHand().getValue() < 17) {
            takeCard(deck);
        }
    }
}
