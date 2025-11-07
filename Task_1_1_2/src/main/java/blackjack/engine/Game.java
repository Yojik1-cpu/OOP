package blackjack.engine;

import blackjack.models.Dealer;
import blackjack.models.Participant;

public class Game {
    public enum Outcome {
        PLAYER_BLACKJACK,
        DEALER_BLACKJACK,
        BOTH_BLACKJACK,
        PLAYER_BUST,
        DEALER_BUST,
        PLAYER_WIN,
        DEALER_WIN,
        PUSH
    }

    private final Deck deck = new Deck();
    private final Participant player = new Participant();
    private final Dealer dealer = new Dealer();


    private boolean finished = false;
    private Outcome outcome = null;

    public void startRound() {
        // подготовка
        player.getHand().clear();
        dealer.getHand().clear();
        finished = false;
        outcome = null;

        deck.reset();

        // раздача: обе карты всем сразу
        player.takeCard(deck);
        dealer.takeCard(deck);
        player.takeCard(deck);
        dealer.takeCard(deck);

        // проверяем блэкджеки
        Outcome early = OutcomeResolver.resolveAfterInitialDeal(player.getHand(),
                dealer.getHand());
        if (early != null) {
            finished = true;
            outcome = early;
        }
    }

    public void playerHit() {
        if (finished) {
            return;
        }
        player.takeCard(deck);
        if (player.getHand().isBust()) {
            finished = true;
            outcome = Outcome.PLAYER_BUST;
        }
    }

    public void playerStand() {
        if (finished) {
            return;
        }
        dealer.play(deck);
        determineOutcome();
    }

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
