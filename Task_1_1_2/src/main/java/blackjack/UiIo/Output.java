package blackjack.UiIo;

import blackjack.engine.Game;
import blackjack.models.Dealer;
import blackjack.models.Participant;

public class Output {
    private final I18n.Lang lang;

    public Output(I18n.Lang lang) {
        this.lang = lang;
    }

    public void printAskLang(){
        System.out.println("0 - " + I18n.Lang.RU.displayName());
        System.out.println("1 - " + I18n.Lang.EN.displayName());
    }
    public void printGreeting() {
        System.out.println(lang.t(I18n.Msg.GREETING));
    }

    public void printEntryMenu() {
        System.out.println(lang.t(I18n.Msg.ASK_ENTER));
    }

    public void printThanksAndGoodbye() {
        System.out.println(lang.t(I18n.Msg.THANKS_BYE));
    }

    public void printPlayAgainMenu() {
        System.out.println("\n" + lang.t(I18n.Msg.PLAY_AGAIN_Q));
    }

    public void printInputPrompt() {
        System.out.print(lang.t(I18n.Msg.INPUT_PROMPT));
    }

    public void printInvalidInputPrompt() {
        System.out.print(lang.t(I18n.Msg.PROMPT_10));
    }

    public void printPlayerTurnPrompt() {
        System.out.println("\n" + lang.t(I18n.Msg.YOUR_TURN));
    }

    public void printHands(Participant player, Dealer dealer, boolean hideDealerHole) {
        System.out.println(lang.t(I18n.Msg.PLAYER) + ": " +
                player.getHand().toDisplayLocalized(lang));

        String dealerHand;
        if (hideDealerHole) {
            dealerHand = dealer.getHand().toDisplayWithHoleHiddenLocalized(lang);
        } else {
            dealerHand = dealer.getHand().toDisplayLocalized(lang);
        }
        System.out.println(lang.t(I18n.Msg.DEALER_NAME) + ": " + dealerHand);
    }

    public void printGameResult(Game.Outcome outcome) {
        String localizedOutcome = I18n.localizeOutcome(outcome, lang);
        System.out.println("\n" + lang.t(I18n.Msg.RESULT, localizedOutcome));
    }

    public void printScore(int playerWins, int dealerWins) {
        System.out.println("\n" + lang.t(I18n.Msg.SCORE_TITLE));
        System.out.println(lang.t(I18n.Msg.PLAYER) + " - " + playerWins);
        System.out.println(lang.t(I18n.Msg.DEALER_NAME) + " - " + dealerWins);
    }

}