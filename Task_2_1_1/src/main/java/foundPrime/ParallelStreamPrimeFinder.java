package foundPrime;

import java.util.Arrays;

public class ParallelStreamPrimeFinder implements PrimeFinder {

    @Override
    public boolean hasNonPrime(long[] numbers) {
        if (numbers == null || numbers.length == 0) {
            return false;
        }
        return Arrays.stream(numbers)
                .parallel()
                .anyMatch(n -> !SequentialPrimeFinder.isPrime(n));
    }
}
