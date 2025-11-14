package hashtable;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

public class HashTableTest {

    @Test
    public void testEmptyTableInitially() {
        HashTable<String, Integer> table = new HashTable<>();
        assertEquals(0, table.size());
        assertNull(table.get("nope"));
        assertFalse(table.containsKey("nope"));
        assertEquals("{}", table.toString());
    }

    @Test
    public void testPutAndGet() {
        HashTable<String, Number> table = new HashTable<>();

        table.put("one", 1);
        table.put("two", 2.0);

        assertEquals(2, table.size());
        assertEquals(1, table.get("one"));
        assertEquals(2.0, table.get("two"));
    }

    @Test
    public void testUpdateExistingKey() {
        HashTable<String, Number> table = new HashTable<>();

        table.put("one", 1);
        boolean updated = table.update("one", 1.0);

        assertTrue(updated);
        assertEquals(1.0, table.get("one"));
    }

    @Test
    public void testUpdateNonExistingKey() {
        HashTable<String, Number> table = new HashTable<>();

        boolean updated = table.update("one", 1.0);

        assertFalse(updated);
        assertNull(table.get("one"));
    }

    @Test
    public void testContainsKeyAndRemove() {
        HashTable<String, Integer> table = new HashTable<>();

        table.put("a", 10);
        table.put("b", 20);

        assertTrue(table.containsKey("a"));
        assertTrue(table.containsKey("b"));
        assertFalse(table.containsKey("c"));

        Integer removed = table.remove("a");
        assertEquals(Integer.valueOf(10), removed);
        assertFalse(table.containsKey("a"));
        assertEquals(1, table.size());

        assertNull(table.remove("zzz"));
    }

    @Test
    public void testIteratorIteratesOverAllElements() {
        HashTable<String, Integer> table = new HashTable<>();

        table.put("one", 1);
        table.put("two", 2);
        table.put("three", 3);

        int sum = 0;
        int count = 0;
        for (Entry<String, Integer> entry : table) {
            sum += entry.getValue();
            count++;
        }

        assertEquals(3, count);
        assertEquals(1 + 2 + 3, sum);
    }

    @Test
    public void testIteratorNoSuchElementException() {
        HashTable<String, Integer> table = new HashTable<>();

        Iterator<Entry<String, Integer>> it = table.iterator();
        assertFalse(it.hasNext());
        assertThrows(NoSuchElementException.class, it::next);
    }

    @Test
    public void testConcurrentModificationExceptionOnIterator() {
        HashTable<String, Integer> table = new HashTable<>();

        table.put("one", 1);
        table.put("two", 2);

        Iterator<Entry<String, Integer>> it = table.iterator();
        assertTrue(it.hasNext());
        it.next();
        table.put("three", 3);
        assertThrows(ConcurrentModificationException.class, it::next);
    }

    @Test
    public void testEqualsAndHashCode() {
        HashTable<String, Integer> t1 = new HashTable<>();
        HashTable<String, Integer> t2 = new HashTable<>();
        HashTable<String, Integer> t3 = new HashTable<>();

        t1.put("one", 1);
        t1.put("two", 2);

        t2.put("one", 1);
        t2.put("two", 2);

        t3.put("one", 1);
        t3.put("two", 222);
        assertTrue(t1.equals(t2));
        assertTrue(t2.equals(t1));
        assertEquals(t1.hashCode(), t2.hashCode());
        assertFalse(t1.equals(t3));
        assertFalse(t3.equals(t1));
    }

    @Test
    public void testToStringFormat() {
        HashTable<String, Integer> table = new HashTable<>();

        table.put("one", 1);
        table.put("two", 2);

        String s = table.toString();
        assertTrue(s.startsWith("{"));
        assertTrue(s.endsWith("}"));
        assertTrue(s.contains("one"));
        assertTrue(s.contains("two"));
    }

    @Test
    public void testResizeIsTriggeredAndKeepsData() {
        HashTable<Integer, Integer> table = new HashTable<>();

        int count = 100;
        for (int i = 0; i < count; i++) {
            table.put(i, i * 10);
        }

        assertEquals(count, table.size());
        assertEquals(Integer.valueOf(0), table.get(0));
        assertEquals(Integer.valueOf(10), table.get(1));
        assertEquals(Integer.valueOf(990), table.get(99));
    }
}
