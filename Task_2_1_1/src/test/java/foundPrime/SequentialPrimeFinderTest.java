package foundPrime;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SequentialPrimeFinderTest {

    private final SequentialPrimeFinder finder = new SequentialPrimeFinder();

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
    void hasNonPrime_mixedArray_returnsTrue() {
        long[] numbers = {6, 8, 7, 13, 5, 9, 4};
        assertTrue(finder.hasNonPrime(numbers));
    }

    @Test
    void hasNonPrime_allLargePrimes_returnsFalse() {
        long[] numbers = {
                20319251, 6997901, 6997927, 6997937, 17858849, 6997967,
                6998009, 6998029, 6998039, 20165149, 6998051, 6998053
        };
        assertFalse(finder.hasNonPrime(numbers));
    }

    @Test
    void isPrime_negativeAndZeroAndOne_returnsFalse() {
        assertFalse(SequentialPrimeFinder.isPrime(0));
        assertFalse(SequentialPrimeFinder.isPrime(1));
        assertFalse(SequentialPrimeFinder.isPrime(-7));
    }

    @Test
    void isPrime_twoAndThree_returnsTrue() {
        assertTrue(SequentialPrimeFinder.isPrime(2));
        assertTrue(SequentialPrimeFinder.isPrime(3));
    }

    @Test
    void isPrime_largePrime_returnsTrue() {
        assertTrue(SequentialPrimeFinder.isPrime(97));
        assertTrue(SequentialPrimeFinder.isPrime(1000000007L));
    }
}
