package foundPrime;

public class SequentialPrimeFinder implements PrimeFinder {

    @Override
    public boolean hasNonPrime(long[] numbers) {
        if (numbers == null || numbers.length == 0) {
            return false;
        }
        for (long n : numbers) {
            if (!PrimeFinder.isPrime(n)) {
                return true;
            }
        }
        return false;
    }
}
