package foundPrime;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ThreadPrimeFinderTest {

    @Test
    void hasNonPrime_whenArrayContainsNonPrime_returnsTrue() {
        ThreadPrimeFinder finder = new ThreadPrimeFinder(2);
        long[] numbers = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        assertTrue(finder.hasNonPrime(numbers));
    }

    @Test
    void hasNonPrime_emptyArray_returnsFalse() {
        ThreadPrimeFinder finder = new ThreadPrimeFinder(4);
        assertFalse(finder.hasNonPrime(new long[]{}));
    }

    @Test
    void hasNonPrime_null_returnsFalse() {
        ThreadPrimeFinder finder = new ThreadPrimeFinder(4);
        assertFalse(finder.hasNonPrime(null));
    }

    @Test
    void hasNonPrime_whenAllPrimes_returnsFalse() {
        ThreadPrimeFinder finder = new ThreadPrimeFinder(3);
        long[] numbers = {2, 3, 5, 7, 11};
        assertFalse(finder.hasNonPrime(numbers));
    }

    @Test
    void singleThread_sameResultAsSequential() {
        long[] numbers = {1, 4, 6, 2, 8, 9, 10};
        SequentialPrimeFinder sequential = new SequentialPrimeFinder();
        ThreadPrimeFinder parallel = new ThreadPrimeFinder(1);
        assertEquals(sequential.hasNonPrime(numbers), parallel.hasNonPrime(numbers));
    }

    @Test
    void constructor_throwsWhenThreadCountLessThanOne() {
        assertThrows(IllegalArgumentException.class, () -> new ThreadPrimeFinder(0));
        assertThrows(IllegalArgumentException.class, () -> new ThreadPrimeFinder(-1));
    }

    @Test
    void getThreadCount_returnsConfiguredValue() {
        assertEquals(4, new ThreadPrimeFinder(4).getThreadCount());
        assertEquals(1, new ThreadPrimeFinder(1).getThreadCount());
    }

    @Test
    void hasNonPrime_mixedArray_returnsTrue() {
        long[] numbers = {6, 8, 7, 13, 5, 9, 4};
        assertTrue(new ThreadPrimeFinder(2).hasNonPrime(numbers));
    }

    @Test
    void hasNonPrime_allLargePrimes_returnsFalse() {
        long[] numbers = {
                20319251, 6997901, 6997927, 6997937, 17858849, 6997967,
                6998009, 6998029, 6998039, 20165149, 6998051, 6998053
        };
        assertFalse(new ThreadPrimeFinder(4).hasNonPrime(numbers));
    }

    @Test
    void hasNonPrime_resultSameForDifferentThreadCounts() {
        long[] withNonPrime = {2, 3, 4, 5, 7};
        long[] onlyPrimes = {2, 3, 5, 7, 11, 13};
        for (int threads : new int[]{1, 2, 3, 5, 8}) {
            ThreadPrimeFinder finder = new ThreadPrimeFinder(threads);
            assertTrue(finder.hasNonPrime(withNonPrime), "threads=" + threads);
            assertFalse(finder.hasNonPrime(onlyPrimes), "threads=" + threads);
        }
    }

    @Test
    void hasNonPrime_singleElementNonPrime_returnsTrue() {
        assertTrue(new ThreadPrimeFinder(2).hasNonPrime(new long[]{1}));
        assertTrue(new ThreadPrimeFinder(2).hasNonPrime(new long[]{4}));
        assertTrue(new ThreadPrimeFinder(2).hasNonPrime(new long[]{9}));
    }

    @Test
    void hasNonPrime_singleElementPrime_returnsFalse() {
        assertFalse(new ThreadPrimeFinder(2).hasNonPrime(new long[]{2}));
        assertFalse(new ThreadPrimeFinder(2).hasNonPrime(new long[]{7}));
    }

    @Test
    void hasNonPrime_nonPrimeAtFirstIndex_found() {
        long[] numbers = {4, 2, 3, 5, 7};
        assertTrue(new ThreadPrimeFinder(3).hasNonPrime(numbers));
    }

    @Test
    void hasNonPrime_nonPrimeAtLastIndex_found() {
        long[] numbers = {2, 3, 5, 7, 9};
        assertTrue(new ThreadPrimeFinder(3).hasNonPrime(numbers));
    }

    @Test
    void hasNonPrime_alwaysMatchesSequentialFinder() {
        long[][] arrays = {
                {6, 8, 7, 13, 5, 9, 4},
                {2, 3, 5, 7, 11},
                {1, 4, 6, 8},
                {20319251, 6997901, 6997927},
                {2}
        };
        SequentialPrimeFinder sequential = new SequentialPrimeFinder();
        for (long[] numbers : arrays) {
            boolean expected = sequential.hasNonPrime(numbers);
            assertEquals(expected, new ThreadPrimeFinder(4).hasNonPrime(numbers));
        }
    }
}
