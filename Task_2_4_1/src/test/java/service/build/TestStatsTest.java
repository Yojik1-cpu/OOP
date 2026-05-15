package service.build;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestStatsTest {

    @Test
    public void testTestStats() {
        TestStats stats = new TestStats(10, 2, 1,
                true, "output", 0.75);
        assertEquals(10, stats.total);
        assertEquals(2, stats.failed);
        assertEquals(1, stats.skipped);
        assertTrue(stats.commandSuccessful);
        assertEquals("output", stats.rawOutput);
        assertEquals(0.75, stats.codeCoverage);
    }
}
