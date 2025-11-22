package finder;

import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FinderTest {

    @TempDir
    Path tempDir;

    private Path createFile(String name, String content) throws IOException {
        Path file = tempDir.resolve(name);
        Files.writeString(file, content, StandardCharsets.UTF_8);
        return file;
    }

    @Test
    void nullPatternReturnsEmptyList() throws IOException {
        List<Long> result = Finder.find("does_not_matter.txt", null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void emptyPatternReturnsEmptyList() throws IOException {
        Path file = createFile("text.txt", "something here");
        List<Long> result = Finder.find(file.toString(), "");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void emptyFileReturnsEmptyList() throws IOException {
        Path file = createFile("empty.txt", "");
        List<Long> result = Finder.find(file.toString(), "abra");

        assertTrue(result.isEmpty());
    }

    @Test
    void patternLongerThanFile() throws IOException {
        Path file = createFile("short.txt", "abc");
        String pattern = "abcdef";
        List<Long> result = Finder.find(file.toString(), pattern);

        assertTrue(result.isEmpty());
    }


    @Test
    void exampleFromTask() throws IOException {
        Path file = createFile("input.txt", "абракадабра");
        List<Long> result = Finder.find(file.toString(), "бра");

        assertEquals(List.of(1L, 8L), result);
    }

    @Test
    void noOccurrences() throws IOException {
        Path file = createFile("no_occ.txt", "hello world");
        List<Long> result = Finder.find(file.toString(), "abra");

        assertTrue(result.isEmpty());
    }

    @Test
    void patternEqualsWholeFile() throws IOException {
        Path file = createFile("whole.txt", "pattern");
        List<Long> result = Finder.find(file.toString(), "pattern");

        assertEquals(List.of(0L), result);
    }

    @Test
    void singleCharPattern() throws IOException {
        Path file = createFile("chars.txt", "aaaa");

        List<Long> result = Finder.find(file.toString(), "a");
        assertEquals(List.of(0L, 1L, 2L, 3L), result);
    }

    @Test
    void overlappingMatches() throws IOException {
        // "aaaaa", ищем "aa" -> [0, 1, 2, 3]
        Path file = createFile("overlap.txt", "aaaaa");
        List<Long> result = Finder.find(file.toString(), "aa");

        assertEquals(List.of(0L, 1L, 2L, 3L), result);
    }

    @Test
    void utf8CyrillicAndLatinMix() throws IOException {
        String content = "абракадабра hellomisterBob абраабра";
        Path file = createFile("utf8.txt", content);

        List<Long> result1 = Finder.find(file.toString(), "бра");
        List<Long> result2 = Finder.find(file.toString(), "SOMETHINGWHATICANTFIND");
        assertFalse(result1.isEmpty());
        assertTrue(result2.isEmpty());
        assertEquals(4, result1.size());
    }

    @Test
    void largePatternTriggersBiggerChunkSize() throws IOException {
        // pattern > 1024 -> patLen * 4 > 4096
        StringBuilder sbPattern = new StringBuilder();
        for (int i = 0; i < 2000; i++) {
            sbPattern.append('x');
        }
        String pattern = sbPattern.toString();

        String content = "start-" + pattern + "-end";
        Path file = createFile("largePattern.txt", content);

        List<Long> result = Finder.find(file.toString(), pattern);

        // start- 6 символов, значит совпадение с 6
        assertEquals(List.of(6L), result);
    }

    @Test
    void bigFileGeneratedInTest() throws IOException {
        // большие данные ＼(＾▽＾)／
        Path file = tempDir.resolve("big.txt");

        final int lines = 2_000_000; //~ 40 Мб
        final String line = "абракадабра\n"; // в каждой строке два "бра"

        try (var writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            for (int i = 0; i < lines; i++) {
                writer.write(line);
            }
        }

        List<Long> result = Finder.find(file.toString(), "бра");
        assertEquals(lines * 2, result.size());
        assertFalse(result.isEmpty());
    }
}