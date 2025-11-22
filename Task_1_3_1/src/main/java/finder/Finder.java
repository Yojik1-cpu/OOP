package finder;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Finder {
    public static List<Long> find(String filename, String pattern) throws IOException {
        List<Long> result = new ArrayList<>();

        if (pattern == null || pattern.isEmpty()) {
            return result;
        }

        int patLen = pattern.length();
        int chunkSize = Math.max(4096, patLen * 4);

        try (BufferedReader reader = Files.newBufferedReader(
                Path.of(filename),
                StandardCharsets.UTF_8
        )) {
            char[] buffer = new char[chunkSize];
            String tail = "";
            long offset = 0;

            int read;
            while ((read = reader.read(buffer)) != -1) {
                String chunk = new String(buffer, 0, read);
                String text = tail + chunk;

                int fromIndex = 0;
                while (true) {
                    int idx = text.indexOf(pattern, fromIndex);
                    if (idx == -1) {
                        break;
                    }

                    long globalIdx = offset - tail.length() + idx;
                    if (globalIdx >= 0) {
                        result.add(globalIdx);
                    }

                    fromIndex = idx + 1;
                }

                if (patLen > 1) {
                    int startTail = Math.max(0, text.length() - (patLen - 1));
                    tail = text.substring(startTail);
                } else {
                    tail = "";
                }

                offset += read;
            }
        }
        return result;
    }
}
