package blackjack.UiIo;

import blackjack.engine.Game;
import blackjack.models.Card;
import blackjack.models.Dealer;
import blackjack.models.Participant;
import blackjack.models.Rank;
import blackjack.models.Suit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class OutputTest {

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream buffer;
    private Output out;

    @BeforeEach
    void setUp() {
        buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer));
        out = new Output(I18n.Lang.RU);
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    private String dump() {
        return buffer.toString();
    }

    @Test
    void menusAndPrompts_printSomethingSane() {
        out.printAskLang();
        out.printGreeting();
        out.printEntryMenu();
        out.printPlayAgainMenu();
        out.printInputPrompt();
        out.printInvalidInputPrompt();
        out.printPlayerTurnPrompt();

        String s = dump();

        assertTrue(s.contains("0 - Русский"));
        assertTrue(s.contains("1 - English"));

        assertTrue(s.contains("Приветствуем Вас в игре Блэкджек!"));
        assertTrue(s.contains("Желаете ли вы войти в игру?"));
        assertTrue(s.contains("Сыграем ещё?"));

        assertTrue(s.contains("> "));
        assertTrue(s.contains("Ожидается 1 или 0"));

        assertTrue(s.contains("Ваш ход"));
    }

    @Test
    void printHands_showsPlayerAndDealer_withAndWithoutHole() {
        Participant player = new Participant();
        Dealer dealer = new Dealer();

        player.getHand().addCard(new Card(Suit.SPADES, Rank.ACE));
        player.getHand().addCard(new Card(Suit.HEARTS, Rank.TEN));

        dealer.getHand().addCard(new Card(Suit.DIAMONDS, Rank.KING));
        dealer.getHand().addCard(new Card(Suit.CLUBS, Rank.NINE));

        out.printHands(player, dealer, true);
        String s1 = dump();
        assertTrue(s1.contains("Игрок:"));
        assertTrue(s1.contains("сумма 21"));
        assertTrue(s1.contains("Дилер: ["));
        assertTrue(s1.contains("??]"));


        buffer.reset();
        out.printHands(player, dealer, false);
        String s2 = dump();
        assertTrue(s2.contains("Дилер:"));
        assertTrue(s2.contains("сумма 19"));
        assertTrue(!s2.contains("??]"));
    }

    @Test
    void printGameResult_and_Score() {
        out.printGameResult(Game.Outcome.PLAYER_WIN);
        out.printScore(4, 3);

        String s = dump();
        assertTrue(s.contains("Итог: Игрок победил"));
        assertTrue(s.contains("ОБЩИЙ СЧЁТ:"));
        assertTrue(s.contains("Игрок - 4"));
        assertTrue(s.contains("Дилер - 3"));
    }

    @Test
    void printThanks_printsBye() {
        out.printThanksAndGoodbye();
        String s = dump();
        assertTrue(s.contains("Спасибо за игру! Пока!"));
    }
}
