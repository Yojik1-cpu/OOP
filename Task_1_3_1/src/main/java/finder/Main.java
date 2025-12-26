package finder;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        List<Long> positions = Finder.find("input.txt", "AA");
        System.out.println(positions);
    }
}
