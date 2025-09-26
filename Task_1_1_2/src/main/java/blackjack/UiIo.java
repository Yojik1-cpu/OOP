package blackjack;

import blackjack.engine.Game;
import blackjack.units.Card;
import blackjack.units.Dealer;
import blackjack.units.Participant;
import java.util.List;
import java.util.Scanner;

/**
 * Консольный интерфейс для игры в блэкджек.
 * Управление осуществляется клавишами 1 и 0.
 */
public class UiIo {
    private final Game game = new Game();
    private final Scanner in = new Scanner(System.in);

    /**
     * Запускает основной игровой цикл.
     * Запускает раунды и предлагает сыграть снова после окончания каждого раунда.
     */
    public void run() {
        System.out.println(
                "Приветствуем Вас в игре Блэкджек!\n"
                        + "Желаете ли вы войти в игру?\n"
                        + "1 - Начать игру\n"
                        + "0 - Выйти из игры"
        );

        String choice = read10();
        if (choice.equals("0")) {
            System.out.println("Спасибо за игру! Пока!");
            return;
        }

        while (true) {
            playOneRound();

            System.out.println(
                    "Хочешь сыграть ещё?\n"
                            + "1 - Войти в игру\n"
                            + "0 - Выйти"
            );

            String again = read10();
            if (again.equals("0")) {
                System.out.println("Спасибо за игру! Пока!");
                return;
            }
        }
    }

    /**
     * Проводит один раунд игры
     */
    private void playOneRound() {
        game.startRound();

        printHands(true);

        // блекджек
        if (game.isFinished()) {
            printHands(false);
            System.out.println("Итог: "
                    + game.getOutcome());
            return;
        }

        while (true) {
            System.out.println("Ваш ход: 1 - Взять карту, 0 - Остановиться");
            String cmd = read10();

            if (cmd.equals("1")) {
                game.playerHit();
                printHands(true);
                if (game.isFinished()) {
                    printHands(false);
                    System.out.println("Итог: "
                            + localizeOutcome(game.getOutcome()));
                    return;
                }
            } else {
                game.playerStand();
                printHands(false);
                System.out.println("Итог: "
                        + localizeOutcome(game.getOutcome()));
                return;
            }
        }
    }

    // вывод строки для ввода пользователя
    private String read10() {
        System.out.print("> ");
        while (true) {
            String s = in.nextLine().trim();
            if (s.equals("1") || s.equals("0")) {
                return s;
            }
            System.out.print("Ожидается 1 или 0. Повторите ввод: ");
        }
    }

    // вывод "рук"
    private void printHands(boolean hideDealerHole) {
        Participant p = game.getPlayer();
        Dealer d = game.getDealer();

        System.out.println("Игрок: "
                + p.getHand().getCards()
                + " (сумма "
                + p.getHand().getValue()
                + ")");

        if (hideDealerHole) {
            List<Card> cards = d.getHand().getCards();
            System.out.println(d.getDisplayName()
                    + ": ["
                    + cards.get(0)
                    + ", ??]");
        } else {
            System.out.println(d.getDisplayName()
                    + ": "
                    + d.getHand().getCards()
                    + " (сумма "
                    + d.getHand().getValue()
                    + ")");
        }
    }

    //локализация
    private String localizeOutcome(Game.Outcome outcome) {
        switch (outcome) {
            case PLAYER_BLACKJACK:
                return "Блэкджек у игрока";
            case DEALER_BLACKJACK:
                return "Блэкджек у дилера";
            case PLAYER_BUST:
                return "Перебор у игрока";
            case DEALER_BUST:
                return "Перебор у дилера";
            case PLAYER_WIN:
                return "Игрок победил";
            case DEALER_WIN:
                return "Дилер победил";
            case PUSH:
                return "Ничья";
            default:
                return "?";
        }
    }
}
