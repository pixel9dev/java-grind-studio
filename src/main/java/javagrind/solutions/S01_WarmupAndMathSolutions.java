package javagrind.solutions;

import javagrind.util.TestRunner;

/**
 * REFERENCE SOLUTIONS FOR GRIND 01 (Warmup & Number Logic).
 * Try solving in G01_WarmupAndMathGrind.java first before reading these!
 */
public class S01_WarmupAndMathSolutions {

    // P1.1: O(sqrt(n)) Time | O(1) Space
    public static boolean isPrime(int n) {
        if (n <= 1) return false;
        if (n <= 3) return true;
        if (n % 2 == 0 || n % 3 == 0) return false;
        for (long i = 5; i * i <= n; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0) {
                return false;
            }
        }
        return true;
    }

    // P1.2: O(log10(x)) Time | O(1) Space
    public static int reverseInt(int x) {
        long rev = 0;
        while (x != 0) {
            rev = rev * 10 + (x % 10);
            if (rev > Integer.MAX_VALUE || rev < Integer.MIN_VALUE) {
                return 0;
            }
            x /= 10;
        }
        return (int) rev;
    }

    // P1.3: O(n) Time | O(1) Space
    public static long fibonacci(int n) {
        if (n <= 1) return n;
        long prev = 0;
        long curr = 1;
        for (int i = 2; i <= n; i++) {
            long next = prev + curr;
            prev = curr;
            curr = next;
        }
        return curr;
    }

    // P1.4: O(log(min(a, b))) Time | O(1) Space
    public static int gcd(int a, int b) {
        a = Math.abs(a);
        b = Math.abs(b);
        while (b != 0) {
            int rem = a % b;
            a = b;
            b = rem;
        }
        return a;
    }

    public static TestRunner.ModuleResult verify(boolean verbose) {
        return new TestRunner.Suite("SOLUTIONS 01: Warmup & Number Logic")
                .problem("P1.1", "isPrime", "", v -> {
                    v.assertFalse("isPrime(1)", isPrime(1));
                    v.assertTrue("isPrime(2)", isPrime(2));
                    v.assertFalse("isPrime(49)", isPrime(49));
                    v.assertTrue("isPrime(1_000_000_007)", isPrime(1_000_000_007));
                })
                .problem("P1.2", "reverseInt", "", v -> {
                    v.assertEquals("reverseInt(123)", 321, reverseInt(123));
                    v.assertEquals("reverseInt(-450)", -54, reverseInt(-450));
                    v.assertEquals("reverseInt(1534236469)", 0, reverseInt(1534236469));
                    v.assertEquals("reverseInt(Integer.MIN_VALUE)", 0, reverseInt(Integer.MIN_VALUE));
                })
                .problem("P1.3", "fibonacci", "", v -> {
                    v.assertEquals("fibonacci(0)", 0L, fibonacci(0));
                    v.assertEquals("fibonacci(10)", 55L, fibonacci(10));
                    v.assertEquals("fibonacci(50)", 12586269025L, fibonacci(50));
                })
                .problem("P1.4", "gcd", "", v -> {
                    v.assertEquals("gcd(48, 18)", 6, gcd(48, 18));
                    v.assertEquals("gcd(0, 9)", 9, gcd(0, 9));
                    v.assertEquals("gcd(-12, 8)", 4, gcd(-12, 8));
                })
                .run(verbose);
    }

    public static void main(String[] args) {
        verify(true);
    }
}
