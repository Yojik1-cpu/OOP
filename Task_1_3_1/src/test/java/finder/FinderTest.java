package finder;

import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
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
        String pattern = "x".repeat(2000);

        String content = "start-" + pattern + "-end";
        Path file = createFile("largePattern.txt", content);

        List<Long> result = Finder.find(file.toString(), pattern);

        assertEquals(List.of(6L), result);
    }

    @Test
    void findsPatternInSeveralGiBFile() throws IOException {
        Path file = tempDir.resolve("big.txt");

        long gib = 10;
        long targetSize = gib * 1024L * 1024L * 1024L;

        byte[] patternBytes = "abra".getBytes(StandardCharsets.UTF_8);
        String pattern = "abra";

        long pos1 = 0;
        long pos2 = targetSize - patternBytes.length;

        byte[] block = new byte[8 * 1024 * 1024];
        Arrays.fill(block, (byte) 'a');

        try (var out = Files.newOutputStream(file)) {
            long remaining = targetSize;
            while (remaining > 0) {
                int n = (int) Math.min(block.length, remaining);
                out.write(block, 0, n);
                remaining -= n;
            }
        }

        try (RandomAccessFile raf = new RandomAccessFile(file.toFile(), "rw")) {
            raf.seek(pos1);
            raf.write(patternBytes);

            raf.seek(pos2);
            raf.write(patternBytes);
        }

        List<Long> result = Finder.find(file.toString(), pattern);

        assertEquals(List.of(pos1, pos2), result);
    }

    @Test
    void japaneseSymbols() throws IOException {
        Path file = createFile("jp.txt", "あいうえおあいう");
        List<Long> result = Finder.find(file.toString(), "いう");

        // あ(0) い(1) う(2) え(3) お(4) あ(5) い(6) う(7)
        assertEquals(List.of(1L, 6L), result);
    }

    @Test
    void emojiPattern() throws IOException {
        Path file = createFile("emoji.txt", "a😀b😀c");
        List<Long> result = Finder.find(file.toString(), "😀");
        assertEquals(List.of(1L, 3L), result);
    }

}