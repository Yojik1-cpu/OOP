package blackjack.UiIo;

import java.util.Scanner;

public class Input {
    private final Scanner scanner;
    private final Output output;

    public Input(Output output) {
        this.scanner = new Scanner(System.in);
        this.output = output;
    }

    public String readBinaryChoice() {
        output.printInputPrompt();
        while (true) {
            String input = scanner.nextLine().trim();
            if (input.equals("1") || input.equals("0")) {
                return input;
            }
            output.printInvalidInputPrompt();
        }
    }

    public void close() {
        scanner.close();
    }
}