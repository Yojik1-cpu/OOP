package blackjack;

import blackjack.UiIo.I18n;
import blackjack.engine.GameController;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        I18n.Lang lang = I18n.Lang.EN;
        GameController gameController = new GameController(lang);
        gameController.run();
    }
}