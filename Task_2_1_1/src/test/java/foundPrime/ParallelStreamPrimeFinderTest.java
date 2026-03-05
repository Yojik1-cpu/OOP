package foundPrime;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParallelStreamPrimeFinderTest {

    private final ParallelStreamPrimeFinder finder = new ParallelStreamPrimeFinder();

    @Test
    void hasNonPrime_whenArrayContainsNonPrime_returnsTrue() {
        long[] numbers = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        assertTrue(finder.hasNonPrime(numbers));
    }

    @Test
    void hasNonPrime_emptyArray_returnsFalse() {
        assertFalse(finder.hasNonPrime(new long[]{}));
    }

    @Test
    void hasNonPrime_null_returnsFalse() {
        assertFalse(finder.hasNonPrime(null));
    }

    @Test
    void hasNonPrime_whenAllPrimes_returnsFalse() {
        long[] numbers = {2, 3, 5, 7, 11};
        assertFalse(finder.hasNonPrime(numbers));
    }

    @Test
    void hasNonPrime_sameResultAsSequential() {
        long[] numbers = {1, 4, 6, 2, 8, 9, 10};
        SequentialPrimeFinder sequential = new SequentialPrimeFinder();
        assertEquals(sequential.hasNonPrime(numbers), finder.hasNonPrime(numbers));
    }
}
