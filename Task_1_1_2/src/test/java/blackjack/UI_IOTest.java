package blackjack;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class UI_IOTest {

    private InputStream oldIn;
    private PrintStream oldOut;
    private ByteArrayOutputStream bout;

    //создание перед каждым тестом
    @BeforeEach
    void setUp() {
        oldIn = System.in;
        oldOut = System.out;
        bout = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bout));
    }

    //завершение после каждого теста
    @AfterEach
    void tearDown() {
        System.setIn(oldIn);
        System.setOut(oldOut);
    }

    @Test
    void happyPath_StartStandExit() {
        // сценарий - старт => стоп => выйти
        String script = String.join(System.lineSeparator(), "1", "0", "0") + System.lineSeparator();
        System.setIn(new ByteArrayInputStream(script.getBytes()));

        new UI_IO().run();

        String out = bout.toString();
        assertTrue(out.contains("Приветствуем Вас в игре"));
        assertTrue(out.contains("Ваш ход"));
        assertTrue(out.contains("Итог:"));
        assertTrue(out.contains("Спасибо за игру!"));
    }
}
