package blackjack.engine;

import blackjack.models.Dealer;
import blackjack.models.Participant;
import blackjack.UiIo.I18n;
import blackjack.UiIo.Input;
import blackjack.UiIo.Output;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.Deque;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameControllerTest {

    static class FakeInput extends Input {
        private final Deque<String> script = new ArrayDeque<>();

        FakeInput(Output output, String... steps) {
            super(output);
            for (String s : steps) script.addLast(s);
        }

        @Override
        public String readBinaryChoice() {
            return script.removeFirst();
        }
    }

    static class FakeOutput extends Output {
        int scoreCalls = 0;
        String lastCall = "";

        FakeOutput(I18n.Lang lang) {
            super(lang);
        }

        @Override
        public void printAskLang() {
            lastCall = "askLang";
        }

        @Override
        public void printGreeting() {
            lastCall = "greet";
        }

        @Override
        public void printEntryMenu() {
            lastCall = "entry";
        }

        @Override
        public void printThanksAndGoodbye() {
            lastCall = "bye";
        }

        @Override
        public void printPlayAgainMenu() {
            lastCall = "again";
        }

        @Override
        public void printScore(int pw, int dw) {
            scoreCalls++;
            lastCall = "score:" + pw + ":" + dw;
        }
    }

    static class FakeGame extends Game {
        private final Participant player = new Participant();
        private final Dealer dealer = new Dealer();
        private boolean finished = true;
        private Outcome outcome = Outcome.PLAYER_WIN;

        @Override
        public void startRound() {
        }

        @Override
        public boolean isFinished() {
            return finished;
        }

        @Override
        public Outcome getOutcome() {
            return outcome;
        }

        @Override
        public Participant getPlayer() {
            return player;
        }

        @Override
        public Dealer getDealer() {
            return dealer;
        }
    }

    private static void set(Object target, String field, Object value) {
        try {
            Field f = target.getClass().getDeclaredField(field);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void run_exitFromEntryMenu() {
        GameController gc = new GameController(I18n.Lang.RU);
        FakeOutput out = new FakeOutput(I18n.Lang.RU);
        FakeInput in = new FakeInput(out,
                "1", // язык не меняем
                "0"  // выйти из меню
        );
        set(gc, "output", out);
        set(gc, "input", in);

        gc.run();

        assertEquals("bye", out.lastCall);
        assertEquals(0, out.scoreCalls);
    }

    @Test
    void run_oneRound_immediateFinish_updatesScore() {
        GameController gc = new GameController(I18n.Lang.RU);
        FakeOutput out = new FakeOutput(I18n.Lang.RU);
        FakeInput in = new FakeInput(out,
                "1", // язык
                "1", // начать игру
                "0"  // не играть ещё
        );
        FakeGame game = new FakeGame();

        set(gc, "output", out);
        set(gc, "input", in);
        set(gc, "game", game);

        gc.run();

        assertTrue(out.lastCall.equals("bye"));
        assertEquals(1, out.scoreCalls);
    }
}
