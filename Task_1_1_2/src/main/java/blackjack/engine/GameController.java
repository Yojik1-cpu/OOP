package blackjack.engine;

import blackjack.UiIo.I18n;
import blackjack.UiIo.Input;
import blackjack.UiIo.Output;

public class GameController {
    private final Game game;
    private Input input;
    private Output output;
    private int playerWins = 0;
    private int dealerWins = 0;

    public GameController(I18n.Lang lang) {
        this.game = new Game();
        this.output = new Output(lang);
        this.input = new Input(output);
    }

    public void run() {
        try (Input input = this.input) {
            output.printAskLang();
            String langChoice = input.readBinaryChoice();
            if (langChoice.equals("0")) {
                this.output = new Output(I18n.Lang.RU);
                this.input = new Input(output);
            }

            output.printGreeting();
            output.printEntryMenu();

            String choice = input.readBinaryChoice();
            if (choice.equals("0")) {
                output.printThanksAndGoodbye();
                return;
            }

            while (true) {
                playOneRound();

                output.printPlayAgainMenu();
                String again = input.readBinaryChoice();
                if (again.equals("0")) {
                    output.printThanksAndGoodbye();
                    return;
                }
            }
        }

    }

    private void applyOutcomeToScore(Game.Outcome o) {
        switch (o) {
            case PLAYER_BLACKJACK, PLAYER_WIN, DEALER_BUST -> playerWins++;
            case DEALER_BLACKJACK, DEALER_WIN, PLAYER_BUST -> dealerWins++;
        }
    }

    private void playOneRound() {
        game.startRound();
        output.printHands(game.getPlayer(), game.getDealer(), true);

        if (game.isFinished()) {
            output.printHands(game.getPlayer(), game.getDealer(), false);
            output.printGameResult(game.getOutcome());

            applyOutcomeToScore(game.getOutcome());
            output.printScore(playerWins, dealerWins);
            return;
        }

        while (true) {
            output.printPlayerTurnPrompt();
            String cmd = input.readBinaryChoice();

            if (cmd.equals("1")) {
                game.playerHit();
                output.printHands(game.getPlayer(), game.getDealer(), true);
                if (game.isFinished()) {
                    output.printHands(game.getPlayer(), game.getDealer(), false);
                    output.printGameResult(game.getOutcome());

                    applyOutcomeToScore(game.getOutcome());
                    output.printScore(playerWins, dealerWins);
                    return;
                }
            } else {
                game.playerStand();
                output.printHands(game.getPlayer(), game.getDealer(), false);
                output.printGameResult(game.getOutcome());

                applyOutcomeToScore(game.getOutcome());
                output.printScore(playerWins, dealerWins);
                return;
            }
        }
    }
}