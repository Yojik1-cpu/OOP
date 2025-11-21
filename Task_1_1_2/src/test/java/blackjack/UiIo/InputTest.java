package blackjack.UiIo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InputTest {

    static class FakeOutput extends Output {
        int promptCount = 0;
        int invalidCount = 0;

        FakeOutput(I18n.Lang lang) {
            super(lang);
        }

        @Override
        public void printInputPrompt() {
            promptCount++;
        }

        @Override
        public void printInvalidInputPrompt() {
            invalidCount++;
        }
    }

    private InputStream originalIn;

    private void setStdin(String data) {
        originalIn = System.in;
        System.setIn(new ByteArrayInputStream(data.getBytes()));
    }

    @AfterEach
    void restoreStdin() {
        if (originalIn != null) {
            System.setIn(originalIn);
        }
    }

    @Test
    void readBinaryChoice_validImmediately() {
        setStdin("1\n");
        FakeOutput out = new FakeOutput(I18n.Lang.RU);
        Input in = new Input(out);

        String choice = in.readBinaryChoice();
        in.close();

        assertEquals("1", choice);
        assertEquals(1, out.promptCount);
        assertEquals(0, out.invalidCount);
    }

    @Test
    void readBinaryChoice_invalidThenValid() {
        setStdin("oops\n  0  \n");
        FakeOutput out = new FakeOutput(I18n.Lang.EN);
        Input in = new Input(out);

        String choice = in.readBinaryChoice();
        in.close();

        assertEquals("0", choice);
        assertEquals(1, out.promptCount);
        assertEquals(1, out.invalidCount);
    }
}
