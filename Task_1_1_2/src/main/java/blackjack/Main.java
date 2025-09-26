package blackjack;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * Точка входа в приложение.
 * Запускает игру в консоли.
 */
public class Main {
    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        UiIo ui = new UiIo();
        ui.run();
    }
}
