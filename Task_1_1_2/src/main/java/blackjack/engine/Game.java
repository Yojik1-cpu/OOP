package blackjack.engine;

import blackjack.units.Card;
import blackjack.units.Dealer;
import blackjack.units.Participant;

public class Game {
    public enum Outcome { PLAYER_BLACKJACK, DEALER_BLACKJACK, PLAYER_BUST, DEALER_BUST, PLAYER_WIN, DEALER_WIN, PUSH }

    private final Deck deck = new Deck();
    private final Participant player = new Participant("Игрок");
    private final Dealer dealer = new Dealer("Дилер");

    private boolean dealerHoleHidden = false;

    private boolean finished = false;
    private Outcome outcome = null;


    public void startRound() {
        // подготовка
        player.getHand().clear();
        dealer.getHand().clear();
        dealerHoleHidden = false;
        finished = false;
        outcome = null;

        deck.reset();


        // раздача: обе карты всем сразу
        player.takeCard(deck);
        dealer.takeCard(deck);
        player.takeCard(deck);
        dealer.takeCard(deck);
        dealerHoleHidden = true;

        // проверяем блэкджеки
        Outcome early = OutcomeResolver.resolveAfterInitialDeal(player.getHand(), dealer.getHand());
        if (early != null) {
            finished = true;
            dealerHoleHidden = false; //открываем для UI
            outcome = early;
        }
    }

    public void playerHit() {
        if (finished) return;
        player.takeCard(deck);
        if (player.getHand().isBust()) {
            finished = true;
            dealerHoleHidden = false;
            outcome = Outcome.PLAYER_BUST;
        }
    }

    //прописать ход дилера и исход сравнения рук

}
