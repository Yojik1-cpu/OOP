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

        int patCpLen = pattern.codePointCount(0, pattern.length());
        if (patCpLen == 0) return result;

        int patCharLen = pattern.length();
        int chunkSize = Math.max(4096, patCharLen * 4);

        try (BufferedReader reader = Files.newBufferedReader(
                Path.of(filename),
                StandardCharsets.UTF_8
        )) {
            char[] buffer = new char[chunkSize];
            String tail = "";
            long offsetCp = 0;
            char pendingHighSurrogate = 0;

            int read;
            while ((read = reader.read(buffer)) != -1) {
                String chunk = new String(buffer, 0, read);

                if (pendingHighSurrogate != 0) {
                    chunk = pendingHighSurrogate + chunk;
                    pendingHighSurrogate = 0;
                }

                if (!chunk.isEmpty() && Character.isHighSurrogate(chunk.charAt(chunk.length() - 1))) {
                    pendingHighSurrogate = chunk.charAt(chunk.length() - 1);
                    chunk = chunk.substring(0, chunk.length() - 1);
                }

                String text = tail + chunk;

                int tailCp = tail.codePointCount(0, tail.length());

                int fromIndex = 0;
                while (true) {
                    int idx = text.indexOf(pattern, fromIndex);
                    if (idx == -1) break;

                    int idxCp = text.codePointCount(0, idx);
                    long globalIdxCp = offsetCp - tailCp + idxCp;

                    if (globalIdxCp >= 0) {
                        result.add(globalIdxCp);
                    }

                    fromIndex = idx + 1;
                }

                if (patCpLen > 1) {
                    int textCpLen = text.codePointCount(0, text.length());
                    int keepCp = patCpLen - 1;
                    int startCp = Math.max(0, textCpLen - keepCp);
                    int startChar = text.offsetByCodePoints(0, startCp);
                    tail = text.substring(startChar);
                } else {
                    tail = "";
                }
                offsetCp += chunk.codePointCount(0, chunk.length());
            }
        }

        return result;
    }
}
