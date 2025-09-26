package blackjack.engine;

import blackjack.units.Card;
import blackjack.units.Dealer;
import blackjack.units.Participant;

public class Game {
    public enum Outcome {PLAYER_BLACKJACK, DEALER_BLACKJACK, PLAYER_BUST, DEALER_BUST, PLAYER_WIN, DEALER_WIN, PUSH}

    private final Deck deck = new Deck();
    private final Participant player = new Participant("Игрок");
    private final Dealer dealer = new Dealer("Дилер");

    private boolean dealerHoleHidden = false;

    private boolean finished = false;
    private Outcome outcome = null;

    //начало раунда
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

    //ход игрока
    public void playerHit() {
        if (finished) return;
        player.takeCard(deck);
        if (player.getHand().isBust()) {
            finished = true;
            dealerHoleHidden = false;
            outcome = Outcome.PLAYER_BUST;
        }
    }

    //игрок больше не берёт карт
    public void playerStand() {
        if (finished) return;

        dealerHoleHidden = false;
        dealer.play(deck);

        determineOutcome();
    }

    //определяем исход
    private void determineOutcome() {
        int playerValue = player.getHand().getValue();
        int dealerValue = dealer.getHand().getValue();

        if (dealer.getHand().isBust()) {
            outcome = Outcome.DEALER_BUST;
        } else if (playerValue > dealerValue) {
            outcome = Outcome.PLAYER_WIN;
        } else if (playerValue < dealerValue) {
            outcome = Outcome.DEALER_WIN;
        } else {
            outcome = Outcome.PUSH;
        }

        finished = true;
    }

    // гетеры
    public Outcome getOutcome() {
        return outcome;
    }

    public Participant getPlayer() {
        return player;
    }

    public Dealer getDealer() {
        return dealer;
    }

    public boolean isFinished() {
        return finished;
    }
}
