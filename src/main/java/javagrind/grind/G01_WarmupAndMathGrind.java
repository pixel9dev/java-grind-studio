package javagrind.grind;

import javagrind.util.TestRunner;

/**
 * ==========================================================================
 * GRIND 01: Java Warmup, Loops & Number Logic (4 Problems)
 * ==========================================================================
 * Prerequisite: Run & read (L01_BasicsTypesAndMemory.java:1) first!
 *
 * HOW TO GRIND:
 * 1. Replace `return TestRunner.todo();` inside a method with your code.
 * 2. Click the green ▶ button next to `main` (or press Shift+F10) to test!
 * 3. Stuck for >15 mins? Check (S01_WarmupAndMathSolutions.java:1).
 */
public class G01_WarmupAndMathGrind {

    /**
     * P1.1: Prime Number Check
     * Return true if `n` is a prime number, false otherwise.
     * Remember: Numbers <= 1 are NOT prime. 2 is the smallest prime.
     *
     * Examples:
     *   isPrime(2)  -> true
     *   isPrime(17) -> true
     *   isPrime(1)  -> false
     *   isPrime(49) -> false
     *
     * Target Complexity: O(sqrt(n)) time, O(1) space.
     */
    public static boolean isPrime(int n) {
        return TestRunner.todo();
    }

    /**
     * P1.2: Reverse Signed 32-Bit Integer (Overflow-Safe!)
     * Reverse the digits of `x`. Keep the negative sign if `x < 0`.
     * If reversing `x` causes it to go outside the 32-bit `int` range
     * [Integer.MIN_VALUE, Integer.MAX_VALUE], return 0!
     *
     * Examples:
     *   reverseInt(123)        -> 321
     *   reverseInt(-450)       -> -54
     *   reverseInt(1534236469) -> 0   (overflows 2,147,483,647!)
     */
    public static int reverseInt(int x) {
        return TestRunner.todo();
    }

    /**
     * P1.3: N-th Fibonacci Number (Iterative O(1) Space)
     * Given `n >= 0`, return F(n) where:
     *   F(0) = 0, F(1) = 1, and F(n) = F(n-1) + F(n-2) for n >= 2.
     *
     * Examples:
     *   fibonacci(0)  -> 0L
     *   fibonacci(1)  -> 1L
     *   fibonacci(10) -> 55L
     *   fibonacci(50) -> 12586269025L
     */
    public static long fibonacci(int n) {
        return TestRunner.todo();
    }

    /**
     * P1.4: Greatest Common Divisor (Euclidean Algorithm)
     * Return the non-negative GCD of integers `a` and `b`.
     * Note: Take `Math.abs` first; if one number is 0, GCD is the other!
     *
     * Examples:
     *   gcd(48, 18) -> 6
     *   gcd(7, 13)  -> 1
     *   gcd(0, 9)   -> 9
     *   gcd(-12, 8) -> 4
     */
    public static int gcd(int a, int b) {
        return TestRunner.todo();
    }

    // ==================== DO NOT EDIT BELOW THIS LINE (TEST HARNESS) ====================

    public static TestRunner.ModuleResult runTests(boolean verbose) {
        return new TestRunner.Suite("GRIND 01: Warmup & Number Logic")
                .problem("P1.1", "isPrime(int n)",
                        "Check n <= 1 -> false; loop i from 2 while (long) i * i <= n.",
                        v -> {
                            v.assertFalse("isPrime(-5)", isPrime(-5));
                            v.assertFalse("isPrime(0)", isPrime(0));
                            v.assertFalse("isPrime(1)", isPrime(1));
                            v.assertTrue("isPrime(2)", isPrime(2));
                            v.assertTrue("isPrime(3)", isPrime(3));
                            v.assertFalse("isPrime(4)", isPrime(4));
                            v.assertFalse("isPrime(49)", isPrime(49));
                            v.assertTrue("isPrime(97)", isPrime(97));
                            v.assertTrue("isPrime(1_000_000_007)", isPrime(1_000_000_007));
                        })
                .problem("P1.2", "reverseInt(int x)",
                        "Use a `long rev = 0;` accumulator (`rev = rev * 10 + x % 10`), then check Integer bounds!",
                        v -> {
                            v.assertEquals("reverseInt(123)", 321, reverseInt(123));
                            v.assertEquals("reverseInt(-450)", -54, reverseInt(-450));
                            v.assertEquals("reverseInt(0)", 0, reverseInt(0));
                            v.assertEquals("reverseInt(1000)", 1, reverseInt(1000));
                            v.assertEquals("reverseInt(1534236469)", 0, reverseInt(1534236469));
                            v.assertEquals("reverseInt(Integer.MIN_VALUE)", 0, reverseInt(Integer.MIN_VALUE));
                        })
                .problem("P1.3", "fibonacci(int n)",
                        "Track two `long` variables `prev = 0, curr = 1` and advance `n` steps.",
                        v -> {
                            v.assertEquals("fibonacci(0)", 0L, fibonacci(0));
                            v.assertEquals("fibonacci(1)", 1L, fibonacci(1));
                            v.assertEquals("fibonacci(2)", 1L, fibonacci(2));
                            v.assertEquals("fibonacci(10)", 55L, fibonacci(10));
                            v.assertEquals("fibonacci(50)", 12586269025L, fibonacci(50));
                        })
                .problem("P1.4", "gcd(int a, int b)",
                        "While b != 0: int temp = a % b; a = b; b = temp;",
                        v -> {
                            v.assertEquals("gcd(48, 18)", 6, gcd(48, 18));
                            v.assertEquals("gcd(7, 13)", 1, gcd(7, 13));
                            v.assertEquals("gcd(0, 9)", 9, gcd(0, 9));
                            v.assertEquals("gcd(9, 0)", 9, gcd(9, 0));
                            v.assertEquals("gcd(-12, 8)", 4, gcd(-12, 8));
                        })
                .run(verbose);
    }

    public static void main(String[] args) {
        runTests(true);
    }
}
