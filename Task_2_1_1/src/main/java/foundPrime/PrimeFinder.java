package foundPrime;

public interface PrimeFinder {

    boolean hasNonPrime(long[] numbers);

    static boolean isPrime(long n) {
        if (n < 2) {
            return false;
        }
        if (n == 2) {
            return true;
        }
        if (n % 2 == 0) {
            return false;
        }
        long limit = (long) Math.sqrt(n);
        for (long d = 3; d <= limit; d += 2) {
            if (n % d == 0) {
                return false;
            }
        }
        return true;
    }
}
