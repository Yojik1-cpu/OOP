package service.build;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestStatsTest {

    @Test
    public void testTestStats() {
        TestStats stats = new TestStats(10, 2, 1, true, "OK", 0.8);

        assertEquals(10, stats.total);
        assertEquals(2, stats.failed);
        assertEquals(1, stats.skipped);
        assertTrue(stats.commandSuccessful);
        assertEquals("OK", stats.rawOutput);
        assertEquals(0.8, stats.codeCoverage);

        String statsString = stats.toString();
        assertTrue(statsString.contains("total=10"));
        assertTrue(statsString.contains("failed=2"));
        assertTrue(statsString.contains("skipped=1"));
        assertTrue(statsString.contains("commandSuccessful=true"));
        assertTrue(statsString.contains("codeCoverage=0.8"));
        assertTrue(statsString.contains("rawOutput='OK'"));
    }
}
