package javagrind.app;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * All 32 interactive Java coding problems with starter code, hints,
 * full reference solutions, and live multi-case test suites.
 */
public final class ProblemBank {

    public static final String ALL_PASSED_TOKEN = "[ALL_PASSED]\n";

    private ProblemBank() {}

    @FunctionalInterface
    public interface SuiteRunner {
        void run(Class<?> solClass, CaseRecorder rec) throws Exception;
    }

    public record CodingProblem(
            String id,
            String group,
            String title,
            String difficulty,
            String descriptionHtml,
            String hint,
            String starterCode,
            String solutionCode,
            SuiteRunner suite
    ) {}

    public static final class CaseRecorder {
        private final List<String> lines = new ArrayList<>();
        private int passed = 0;
        private int failed = 0;

        public void checkEquals(String label, Object expected, Object actual) {
            if (deepEq(expected, actual)) {
                passed++;
                lines.add("  [PASS] " + label + " -> " + fmt(actual));
            } else {
                failed++;
                lines.add("  [FAIL] " + label + "\n         Expected: " + fmt(expected)
                        + "\n         Actual  : " + fmt(actual));
            }
        }

        public void checkThrows(String label, Class<? extends Throwable> expectedType, ThrowingRunnable action) {
            try {
                action.run();
                failed++;
                lines.add("  [FAIL] " + label + " -> Expected " + expectedType.getSimpleName() + ", nothing thrown");
            } catch (InvocationTargetException ite) {
                Throwable cause = ite.getTargetException();
                if (expectedType.isInstance(cause)) {
                    passed++;
                    lines.add("  [PASS] " + label + " -> Threw " + cause.getClass().getSimpleName());
                } else {
                    failed++;
                    lines.add("  [FAIL] " + label + " -> Expected " + expectedType.getSimpleName()
                            + ", threw " + cause.getClass().getSimpleName());
                }
            } catch (Throwable t) {
                if (expectedType.isInstance(t)) {
                    passed++;
                    lines.add("  [PASS] " + label + " -> Threw " + t.getClass().getSimpleName());
                } else {
                    failed++;
                    lines.add("  [FAIL] " + label + " -> Expected " + expectedType.getSimpleName()
                            + ", threw " + t.getClass().getSimpleName());
                }
            }
        }

        public String formatReport() {
            int total = passed + failed;
            StringBuilder sb = new StringBuilder();
            if (failed == 0 && total > 0) {
                sb.append(ALL_PASSED_TOKEN);
                sb.append("ALL TEST CASES PASSED! (").append(passed).append("/").append(total).append(")\n");
            } else {
                sb.append("TEST SUMMARY: ").append(passed).append("/").append(total)
                        .append(" passed, ").append(failed).append(" failed.\n");
            }
            sb.append("------------------------------------------------------------\n");
            for (String line : lines) {
                sb.append(line).append('\n');
            }
            return sb.toString();
        }
    }

    @FunctionalInterface
    public interface ThrowingRunnable {
        void run() throws Throwable;
    }

    public static LiveJavaEngine.RunOutput evaluate(CodingProblem problem, String userSource) {
        return LiveJavaEngine.compileAndVerifySolution(userSource, solClass -> {
            CaseRecorder rec = new CaseRecorder();
            try {
                problem.suite().run(solClass, rec);
            } catch (InvocationTargetException ite) {
                Throwable cause = ite.getTargetException() != null ? ite.getTargetException() : ite;
                return "RUNTIME EXCEPTION DURING TESTS:\n"
                        + "  " + cause.getClass().getSimpleName() + ": " + cause.getMessage() + "\n\n"
                        + rec.formatReport();
            }
            return rec.formatReport();
        });
    }

    private static boolean deepEq(Object a, Object b) {
        if (a == b) return true;
        if (a == null || b == null) return false;
        if (a instanceof int[] ia && b instanceof int[] ib) return Arrays.equals(ia, ib);
        if (a instanceof long[] la && b instanceof long[] lb) return Arrays.equals(la, lb);
        if (a instanceof double[] da && b instanceof double[] db) {
            if (da.length != db.length) return false;
            for (int i = 0; i < da.length; i++) if (Math.abs(da[i] - db[i]) > 1e-6) return false;
            return true;
        }
        if (a instanceof Double d1 && b instanceof Double d2) return Math.abs(d1 - d2) < 1e-6;
        if (a instanceof Object[] oa && b instanceof Object[] ob) return Arrays.deepEquals(oa, ob);
        return Objects.equals(a, b);
    }

    private static String fmt(Object v) {
        if (v == null) return "null";
        if (v instanceof String s) return "\"" + s + "\"";
        if (v instanceof Character c) return "'" + c + "'";
        if (v instanceof int[] a) return Arrays.toString(a);
        if (v instanceof long[] a) return Arrays.toString(a);
        if (v instanceof Object[] a) return Arrays.deepToString(a);
        return String.valueOf(v);
    }

    private static List<List<String>> normGroups(List<List<String>> groups) {
        if (groups == null) return null;
        List<List<String>> copy = new ArrayList<>();
        for (List<String> g : groups) {
            List<String> inner = new ArrayList<>(g);
            Collections.sort(inner);
            copy.add(inner);
        }
        copy.sort(Comparator.comparing(l -> l.isEmpty() ? "" : l.get(0)));
        return copy;
    }

    public static final List<CodingProblem> PROBLEMS = List.of(
            // ==================== 01. WARMUP & MATH ====================
            new CodingProblem(
                    "P1.1", "01. Warmup & Math", "Prime Number Check", "Easy",
                    """
                    <h1>P1.1 — Prime Number Check</h1>
                    <p>Return <code>true</code> if integer <code>n</code> is a prime number, otherwise <code>false</code>.</p>
                    <ul>
                      <li>Numbers <code>&le; 1</code> are <b>not</b> prime.</li>
                      <li><code>2</code> is the smallest prime number.</li>
                    </ul>
                    <pre>isPrime(2)  -&gt; true
                    isPrime(17) -&gt; true
                    isPrime(1)  -&gt; false
                    isPrime(49) -&gt; false</pre>
                    <p><b>Target:</b> <code>O(&radic;n)</code> time, <code>O(1)</code> space.</p>
                    """,
                    "Check `if (n <= 1) return false;`. Then loop `for (long i = 2; i * i <= n; i++)` and test `n % i == 0`.",
                    """
                    public class Solution {
                        public static boolean isPrime(int n) {
                            // TODO: Write your logic here
                            return false;
                        }
                    }
                    """,
                    """
                    public class Solution {
                        public static boolean isPrime(int n) {
                            if (n <= 1) return false;
                            for (long i = 2; i * i <= n; i++) {
                                if (n % i == 0) return false;
                            }
                            return true;
                        }
                    }
                    """,
                    (cls, rec) -> {
                        Method m = cls.getMethod("isPrime", int.class);
                        rec.checkEquals("isPrime(-3)", false, m.invoke(null, -3));
                        rec.checkEquals("isPrime(1)", false, m.invoke(null, 1));
                        rec.checkEquals("isPrime(2)", true, m.invoke(null, 2));
                        rec.checkEquals("isPrime(49)", false, m.invoke(null, 49));
                        rec.checkEquals("isPrime(97)", true, m.invoke(null, 97));
                        rec.checkEquals("isPrime(1000000007)", true, m.invoke(null, 1_000_000_007));
                    }
            ),
            new CodingProblem(
                    "P1.2", "01. Warmup & Math", "Overflow-Safe Reverse Integer", "Easy",
                    """
                    <h1>P1.2 — Reverse Signed 32-Bit Integer</h1>
                    <p>Reverse the digits of signed integer <code>x</code>. Keep the negative sign if <code>x &lt; 0</code>.</p>
                    <p>If reversing causes overflow outside <code>[Integer.MIN_VALUE, Integer.MAX_VALUE]</code>, return <code>0</code>!</p>
                    <pre>reverseInt(123)        -&gt; 321
                    reverseInt(-450)       -&gt; -54
                    reverseInt(1534236469) -&gt; 0 (overflows 32-bit int!)</pre>
                    """,
                    "Accumulate in `long rev = 0;`: `rev = rev * 10 + (x % 10);` and check if `rev > Integer.MAX_VALUE || rev < Integer.MIN_VALUE`.",
                    """
                    public class Solution {
                        public static int reverseInt(int x) {
                            // TODO: Reverse digits safely without overflowing int
                            return 0;
                        }
                    }
                    """,
                    """
                    public class Solution {
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
                    }
                    """,
                    (cls, rec) -> {
                        Method m = cls.getMethod("reverseInt", int.class);
                        rec.checkEquals("reverseInt(123)", 321, m.invoke(null, 123));
                        rec.checkEquals("reverseInt(-450)", -54, m.invoke(null, -450));
                        rec.checkEquals("reverseInt(0)", 0, m.invoke(null, 0));
                        rec.checkEquals("reverseInt(1534236469)", 0, m.invoke(null, 1534236469));
                        rec.checkEquals("reverseInt(Integer.MIN_VALUE)", 0, m.invoke(null, Integer.MIN_VALUE));
                    }
            ),
            new CodingProblem(
                    "P1.3", "01. Warmup & Math", "N-th Fibonacci (Iterative)", "Easy",
                    """
                    <h1>P1.3 — N-th Fibonacci Number</h1>
                    <p>Given <code>n &ge; 0</code>, return <code>F(n)</code> as a <code>long</code> where <code>F(0)=0, F(1)=1</code>, and <code>F(n)=F(n-1)+F(n-2)</code>.</p>
                    <pre>fibonacci(0)  -&gt; 0L
                    fibonacci(10) -&gt; 55L
                    fibonacci(50) -&gt; 12586269025L</pre>
                    <div class='trap'>Plain recursion <code>fib(n-1)+fib(n-2)</code> times out at <code>n=50</code>! Use a simple loop in <code>O(n)</code> time, <code>O(1)</code> space.</div>
                    """,
                    "Keep `long prev = 0, curr = 1;` and iterate from `i = 2` to `n`.",
                    """
                    public class Solution {
                        public static long fibonacci(int n) {
                            // TODO: Compute F(n) iteratively
                            return 0L;
                        }
                    }
                    """,
                    """
                    public class Solution {
                        public static long fibonacci(int n) {
                            if (n <= 1) return n;
                            long prev = 0, curr = 1;
                            for (int i = 2; i <= n; i++) {
                                long next = prev + curr;
                                prev = curr;
                                curr = next;
                            }
                            return curr;
                        }
                    }
                    """,
                    (cls, rec) -> {
                        Method m = cls.getMethod("fibonacci", int.class);
                        rec.checkEquals("fibonacci(0)", 0L, m.invoke(null, 0));
                        rec.checkEquals("fibonacci(1)", 1L, m.invoke(null, 1));
                        rec.checkEquals("fibonacci(10)", 55L, m.invoke(null, 10));
                        rec.checkEquals("fibonacci(50)", 12586269025L, m.invoke(null, 50));
                    }
            ),
            new CodingProblem(
                    "P1.4", "01. Warmup & Math", "Greatest Common Divisor (GCD)", "Easy",
                    """
                    <h1>P1.4 — Greatest Common Divisor (Euclidean Algorithm)</h1>
                    <p>Return the non-negative GCD of integers <code>a</code> and <code>b</code>.</p>
                    <pre>gcd(48, 18) -&gt; 6
                    gcd(0, 9)   -&gt; 9
                    gcd(-12, 8) -&gt; 4</pre>
                    """,
                    "Take `Math.abs` first, then loop `while (b != 0) { int r = a % b; a = b; b = r; } return a;`.",
                    """
                    public class Solution {
                        public static int gcd(int a, int b) {
                            // TODO: Euclidean GCD
                            return 0;
                        }
                    }
                    """,
                    """
                    public class Solution {
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
                    }
                    """,
                    (cls, rec) -> {
                        Method m = cls.getMethod("gcd", int.class, int.class);
                        rec.checkEquals("gcd(48, 18)", 6, m.invoke(null, 48, 18));
                        rec.checkEquals("gcd(7, 13)", 1, m.invoke(null, 7, 13));
                        rec.checkEquals("gcd(0, 9)", 9, m.invoke(null, 0, 9));
                        rec.checkEquals("gcd(-12, 8)", 4, m.invoke(null, -12, 8));
                    }
            ),

            // ==================== 02. STRINGS ====================
            new CodingProblem(
                    "P2.1", "02. Strings", "Valid Alphanumeric Palindrome", "Easy",
                    """
                    <h1>P2.1 — Valid Alphanumeric Palindrome</h1>
                    <p>Return <code>true</code> if <code>s</code> reads the same forward and backward after ignoring case and skipping non-alphanumeric characters (<code>!Character.isLetterOrDigit(ch)</code>).</p>
                    <pre>isValidPalindrome("A man, a plan, a canal: Panama") -&gt; true
                    isValidPalindrome("race a car")                     -&gt; false</pre>
                    """,
                    "Use two pointers `left = 0, right = s.length() - 1`, advancing past non-alphanumeric characters.",
                    """
                    public class Solution {
                        public static boolean isValidPalindrome(String s) {
                            // TODO: Two-pointer alphanumeric palindrome check
                            return false;
                        }
                    }
                    """,
                    """
                    public class Solution {
                        public static boolean isValidPalindrome(String s) {
                            if (s == null) return false;
                            int left = 0, right = s.length() - 1;
                            while (left < right) {
                                while (left < right && !Character.isLetterOrDigit(s.charAt(left))) left++;
                                while (left < right && !Character.isLetterOrDigit(s.charAt(right))) right--;
                                if (Character.toLowerCase(s.charAt(left)) != Character.toLowerCase(s.charAt(right))) {
                                    return false;
                                }
                                left++;
                                right--;
                            }
                            return true;
                        }
                    }
                    """,
                    (cls, rec) -> {
                        Method m = cls.getMethod("isValidPalindrome", String.class);
                        rec.checkEquals("Panama", true, m.invoke(null, "A man, a plan, a canal: Panama"));
                        rec.checkEquals("race a car", false, m.invoke(null, "race a car"));
                        rec.checkEquals("blank", true, m.invoke(null, "   "));
                        rec.checkEquals("0P", false, m.invoke(null, "0P"));
                    }
            ),
            new CodingProblem(
                    "P2.2", "02. Strings", "Valid Anagram", "Easy",
                    """
                    <h1>P2.2 — Valid Anagram (Lowercase ASCII)</h1>
                    <p>Return <code>true</code> if <code>t</code> is an exact character rearrangement of <code>s</code> (lowercase <code>'a'-'z'</code>). Return <code>false</code> if either is <code>null</code>.</p>
                    <pre>isAnagram("listen", "silent") -&gt; true
                    isAnagram("rat", "car")       -&gt; false</pre>
                    """,
                    "Check lengths match, then use `int[] freq = new int[26]` incrementing for `s` and decrementing for `t`.",
                    """
                    public class Solution {
                        public static boolean isAnagram(String s, String t) {
                            // TODO: Check character frequencies
                            return false;
                        }
                    }
                    """,
                    """
                    public class Solution {
                        public static boolean isAnagram(String s, String t) {
                            if (s == null || t == null || s.length() != t.length()) return false;
                            int[] freq = new int[26];
                            for (int i = 0; i < s.length(); i++) {
                                freq[s.charAt(i) - 'a']++;
                                freq[t.charAt(i) - 'a']--;
                            }
                            for (int c : freq) {
                                if (c != 0) return false;
                            }
                            return true;
                        }
                    }
                    """,
                    (cls, rec) -> {
                        Method m = cls.getMethod("isAnagram", String.class, String.class);
                        rec.checkEquals("listen/silent", true, m.invoke(null, "listen", "silent"));
                        rec.checkEquals("rat/car", false, m.invoke(null, "rat", "car"));
                        rec.checkEquals("ab/a", false, m.invoke(null, "ab", "a"));
                        rec.checkEquals("null check", false, m.invoke(null, null, "abc"));
                    }
            ),
            new CodingProblem(
                    "P2.3", "02. Strings", "First Non-Repeating Character", "Easy",
                    """
                    <h1>P2.3 — First Non-Repeating Character (#1 Walk-In Classic!)</h1>
                    <p>Return the <b>first</b> character in <code>s</code> that appears with frequency <code>1</code>. If none exists (or <code>s</code> is empty), return <code>'_'</code>.</p>
                    <pre>firstNonRepeatingChar("swiss")     -&gt; 'w'
                    firstNonRepeatingChar("developer") -&gt; 'd'
                    firstNonRepeatingChar("aabb")      -&gt; '_'_</pre>
                    """,
                    "Pass 1: count chars in `int[] counts = new int[256]`. Pass 2: iterate `i = 0..s.length()-1` and return first char with `counts[ch] == 1`.",
                    """
                    public class Solution {
                        public static char firstNonRepeatingChar(String s) {
                            // TODO: Return first unique char, or '_' if none
                            return '_';
                        }
                    }
                    """,
                    """
                    public class Solution {
                        public static char firstNonRepeatingChar(String s) {
                            if (s == null || s.isEmpty()) return '_';
                            int[] counts = new int[256];
                            for (int i = 0; i < s.length(); i++) counts[s.charAt(i)]++;
                            for (int i = 0; i < s.length(); i++) {
                                if (counts[s.charAt(i)] == 1) return s.charAt(i);
                            }
                            return '_';
                        }
                    }
                    """,
                    (cls, rec) -> {
                        Method m = cls.getMethod("firstNonRepeatingChar", String.class);
                        rec.checkEquals("swiss", 'w', m.invoke(null, "swiss"));
                        rec.checkEquals("developer", 'd', m.invoke(null, "developer"));
                        rec.checkEquals("stress", 't', m.invoke(null, "stress"));
                        rec.checkEquals("aabb", '_', m.invoke(null, "aabb"));
                    }
            ),
            new CodingProblem(
                    "P2.4", "02. Strings", "Reverse Words in Sentence", "Medium",
                    """
                    <h1>P2.4 — Reverse Words in a Sentence</h1>
                    <p>Trim leading/trailing spaces, collapse multiple spaces into a single space, and reverse the order of words. Return <code>""</code> if blank.</p>
                    <pre>reverseWords("  the sky   is blue ") -&gt; "blue is sky the"
                    reverseWords("   ")                  -&gt; ""</pre>
                    """,
                    "`String t = sentence.trim();` if empty return `\"\"`. Use `t.split(\"\\\\s+\")` and append words from last to first with `StringBuilder`.",
                    """
                    public class Solution {
                        public static String reverseWords(String sentence) {
                            // TODO: Reverse word order cleanly
                            return "";
                        }
                    }
                    """,
                    """
                    public class Solution {
                        public static String reverseWords(String sentence) {
                            if (sentence == null) return "";
                            String trimmed = sentence.trim();
                            if (trimmed.isEmpty()) return "";
                            String[] words = trimmed.split("\\\\s+");
                            StringBuilder sb = new StringBuilder();
                            for (int i = words.length - 1; i >= 0; i--) {
                                sb.append(words[i]);
                                if (i > 0) sb.append(' ');
                            }
                            return sb.toString();
                        }
                    }
                    """,
                    (cls, rec) -> {
                        Method m = cls.getMethod("reverseWords", String.class);
                        rec.checkEquals("the sky is blue", "blue is sky the", m.invoke(null, "  the sky   is blue "));
                        rec.checkEquals("hello", "hello", m.invoke(null, "  hello "));
                        rec.checkEquals("blank", "", m.invoke(null, "    "));
                    }
            ),
            new CodingProblem(
                    "P2.5", "02. Strings", "Run-Length String Compression", "Medium",
                    """
                    <h1>P2.5 — Basic String Compression</h1>
                    <p>Compress consecutive identical characters as <code>&lt;char&gt;&lt;count&gt;</code>. <b>Rule:</b> If the compressed result is NOT strictly shorter than <code>s</code>, return original <code>s</code>!</p>
                    <pre>compressString("aabcccccaaa") -&gt; "a2b1c5a3"
                    compressString("abc")         -&gt; "abc" ("a1b1c1" is longer!)</pre>
                    """,
                    "Walk string counting consecutive streaks, append to `StringBuilder`, and return `sb.length() < s.length() ? sb.toString() : s`.",
                    """
                    public class Solution {
                        public static String compressString(String s) {
                            // TODO: Compress streaks; return original if not shorter
                            return s;
                        }
                    }
                    """,
                    """
                    public class Solution {
                        public static String compressString(String s) {
                            if (s == null || s.length() <= 2) return s == null ? "" : s;
                            StringBuilder sb = new StringBuilder();
                            int count = 1;
                            for (int i = 1; i <= s.length(); i++) {
                                if (i < s.length() && s.charAt(i) == s.charAt(i - 1)) {
                                    count++;
                                } else {
                                    sb.append(s.charAt(i - 1)).append(count);
                                    count = 1;
                                }
                            }
                            return sb.length() < s.length() ? sb.toString() : s;
                        }
                    }
                    """,
                    (cls, rec) -> {
                        Method m = cls.getMethod("compressString", String.class);
                        rec.checkEquals("aabcccccaaa", "a2b1c5a3", m.invoke(null, "aabcccccaaa"));
                        rec.checkEquals("abc", "abc", m.invoke(null, "abc"));
                        rec.checkEquals("aabb", "aabb", m.invoke(null, "aabb"));
                        rec.checkEquals("aaaa", "a4", m.invoke(null, "aaaa"));
                    }
            ),

            // ==================== 03. ARRAYS & TWO POINTERS ====================
            new CodingProblem(
                    "P3.1", "03. Arrays & Two Pointers", "Second Largest Distinct", "Easy",
                    """
                    <h1>P3.1 — Second Largest Distinct Element (Single Pass)</h1>
                    <p>Return the 2nd largest <b>distinct</b> value in <code>nums</code> in <code>O(n)</code> without sorting! If fewer than 2 distinct values exist, return <code>Integer.MIN_VALUE</code>.</p>
                    <pre>secondLargestDistinct([10, 20, 20, 8, 15]) -&gt; 15
                    secondLargestDistinct([5, 5, 5])           -&gt; Integer.MIN_VALUE</pre>
                    """,
                    "Use `long first = Long.MIN_VALUE, second = Long.MIN_VALUE;` and skip `num == first`!",
                    """
                    public class Solution {
                        public static int secondLargestDistinct(int[] nums) {
                            // TODO: Find 2nd largest distinct value in one pass
                            return Integer.MIN_VALUE;
                        }
                    }
                    """,
                    """
                    public class Solution {
                        public static int secondLargestDistinct(int[] nums) {
                            if (nums == null || nums.length < 2) return Integer.MIN_VALUE;
                            long first = Long.MIN_VALUE, second = Long.MIN_VALUE;
                            for (int n : nums) {
                                if (n > first) {
                                    second = first;
                                    first = n;
                                } else if (n < first && n > second) {
                                    second = n;
                                }
                            }
                            return second == Long.MIN_VALUE ? Integer.MIN_VALUE : (int) second;
                        }
                    }
                    """,
                    (cls, rec) -> {
                        Method m = cls.getMethod("secondLargestDistinct", int[].class);
                        rec.checkEquals("[10,20,20,8,15]", 15, m.invoke(null, (Object) new int[]{10, 20, 20, 8, 15}));
                        rec.checkEquals("[5,5,5]", Integer.MIN_VALUE, m.invoke(null, (Object) new int[]{5, 5, 5}));
                        rec.checkEquals("[-10,-3,-20]", -10, m.invoke(null, (Object) new int[]{-10, -3, -20}));
                        rec.checkEquals("[5, MIN_VALUE]", Integer.MIN_VALUE,
                                m.invoke(null, (Object) new int[]{5, Integer.MIN_VALUE}));
                    }
            ),
            new CodingProblem(
                    "P3.2", "03. Arrays & Two Pointers", "Move Zeroes In-Place", "Easy",
                    """
                    <h1>P3.2 — Move Zeroes to End (In-Place)</h1>
                    <p>Move all <code>0</code>s to the end of <code>nums</code> in-place while preserving the relative order of non-zero elements.</p>
                    <pre>[0, 1, 0, 3, 12] -&gt; [1, 3, 12, 0, 0]</pre>
                    """,
                    "Maintain `int write = 0;`. Copy every non-zero to `nums[write++]`, then fill remaining indices with `0`.",
                    """
                    public class Solution {
                        public static void moveZeroes(int[] nums) {
                            // TODO: Modify nums in-place
                        }
                    }
                    """,
                    """
                    public class Solution {
                        public static void moveZeroes(int[] nums) {
                            if (nums == null) return;
                            int write = 0;
                            for (int n : nums) {
                                if (n != 0) nums[write++] = n;
                            }
                            while (write < nums.length) nums[write++] = 0;
                        }
                    }
                    """,
                    (cls, rec) -> {
                        Method m = cls.getMethod("moveZeroes", int[].class);
                        int[] a1 = {0, 1, 0, 3, 12};
                        m.invoke(null, (Object) a1);
                        rec.checkEquals("[0,1,0,3,12]", new int[]{1, 3, 12, 0, 0}, a1);

                        int[] a2 = {0, 0, 1};
                        m.invoke(null, (Object) a2);
                        rec.checkEquals("[0,0,1]", new int[]{1, 0, 0}, a2);
                    }
            ),
            new CodingProblem(
                    "P3.3", "03. Arrays & Two Pointers", "Rotate Array Right by K", "Medium",
                    """
                    <h1>P3.3 — Rotate Array Right by K Steps</h1>
                    <p>Rotate <code>nums</code> right by <code>k</code> steps in-place (<code>O(1)</code> extra space). Watch out for <code>k &ge; nums.length</code>!</p>
                    <pre>[1,2,3,4,5,6,7], k=3 -&gt; [5,6,7,1,2,3,4]</pre>
                    """,
                    "`k %= n;` Reverse `[0..n-1]`, reverse `[0..k-1]`, reverse `[k..n-1]`!",
                    """
                    public class Solution {
                        public static void rotateRight(int[] nums, int k) {
                            // TODO: Rotate right by k in-place
                        }
                    }
                    """,
                    """
                    public class Solution {
                        public static void rotateRight(int[] nums, int k) {
                            if (nums == null || nums.length <= 1) return;
                            int n = nums.length;
                            k %= n;
                            reverse(nums, 0, n - 1);
                            reverse(nums, 0, k - 1);
                            reverse(nums, k, n - 1);
                        }

                        private static void reverse(int[] a, int l, int r) {
                            while (l < r) {
                                int tmp = a[l];
                                a[l++] = a[r];
                                a[r--] = tmp;
                            }
                        }
                    }
                    """,
                    (cls, rec) -> {
                        Method m = cls.getMethod("rotateRight", int[].class, int.class);
                        int[] a1 = {1, 2, 3, 4, 5, 6, 7};
                        m.invoke(null, a1, 3);
                        rec.checkEquals("[1..7], k=3", new int[]{5, 6, 7, 1, 2, 3, 4}, a1);

                        int[] a2 = {1, 2};
                        m.invoke(null, a2, 5);
                        rec.checkEquals("[1,2], k=5", new int[]{2, 1}, a2);
                    }
            ),
            new CodingProblem(
                    "P3.4", "03. Arrays & Two Pointers", "Max Subarray Sum (Kadane's)", "Medium",
                    """
                    <h1>P3.4 — Maximum Subarray Sum (Kadane's Algorithm)</h1>
                    <p>Return the maximum sum of any contiguous <b>non-empty</b> subarray. Works even when all elements are negative!</p>
                    <pre>maxSubarraySum([-2,1,-3,4,-1,2,1,-5,4]) -&gt; 6 ([4,-1,2,1])
                    maxSubarraySum([-5,-2,-9])              -&gt; -2</pre>
                    """,
                    "Initialize `curr = nums[0], best = nums[0]`. Loop from `i = 1`: `curr = Math.max(nums[i], curr + nums[i]); best = Math.max(best, curr);`.",
                    """
                    public class Solution {
                        public static int maxSubarraySum(int[] nums) {
                            // TODO: Implement Kadane's algorithm
                            return 0;
                        }
                    }
                    """,
                    """
                    public class Solution {
                        public static int maxSubarraySum(int[] nums) {
                            int curr = nums[0], best = nums[0];
                            for (int i = 1; i < nums.length; i++) {
                                curr = Math.max(nums[i], curr + nums[i]);
                                best = Math.max(best, curr);
                            }
                            return best;
                        }
                    }
                    """,
                    (cls, rec) -> {
                        Method m = cls.getMethod("maxSubarraySum", int[].class);
                        rec.checkEquals("[-2,1,-3,4,-1,2,1,-5,4]", 6,
                                m.invoke(null, (Object) new int[]{-2, 1, -3, 4, -1, 2, 1, -5, 4}));
                        rec.checkEquals("[-5,-2,-9]", -2, m.invoke(null, (Object) new int[]{-5, -2, -9}));
                        rec.checkEquals("[5,4,-1,7,8]", 23, m.invoke(null, (Object) new int[]{5, 4, -1, 7, 8}));
                    }
            ),
            new CodingProblem(
                    "P3.5", "03. Arrays & Two Pointers", "Missing Number in [0..n]", "Easy",
                    """
                    <h1>P3.5 — Missing Number in Range [0..n]</h1>
                    <p>Given <code>n</code> distinct numbers from <code>[0, n]</code>, return the single missing integer in <code>O(n)</code> time, <code>O(1)</code> space.</p>
                    <pre>missingNumber([3, 0, 1]) -&gt; 2</pre>
                    """,
                    "Expected sum `(long) n * (n + 1) / 2` minus actual array sum (or XOR `0..n` with `nums[i]`).",
                    """
                    public class Solution {
                        public static int missingNumber(int[] nums) {
                            // TODO: Find the missing number in 0..n
                            return -1;
                        }
                    }
                    """,
                    """
                    public class Solution {
                        public static int missingNumber(int[] nums) {
                            int xor = nums.length;
                            for (int i = 0; i < nums.length; i++) {
                                xor ^= i ^ nums[i];
                            }
                            return xor;
                        }
                    }
                    """,
                    (cls, rec) -> {
                        Method m = cls.getMethod("missingNumber", int[].class);
                        rec.checkEquals("[3,0,1]", 2, m.invoke(null, (Object) new int[]{3, 0, 1}));
                        rec.checkEquals("[0,1]", 2, m.invoke(null, (Object) new int[]{0, 1}));
                        rec.checkEquals("[9,6,4,2,3,5,7,0,1]", 8,
                                m.invoke(null, (Object) new int[]{9, 6, 4, 2, 3, 5, 7, 0, 1}));
                    }
            ),

            // ==================== 04. OOP DESIGN ====================
            new CodingProblem(
                    "P4.1", "04. OOP Design", "BankAccount Encapsulation", "Easy",
                    """
                    <h1>P4.1 — Encapsulation &amp; Validation: BankAccount</h1>
                    <p>Complete <code>deposit(double amount)</code> and <code>withdraw(double amount)</code>:</p>
                    <ul>
                      <li>If <code>amount &le; 0</code>, throw <code>new IllegalArgumentException(...)</code>.</li>
                      <li>In <code>withdraw</code>, if <code>amount &gt; balance</code>, throw <code>new IllegalStateException(...)</code>.</li>
                      <li>Otherwise update <code>balance</code>!</li>
                    </ul>
                    """,
                    "Check `amount <= 0` first in both methods; check `amount > balance` before subtracting in `withdraw`.",
                    """
                    public class Solution {
                        public static class BankAccount {
                            private double balance;

                            public BankAccount(double initialBalance) {
                                this.balance = initialBalance;
                            }

                            public double getBalance() { return balance; }

                            public void deposit(double amount) {
                                // TODO: Validate amount > 0 and add to balance
                            }

                            public void withdraw(double amount) {
                                // TODO: Validate amount > 0, check balance, subtract
                            }
                        }
                    }
                    """,
                    """
                    public class Solution {
                        public static class BankAccount {
                            private double balance;

                            public BankAccount(double initialBalance) {
                                this.balance = initialBalance;
                            }

                            public double getBalance() { return balance; }

                            public void deposit(double amount) {
                                if (amount <= 0) throw new IllegalArgumentException("Positive required");
                                balance += amount;
                            }

                            public void withdraw(double amount) {
                                if (amount <= 0) throw new IllegalArgumentException("Positive required");
                                if (amount > balance) throw new IllegalStateException("Insufficient funds");
                                balance -= amount;
                            }
                        }
                    }
                    """,
                    (cls, rec) -> {
                        Class<?> accCls = Class.forName("Solution$BankAccount", true, cls.getClassLoader());
                        Constructor<?> ctor = accCls.getConstructor(double.class);
                        Method dep = accCls.getMethod("deposit", double.class);
                        Method wit = accCls.getMethod("withdraw", double.class);
                        Method bal = accCls.getMethod("getBalance");

                        Object acc = ctor.newInstance(1000.0);
                        dep.invoke(acc, 500.0);
                        rec.checkEquals("after deposit(500)", 1500.0, bal.invoke(acc));
                        wit.invoke(acc, 300.0);
                        rec.checkEquals("after withdraw(300)", 1200.0, bal.invoke(acc));
                        rec.checkThrows("deposit(-5)", IllegalArgumentException.class, () -> dep.invoke(acc, -5.0));
                        rec.checkThrows("withdraw(9999)", IllegalStateException.class, () -> wit.invoke(acc, 9999.0));
                    }
            ),
            new CodingProblem(
                    "P4.2", "04. OOP Design", "Polymorphic Payroll System", "Easy",
                    """
                    <h1>P4.2 — Runtime Polymorphism: Payable Payroll</h1>
                    <ul>
                      <li><code>FullTimeEmployee.monthlyPay()</code> &rarr; <code>baseSalary + bonus</code></li>
                      <li><code>Freelancer.monthlyPay()</code> &rarr; up to <code>160</code> hrs at <code>hourlyRate</code>, plus <code>1.5 * hourlyRate</code> for hours strictly above <code>160</code>!</li>
                      <li><code>totalPayroll(List&lt;Payable&gt; staff)</code> &rarr; sum polymorphic <code>monthlyPay()</code>!</li>
                    </ul>
                    """,
                    "For Freelancer: `int reg = Math.min(hours, 160); int ot = Math.max(0, hours - 160); return reg * rate + ot * rate * 1.5;`.",
                    """
                    import java.util.List;

                    public class Solution {
                        public interface Payable {
                            double monthlyPay();
                        }

                        public static class FullTimeEmployee implements Payable {
                            private final double baseSalary, bonus;
                            public FullTimeEmployee(double baseSalary, double bonus) {
                                this.baseSalary = baseSalary;
                                this.bonus = bonus;
                            }
                            @Override public double monthlyPay() {
                                // TODO
                                return 0.0;
                            }
                        }

                        public static class Freelancer implements Payable {
                            private final double rate;
                            private final int hours;
                            public Freelancer(double rate, int hours) {
                                this.rate = rate;
                                this.hours = hours;
                            }
                            @Override public double monthlyPay() {
                                // TODO: 1.5x overtime above 160 hours!
                                return 0.0;
                            }
                        }

                        public static double totalPayroll(List<Payable> staff) {
                            // TODO: Sum monthlyPay() across staff
                            return 0.0;
                        }
                    }
                    """,
                    """
                    import java.util.List;

                    public class Solution {
                        public interface Payable {
                            double monthlyPay();
                        }

                        public static class FullTimeEmployee implements Payable {
                            private final double baseSalary, bonus;
                            public FullTimeEmployee(double baseSalary, double bonus) {
                                this.baseSalary = baseSalary;
                                this.bonus = bonus;
                            }
                            @Override public double monthlyPay() {
                                return baseSalary + bonus;
                            }
                        }

                        public static class Freelancer implements Payable {
                            private final double rate;
                            private final int hours;
                            public Freelancer(double rate, int hours) {
                                this.rate = rate;
                                this.hours = hours;
                            }
                            @Override public double monthlyPay() {
                                int reg = Math.min(hours, 160);
                                int ot = Math.max(0, hours - 160);
                                return reg * rate + ot * rate * 1.5;
                            }
                        }

                        public static double totalPayroll(List<Payable> staff) {
                            double sum = 0.0;
                            for (Payable p : staff) sum += p.monthlyPay();
                            return sum;
                        }
                    }
                    """,
                    (cls, rec) -> {
                        ClassLoader cl = cls.getClassLoader();
                        Class<?> ftCls = Class.forName("Solution$FullTimeEmployee", true, cl);
                        Class<?> flCls = Class.forName("Solution$Freelancer", true, cl);
                        Object ft = ftCls.getConstructor(double.class, double.class).newInstance(50_000.0, 10_000.0);
                        Object fl1 = flCls.getConstructor(double.class, int.class).newInstance(100.0, 120);
                        Object fl2 = flCls.getConstructor(double.class, int.class).newInstance(100.0, 170);

                        Method total = cls.getMethod("totalPayroll", List.class);
                        rec.checkEquals("FullTime(50k,10k)", 60_000.0, ftCls.getMethod("monthlyPay").invoke(ft));
                        rec.checkEquals("Freelancer(100,170h)", 17_500.0, flCls.getMethod("monthlyPay").invoke(fl2));
                        rec.checkEquals("totalPayroll", 89_500.0, total.invoke(null, List.of(ft, fl1, fl2)));
                    }
            ),
            new CodingProblem(
                    "P4.3", "04. OOP Design", "equals() & hashCode() Contract", "Medium",
                    """
                    <h1>P4.3 — StudentKey equals() &amp; hashCode() for HashSet</h1>
                    <p>Two <code>StudentKey</code> objects represent the same student if <code>rollNo</code> matches AND <code>email</code> matches <b>case-insensitively</b> (<code>equalsIgnoreCase</code>)!</p>
                    <p>Implement both <code>equals(Object o)</code> and <code>hashCode()</code> so duplicate keys deduplicate properly inside a <code>HashSet</code>.</p>
                    """,
                    "In `hashCode()`, lowercase the email (`Objects.hash(rollNo, email.toLowerCase())`) so equal objects produce identical hashes!",
                    """
                    import java.util.Objects;

                    public class Solution {
                        public static class StudentKey {
                            private final int rollNo;
                            private final String email;

                            public StudentKey(int rollNo, String email) {
                                this.rollNo = rollNo;
                                this.email = email == null ? "" : email;
                            }

                            @Override
                            public boolean equals(Object other) {
                                // TODO: Match rollNo and case-insensitive email
                                return this == other;
                            }

                            @Override
                            public int hashCode() {
                                // TODO: Must be consistent with equals!
                                return 0;
                            }
                        }
                    }
                    """,
                    """
                    import java.util.Locale;
                    import java.util.Objects;

                    public class Solution {
                        public static class StudentKey {
                            private final int rollNo;
                            private final String email;

                            public StudentKey(int rollNo, String email) {
                                this.rollNo = rollNo;
                                this.email = email == null ? "" : email;
                            }

                            @Override
                            public boolean equals(Object other) {
                                if (this == other) return true;
                                if (!(other instanceof StudentKey that)) return false;
                                return this.rollNo == that.rollNo && this.email.equalsIgnoreCase(that.email);
                            }

                            @Override
                            public int hashCode() {
                                return Objects.hash(rollNo, email.toLowerCase(Locale.ROOT));
                            }
                        }
                    }
                    """,
                    (cls, rec) -> {
                        Class<?> keyCls = Class.forName("Solution$StudentKey", true, cls.getClassLoader());
                        Constructor<?> ctor = keyCls.getConstructor(int.class, String.class);
                        Object k1 = ctor.newInstance(101, "Dev@Java.org");
                        Object k2 = ctor.newInstance(101, "dev@java.org");
                        Object k3 = ctor.newInstance(102, "dev@java.org");

                        rec.checkEquals("k1.equals(k2)", true, k1.equals(k2));
                        rec.checkEquals("k1.equals(k3)", false, k1.equals(k3));
                        rec.checkEquals("hashCode consistency", k1.hashCode(), k2.hashCode());

                        Set<Object> set = new HashSet<>();
                        set.add(k1);
                        set.add(k2);
                        set.add(k3);
                        rec.checkEquals("HashSet deduplicated size", 2, set.size());
                    }
            ),

            // ==================== 05. COLLECTIONS & HASHING ====================
            new CodingProblem(
                    "P5.1", "05. Collections & Hashing", "Two Sum (O(n) HashMap)", "Easy",
                    """
                    <h1>P5.1 — Two Sum (O(n) HashMap)</h1>
                    <p>Return <code>new int[]{i, j}</code> (<code>i &lt; j</code>) such that <code>nums[i] + nums[j] == target</code>. If none exists, return <code>new int[]{-1, -1}</code>.</p>
                    <pre>twoSum([2, 7, 11, 15], 9) -&gt; [0, 1]</pre>
                    """,
                    "Check `map.containsKey(target - nums[i])` BEFORE `map.put(nums[i], i)`!",
                    """
                    import java.util.*;

                    public class Solution {
                        public static int[] twoSum(int[] nums, int target) {
                            // TODO: O(n) lookup with HashMap
                            return new int[]{-1, -1};
                        }
                    }
                    """,
                    """
                    import java.util.*;

                    public class Solution {
                        public static int[] twoSum(int[] nums, int target) {
                            Map<Integer, Integer> seen = new HashMap<>();
                            for (int i = 0; i < nums.length; i++) {
                                int need = target - nums[i];
                                if (seen.containsKey(need)) {
                                    return new int[]{seen.get(need), i};
                                }
                                seen.put(nums[i], i);
                            }
                            return new int[]{-1, -1};
                        }
                    }
                    """,
                    (cls, rec) -> {
                        Method m = cls.getMethod("twoSum", int[].class, int.class);
                        rec.checkEquals("[2,7,11,15], 9", new int[]{0, 1}, m.invoke(null, new int[]{2, 7, 11, 15}, 9));
                        rec.checkEquals("[3,2,4], 6", new int[]{1, 2}, m.invoke(null, new int[]{3, 2, 4}, 6));
                        rec.checkEquals("no pair", new int[]{-1, -1}, m.invoke(null, new int[]{1, 2, 3}, 99));
                    }
            ),
            new CodingProblem(
                    "P5.2", "05. Collections & Hashing", "Valid Brackets (Stack)", "Easy",
                    """
                    <h1>P5.2 — Valid Parentheses / Brackets</h1>
                    <p>Given <code>s</code> containing <code>()[]{}</code>, return <code>true</code> if every opening bracket is closed in valid LIFO order.</p>
                    <pre>isValidBrackets("({[]})") -&gt; true
                    isValidBrackets("([)]")   -&gt; false</pre>
                    """,
                    "Use `Deque<Character> stack = new ArrayDeque<>()`. Push expected closing bracket on open; check `stack.isEmpty() || stack.pop() != ch` on close!",
                    """
                    import java.util.*;

                    public class Solution {
                        public static boolean isValidBrackets(String s) {
                            // TODO: Use ArrayDeque as a Stack
                            return false;
                        }
                    }
                    """,
                    """
                    import java.util.*;

                    public class Solution {
                        public static boolean isValidBrackets(String s) {
                            if (s == null) return false;
                            Deque<Character> stack = new ArrayDeque<>();
                            for (char ch : s.toCharArray()) {
                                if (ch == '(') stack.push(')');
                                else if (ch == '[') stack.push(']');
                                else if (ch == '{') stack.push('}');
                                else if (stack.isEmpty() || stack.pop() != ch) return false;
                            }
                            return stack.isEmpty();
                        }
                    }
                    """,
                    (cls, rec) -> {
                        Method m = cls.getMethod("isValidBrackets", String.class);
                        rec.checkEquals("({[]})", true, m.invoke(null, "({[]})"));
                        rec.checkEquals("([)]", false, m.invoke(null, "([)]"));
                        rec.checkEquals("(((", false, m.invoke(null, "((("));
                        rec.checkEquals("]", false, m.invoke(null, "]"));
                    }
            ),
            new CodingProblem(
                    "P5.3", "05. Collections & Hashing", "Group Anagrams", "Medium",
                    """
                    <h1>P5.3 — Group Anagrams</h1>
                    <p>Group strings that are anagrams of each other into sublists (any group order is accepted).</p>
                    <pre>["eat","tea","tan","ate","nat","bat"] -&gt; [["ate","eat","tea"], ["nat","tan"], ["bat"]]</pre>
                    """,
                    "Sort `w.toCharArray()` into a canonical `String key`, then `map.computeIfAbsent(key, k -> new ArrayList<>()).add(w)`.",
                    """
                    import java.util.*;

                    public class Solution {
                        public static List<List<String>> groupAnagrams(String[] words) {
                            // TODO: Group words by sorted character key
                            return List.of();
                        }
                    }
                    """,
                    """
                    import java.util.*;

                    public class Solution {
                        public static List<List<String>> groupAnagrams(String[] words) {
                            Map<String, List<String>> map = new HashMap<>();
                            for (String w : words) {
                                char[] ch = w.toCharArray();
                                Arrays.sort(ch);
                                map.computeIfAbsent(new String(ch), k -> new ArrayList<>()).add(w);
                            }
                            return new ArrayList<>(map.values());
                        }
                    }
                    """,
                    (cls, rec) -> {
                        Method m = cls.getMethod("groupAnagrams", String[].class);
                        @SuppressWarnings("unchecked")
                        List<List<String>> out = (List<List<String>>) m.invoke(
                                null, (Object) new String[]{"eat", "tea", "tan", "ate", "nat", "bat"});
                        List<List<String>> exp = normGroups(List.of(
                                List.of("ate", "eat", "tea"), List.of("bat"), List.of("nat", "tan")));
                        rec.checkEquals("grouped anagrams", exp, normGroups(out));
                    }
            ),
            new CodingProblem(
                    "P5.4", "05. Collections & Hashing", "Top K Frequent Elements", "Medium",
                    """
                    <h1>P5.4 — Top K Frequent Elements</h1>
                    <p>Return an <code>int[]</code> of length <code>k</code> ordered from highest frequency to lowest (break ties by smaller integer first).</p>
                    <pre>topKFrequent([1,1,1,2,2,3], 2) -&gt; [1, 2]</pre>
                    """,
                    "Count frequencies in `HashMap<Integer,Integer>`, sort unique keys with custom comparator `(a,b) -> ...`, copy first `k`.",
                    """
                    import java.util.*;

                    public class Solution {
                        public static int[] topKFrequent(int[] nums, int k) {
                            // TODO: Return top k most frequent ints
                            return new int[0];
                        }
                    }
                    """,
                    """
                    import java.util.*;

                    public class Solution {
                        public static int[] topKFrequent(int[] nums, int k) {
                            Map<Integer, Integer> freq = new HashMap<>();
                            for (int n : nums) freq.put(n, freq.getOrDefault(n, 0) + 1);
                            List<Integer> keys = new ArrayList<>(freq.keySet());
                            keys.sort((a, b) -> {
                                int c = Integer.compare(freq.get(b), freq.get(a));
                                return c != 0 ? c : Integer.compare(a, b);
                            });
                            int[] res = new int[k];
                            for (int i = 0; i < k; i++) res[i] = keys.get(i);
                            return res;
                        }
                    }
                    """,
                    (cls, rec) -> {
                        Method m = cls.getMethod("topKFrequent", int[].class, int.class);
                        rec.checkEquals("[1,1,1,2,2,3], k=2", new int[]{1, 2},
                                m.invoke(null, new int[]{1, 1, 1, 2, 2, 3}, 2));
                        rec.checkEquals("tie-break [4,4,1,1,2], k=2", new int[]{1, 4},
                                m.invoke(null, new int[]{4, 4, 1, 1, 2}, 2));
                    }
            ),
            new CodingProblem(
                    "P5.5", "05. Collections & Hashing", "Longest Consecutive Sequence", "Medium",
                    """
                    <h1>P5.5 — Longest Consecutive Sequence (O(n) HashSet)</h1>
                    <p>Return the length of the longest consecutive sequence in unsorted <code>nums</code> in <code>O(n)</code> time.</p>
                    <pre>longestConsecutive([100, 4, 200, 1, 3, 2]) -&gt; 4 ([1,2,3,4])</pre>
                    """,
                    "Add all ints to a `HashSet`. Only start walking `curr + 1` from `x` if `!set.contains(x - 1)`!",
                    """
                    import java.util.*;

                    public class Solution {
                        public static int longestConsecutive(int[] nums) {
                            // TODO: O(n) HashSet sequence start trick
                            return 0;
                        }
                    }
                    """,
                    """
                    import java.util.*;

                    public class Solution {
                        public static int longestConsecutive(int[] nums) {
                            if (nums == null || nums.length == 0) return 0;
                            Set<Integer> set = new HashSet<>();
                            for (int n : nums) set.add(n);
                            int best = 0;
                            for (int x : set) {
                                if (!set.contains(x - 1)) {
                                    int cur = x, len = 1;
                                    while (set.contains(cur + 1)) { cur++; len++; }
                                    best = Math.max(best, len);
                                }
                            }
                            return best;
                        }
                    }
                    """,
                    (cls, rec) -> {
                        Method m = cls.getMethod("longestConsecutive", int[].class);
                        rec.checkEquals("[100,4,200,1,3,2]", 4,
                                m.invoke(null, (Object) new int[]{100, 4, 200, 1, 3, 2}));
                        rec.checkEquals("[0,3,7,2,5,8,4,6,0,1]", 9,
                                m.invoke(null, (Object) new int[]{0, 3, 7, 2, 5, 8, 4, 6, 0, 1}));
                    }
            ),

            // ==================== 06. STREAMS & EXCEPTIONS ====================
            new CodingProblem(
                    "P6.1", "06. Streams & Exceptions", "Stream Clean & Sort Tags", "Easy",
                    """
                    <h1>P6.1 — Stream Pipeline: Clean, Uppercase, Deduplicate &amp; Sort</h1>
                    <p>Using Streams on <code>rawTags</code>: skip <code>null</code>s, <code>trim()</code>, keep length <code>&ge; 3</code>, convert to uppercase, deduplicate, and sort alphabetically!</p>
                    <pre>[" java ", null, "ai", "Spring", "JAVA", "sql "] -&gt; ["JAVA", "SPRING", "SQL"]</pre>
                    """,
                    "`rawTags.stream().filter(Objects::nonNull).map(String::trim).filter(s -> s.length() >= 3).map(String::toUpperCase).distinct().sorted().toList()`",
                    """
                    import java.util.*;
                    import java.util.stream.*;

                    public class Solution {
                        public static List<String> cleanAndSortTags(List<String> rawTags) {
                            // TODO: Build Stream pipeline
                            return List.of();
                        }
                    }
                    """,
                    """
                    import java.util.*;
                    import java.util.stream.*;

                    public class Solution {
                        public static List<String> cleanAndSortTags(List<String> rawTags) {
                            if (rawTags == null) return List.of();
                            return rawTags.stream()
                                    .filter(Objects::nonNull)
                                    .map(String::trim)
                                    .filter(s -> s.length() >= 3)
                                    .map(s -> s.toUpperCase(Locale.ROOT))
                                    .distinct()
                                    .sorted()
                                    .toList();
                        }
                    }
                    """,
                    (cls, rec) -> {
                        Method m = cls.getMethod("cleanAndSortTags", List.class);
                        List<String> in = Arrays.asList(" java ", null, "ai", "Spring", "JAVA", "sql ", "  ");
                        rec.checkEquals("cleaned tags", List.of("JAVA", "SPRING", "SQL"), m.invoke(null, in));
                    }
            ),
            new CodingProblem(
                    "P6.2", "06. Streams & Exceptions", "Max Salary per Department", "Medium",
                    """
                    <h1>P6.2 — Collectors: Highest Salary by Department</h1>
                    <p>Return a <code>Map&lt;String, Integer&gt;</code> mapping each department to its maximum salary.</p>
                    """,
                    "Use `Collectors.toMap(Employee::dept, Employee::salary, Integer::max)`!",
                    """
                    import java.util.*;
                    import java.util.stream.*;

                    public class Solution {
                        public record Employee(String name, String dept, int salary) {}

                        public static Map<String, Integer> maxSalaryByDept(List<Employee> employees) {
                            // TODO: Group max salary by dept
                            return Map.of();
                        }
                    }
                    """,
                    """
                    import java.util.*;
                    import java.util.stream.*;

                    public class Solution {
                        public record Employee(String name, String dept, int salary) {}

                        public static Map<String, Integer> maxSalaryByDept(List<Employee> employees) {
                            if (employees == null) return Map.of();
                            return employees.stream()
                                    .collect(Collectors.toMap(Employee::dept, Employee::salary, Integer::max));
                        }
                    }
                    """,
                    (cls, rec) -> {
                        Class<?> empCls = Class.forName("Solution$Employee", true, cls.getClassLoader());
                        Constructor<?> ctor = empCls.getConstructor(String.class, String.class, int.class);
                        Object e1 = ctor.newInstance("Asha", "Eng", 120);
                        Object e2 = ctor.newInstance("Vikram", "Eng", 150);
                        Object e3 = ctor.newInstance("Raj", "QA", 110);
                        Method m = cls.getMethod("maxSalaryByDept", List.class);
                        rec.checkEquals("maxSalaryByDept", Map.of("Eng", 150, "QA", 110),
                                m.invoke(null, List.of(e1, e2, e3)));
                    }
            ),
            new CodingProblem(
                    "P6.3", "06. Streams & Exceptions", "Sum of Squares of Evens", "Easy",
                    """
                    <h1>P6.3 — Stream Reduction: Sum of Squares of Even Numbers</h1>
                    <p>Return the <code>long</code> sum of <code>(long) n * n</code> for all non-null even integers in <code>nums</code>.</p>
                    <pre>[1, 2, 3, 4, -2, null] -&gt; 4 + 16 + 4 = 24L</pre>
                    """,
                    "`nums.stream().filter(Objects::nonNull).filter(n -> n % 2 == 0).mapToLong(n -> (long) n * n).sum()`",
                    """
                    import java.util.*;

                    public class Solution {
                        public static long sumOfSquaresOfEvens(List<Integer> nums) {
                            // TODO: Sum (long) n * n for non-null even ints
                            return 0L;
                        }
                    }
                    """,
                    """
                    import java.util.*;

                    public class Solution {
                        public static long sumOfSquaresOfEvens(List<Integer> nums) {
                            if (nums == null) return 0L;
                            return nums.stream()
                                    .filter(Objects::nonNull)
                                    .filter(n -> n % 2 == 0)
                                    .mapToLong(n -> (long) n * n)
                                    .sum();
                        }
                    }
                    """,
                    (cls, rec) -> {
                        Method m = cls.getMethod("sumOfSquaresOfEvens", List.class);
                        rec.checkEquals("[1,2,3,4,-2,null]", 24L,
                                m.invoke(null, Arrays.asList(1, 2, 3, 4, -2, null)));
                        rec.checkEquals("[1,3,5]", 0L, m.invoke(null, List.of(1, 3, 5)));
                    }
            ),
            new CodingProblem(
                    "P6.4", "06. Streams & Exceptions", "Defensive Score Parser", "Easy",
                    """
                    <h1>P6.4 — Safe Parsing &amp; Exception Handling</h1>
                    <p>Trim <code>rawInput</code> and parse an exam score in <code>[1, 100]</code>. Return <code>-1</code> if <code>null</code>, invalid format (<code>NumberFormatException</code>), or out of range!</p>
                    """,
                    "Wrap `Integer.parseInt(rawInput.trim())` in `try { ... } catch (NumberFormatException e) { return -1; }`.",
                    """
                    public class Solution {
                        public static int parsePositiveScore(String rawInput) {
                            // TODO: Parse safely and validate 1..100
                            return -1;
                        }
                    }
                    """,
                    """
                    public class Solution {
                        public static int parsePositiveScore(String rawInput) {
                            if (rawInput == null) return -1;
                            try {
                                int v = Integer.parseInt(rawInput.trim());
                                return (v >= 1 && v <= 100) ? v : -1;
                            } catch (NumberFormatException e) {
                                return -1;
                            }
                        }
                    }
                    """,
                    (cls, rec) -> {
                        Method m = cls.getMethod("parsePositiveScore", String.class);
                        rec.checkEquals("\" 85 \"", 85, m.invoke(null, " 85 "));
                        rec.checkEquals("\"0\"", -1, m.invoke(null, "0"));
                        rec.checkEquals("\"105\"", -1, m.invoke(null, "105"));
                        rec.checkEquals("\"85pts\"", -1, m.invoke(null, "85pts"));
                        rec.checkEquals("null", -1, m.invoke(null, (Object) null));
                    }
            ),

            // ==================== 07. LINKED LISTS, MATRICES & DP ====================
            new CodingProblem(
                    "P7.1", "07. Linked Lists, Matrices & DP", "Move All '#' to Front", "Easy",
                    """
                    <h1>P7.1 — Move All '#' Characters to Front</h1>
                    <p>Move every <code>'#'</code> character in <code>s</code> to the front while preserving the exact order of all other characters.</p>
                    <pre>moveHashesToFront("Move#Hash#to#Front") -&gt; "###MoveHashtoFront"</pre>
                    """,
                    "Use two `StringBuilder`s (`hashes` and `rest`), then `return hashes.append(rest).toString()`!",
                    """
                    public class Solution {
                        public static String moveHashesToFront(String s) {
                            // TODO: Move all '#' chars to the start
                            return "";
                        }
                    }
                    """,
                    """
                    public class Solution {
                        public static String moveHashesToFront(String s) {
                            if (s == null) return "";
                            StringBuilder hashes = new StringBuilder();
                            StringBuilder rest = new StringBuilder();
                            for (int i = 0; i < s.length(); i++) {
                                char c = s.charAt(i);
                                if (c == '#') hashes.append('#');
                                else rest.append(c);
                            }
                            return hashes.append(rest).toString();
                        }
                    }
                    """,
                    (cls, rec) -> {
                        Method m = cls.getMethod("moveHashesToFront", String.class);
                        rec.checkEquals("Move#Hash#to#Front", "###MoveHashtoFront", m.invoke(null, "Move#Hash#to#Front"));
                        rec.checkEquals("BackendJava", "BackendJava", m.invoke(null, "BackendJava"));
                        rec.checkEquals("abc#", "#abc", m.invoke(null, "abc#"));
                    }
            ),
            new CodingProblem(
                    "P7.2", "07. Linked Lists, Matrices & DP", "Count Subtraction Borrows", "Medium",
                    """
                    <h1>P7.2 — Count Borrow Operations in Subtraction</h1>
                    <p>Given non-negative <code>num1</code> and <code>num2</code>: if <code>num1 &lt; num2</code>, return <code>-1</code>. Otherwise simulate digit-by-digit subtraction right-to-left and count total borrows!</p>
                    <pre>countBorrows(754, 658) -&gt; 2
                    countBorrows(500, 1)   -&gt; 2
                    countBorrows(100, 200) -&gt; -1</pre>
                    """,
                    "Track `borrow = 0`. At each rightmost digit: `int d1 = (num1 % 10) - borrow, d2 = num2 % 10; if (d1 < d2) { count++; borrow = 1; } else borrow = 0;`.",
                    """
                    public class Solution {
                        public static int countBorrows(int num1, int num2) {
                            // TODO: Count column borrows when subtracting num2 from num1
                            return 0;
                        }
                    }
                    """,
                    """
                    public class Solution {
                        public static int countBorrows(int num1, int num2) {
                            if (num1 < num2) return -1;
                            int count = 0, borrow = 0;
                            while (num1 > 0 || num2 > 0) {
                                int d1 = (num1 % 10) - borrow;
                                int d2 = num2 % 10;
                                if (d1 < d2) {
                                    count++;
                                    borrow = 1;
                                } else {
                                    borrow = 0;
                                }
                                num1 /= 10;
                                num2 /= 10;
                            }
                            return count;
                        }
                    }
                    """,
                    (cls, rec) -> {
                        Method m = cls.getMethod("countBorrows", int.class, int.class);
                        rec.checkEquals("754 - 658", 2, m.invoke(null, 754, 658));
                        rec.checkEquals("500 - 1", 2, m.invoke(null, 500, 1));
                        rec.checkEquals("654 - 321", 0, m.invoke(null, 654, 321));
                        rec.checkEquals("100 - 200", -1, m.invoke(null, 100, 200));
                    }
            ),
            new CodingProblem(
                    "P7.3", "07. Linked Lists, Matrices & DP", "Find Sorted Duplicates", "Easy",
                    """
                    <h1>P7.3 — Find Duplicate Elements (Sorted Unique List)</h1>
                    <p>Return a sorted <code>List&lt;Integer&gt;</code> of every distinct value that appears at least twice in <code>nums</code>.</p>
                    <pre>findDuplicates([4,3,2,7,8,2,3,1,2]) -&gt; [2, 3]</pre>
                    """,
                    "Use `Set<Integer> seen = new HashSet<>()` and `Set<Integer> dupes = new TreeSet<>()`. When `!seen.add(n)`, add `n` to `dupes`!",
                    """
                    import java.util.*;

                    public class Solution {
                        public static List<Integer> findDuplicates(int[] nums) {
                            // TODO: Return sorted unique list of repeating numbers
                            return List.of();
                        }
                    }
                    """,
                    """
                    import java.util.*;

                    public class Solution {
                        public static List<Integer> findDuplicates(int[] nums) {
                            if (nums == null) return List.of();
                            Set<Integer> seen = new HashSet<>();
                            Set<Integer> dupes = new TreeSet<>();
                            for (int n : nums) {
                                if (!seen.add(n)) dupes.add(n);
                            }
                            return new ArrayList<>(dupes);
                        }
                    }
                    """,
                    (cls, rec) -> {
                        Method m = cls.getMethod("findDuplicates", int[].class);
                        rec.checkEquals("[4,3,2,7,8,2,3,1,2]", List.of(2, 3),
                                m.invoke(null, (Object) new int[]{4, 3, 2, 7, 8, 2, 3, 1, 2}));
                        rec.checkEquals("[1,2,3]", List.of(), m.invoke(null, (Object) new int[]{1, 2, 3}));
                    }
            ),
            new CodingProblem(
                    "P7.4", "07. Linked Lists, Matrices & DP", "Reverse Singly Linked List", "Easy",
                    """
                    <h1>P7.4 — Reverse a Singly Linked List (Live Interview Classic)</h1>
                    <p>Reverse the linked list starting at <code>head</code> in-place (<code>O(1)</code> space) and return the new head.</p>
                    <pre>1 -&gt; 2 -&gt; 3 -&gt; 4 -&gt; null   becomes   4 -&gt; 3 -&gt; 2 -&gt; 1 -&gt; null</pre>
                    """,
                    "`ListNode prev = null, curr = head;` Inside `while (curr != null)`: save `next = curr.next`, flip `curr.next = prev`, advance `prev = curr; curr = next;`.",
                    """
                    public class Solution {
                        public static class ListNode {
                            public int val;
                            public ListNode next;
                            public ListNode(int val) { this.val = val; }
                        }

                        public static ListNode reverseLinkedList(ListNode head) {
                            // TODO: Reverse pointers in-place
                            return head;
                        }
                    }
                    """,
                    """
                    public class Solution {
                        public static class ListNode {
                            public int val;
                            public ListNode next;
                            public ListNode(int val) { this.val = val; }
                        }

                        public static ListNode reverseLinkedList(ListNode head) {
                            ListNode prev = null, curr = head;
                            while (curr != null) {
                                ListNode next = curr.next;
                                curr.next = prev;
                                prev = curr;
                                curr = next;
                            }
                            return prev;
                        }
                    }
                    """,
                    (cls, rec) -> {
                        Class<?> nodeCls = Class.forName("Solution$ListNode", true, cls.getClassLoader());
                        Constructor<?> ctor = nodeCls.getConstructor(int.class);
                        Field valF = nodeCls.getField("val");
                        Field nextF = nodeCls.getField("next");

                        Object n1 = ctor.newInstance(1);
                        Object n2 = ctor.newInstance(2);
                        Object n3 = ctor.newInstance(3);
                        Object n4 = ctor.newInstance(4);
                        nextF.set(n1, n2);
                        nextF.set(n2, n3);
                        nextF.set(n3, n4);

                        Method rev = cls.getMethod("reverseLinkedList", nodeCls);
                        Object head = rev.invoke(null, n1);
                        List<Integer> out = new ArrayList<>();
                        int guard = 0;
                        while (head != null && guard++ < 20) {
                            out.add((Integer) valF.get(head));
                            head = nextF.get(head);
                        }
                        rec.checkEquals("1->2->3->4 reversed", List.of(4, 3, 2, 1), out);
                        rec.checkEquals("null list", null, rev.invoke(null, (Object) null));
                    }
            ),
            new CodingProblem(
                    "P7.5", "07. Linked Lists, Matrices & DP", "Matrix Diagonal Sum", "Easy",
                    """
                    <h1>P7.5 — Square Matrix Diagonal Sum</h1>
                    <p>Sum primary diagonal <code>mat[i][i]</code> and secondary diagonal <code>mat[i][n-1-i]</code>. Count the center cell only once when <code>n</code> is odd!</p>
                    """,
                    "Loop `i = 0..n-1` adding `mat[i][i] + mat[i][n-1-i]`. If `n % 2 == 1`, subtract `mat[n/2][n/2]`.",
                    """
                    public class Solution {
                        public static int matrixDiagonalSum(int[][] mat) {
                            // TODO: Sum both diagonals without double-counting center
                            return 0;
                        }
                    }
                    """,
                    """
                    public class Solution {
                        public static int matrixDiagonalSum(int[][] mat) {
                            int n = mat.length, sum = 0;
                            for (int i = 0; i < n; i++) {
                                sum += mat[i][i] + mat[i][n - 1 - i];
                            }
                            if (n % 2 == 1) sum -= mat[n / 2][n / 2];
                            return sum;
                        }
                    }
                    """,
                    (cls, rec) -> {
                        Method m = cls.getMethod("matrixDiagonalSum", int[][].class);
                        rec.checkEquals("3x3 matrix", 25,
                                m.invoke(null, (Object) new int[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}));
                        rec.checkEquals("2x2 matrix", 4,
                                m.invoke(null, (Object) new int[][]{{1, 1}, {1, 1}}));
                    }
            ),
            new CodingProblem(
                    "P7.6", "07. Linked Lists, Matrices & DP", "Max Non-Adjacent Sum (1D DP)", "Medium",
                    """
                    <h1>P7.6 — Maximum Non-Adjacent Sum (Intro Dynamic Programming)</h1>
                    <p>Given non-negative integers <code>nums</code>, return the maximum sum of elements you can pick without picking two adjacent elements.</p>
                    <pre>maxNonAdjacentSum([2, 7, 9, 3, 1]) -&gt; 12 (2 + 9 + 1)</pre>
                    """,
                    "At each element `x`, best is `Math.max(prev1, prev2 + x)`. Track `prev2 = 0, prev1 = 0`.",
                    """
                    public class Solution {
                        public static int maxNonAdjacentSum(int[] nums) {
                            // TODO: Space-optimized 1D DP
                            return 0;
                        }
                    }
                    """,
                    """
                    public class Solution {
                        public static int maxNonAdjacentSum(int[] nums) {
                            if (nums == null) return 0;
                            int prev2 = 0, prev1 = 0;
                            for (int x : nums) {
                                int cur = Math.max(prev1, prev2 + x);
                                prev2 = prev1;
                                prev1 = cur;
                            }
                            return prev1;
                        }
                    }
                    """,
                    (cls, rec) -> {
                        Method m = cls.getMethod("maxNonAdjacentSum", int[].class);
                        rec.checkEquals("[2,7,9,3,1]", 12, m.invoke(null, (Object) new int[]{2, 7, 9, 3, 1}));
                        rec.checkEquals("[2,1,1,2]", 4, m.invoke(null, (Object) new int[]{2, 1, 1, 2}));
                        rec.checkEquals("empty", 0, m.invoke(null, (Object) new int[]{}));
                    }
            ),

            // ==================== 08. ENTERPRISE & DESIGN PATTERNS ====================
            new CodingProblem(
                    "P8.1", "08. Enterprise & Design Patterns", "LRU Cache (O(1) Get & Put)", "Medium",
                    """
                    <h1>P8.1 — Least Recently Used (LRU) Cache</h1>
                    <p>Design an <code>LruCache(int capacity)</code> supporting <code>O(1)</code> <code>get(int key)</code> (return <code>-1</code> if missing) and <code>put(int key, int value)</code>.</p>
                    <p>When size exceeds <code>capacity</code>, evict the <b>least recently accessed/inserted</b> key!</p>
                    <div class='tip'>Hint: Java's <code>LinkedHashMap&lt;Integer, Integer&gt;(capacity, 0.75f, true)</code> with <code>removeEldestEntry</code> implements access-order LRU in 10 lines!</div>
                    """,
                    "Extend or wrap `new LinkedHashMap<Integer,Integer>(capacity, 0.75f, true)` overriding `removeEldestEntry` when `size() > capacity`!",
                    """
                    import java.util.*;

                    public class Solution {
                        public static class LruCache {
                            private final int capacity;

                            public LruCache(int capacity) {
                                this.capacity = capacity;
                            }

                            public int get(int key) {
                                // TODO: Return value (marking key recently used) or -1
                                return -1;
                            }

                            public void put(int key, int value) {
                                // TODO: Insert/update and evict LRU if above capacity
                            }
                        }
                    }
                    """,
                    """
                    import java.util.*;

                    public class Solution {
                        public static class LruCache {
                            private final Map<Integer, Integer> map;

                            public LruCache(int capacity) {
                                this.map = new LinkedHashMap<>(capacity, 0.75f, true) {
                                    @Override
                                    protected boolean removeEldestEntry(Map.Entry<Integer, Integer> eldest) {
                                        return size() > capacity;
                                    }
                                };
                            }

                            public int get(int key) {
                                return map.getOrDefault(key, -1);
                            }

                            public void put(int key, int value) {
                                map.put(key, value);
                            }
                        }
                    }
                    """,
                    (cls, rec) -> {
                        Class<?> lruCls = Class.forName("Solution$LruCache", true, cls.getClassLoader());
                        Object cache = lruCls.getConstructor(int.class).newInstance(2);
                        Method get = lruCls.getMethod("get", int.class);
                        Method put = lruCls.getMethod("put", int.class, int.class);

                        put.invoke(cache, 1, 10);
                        put.invoke(cache, 2, 20);
                        rec.checkEquals("get(1) refreshes key 1", 10, get.invoke(cache, 1));
                        put.invoke(cache, 3, 30); // Evicts key 2!
                        rec.checkEquals("get(2) evicted", -1, get.invoke(cache, 2));
                        rec.checkEquals("get(3) present", 30, get.invoke(cache, 3));
                    }
            ),
            new CodingProblem(
                    "P8.2", "08. Enterprise & Design Patterns", "Fluent Immutable Request Builder", "Easy",
                    """
                    <h1>P8.2 — GoF Builder Pattern: Immutable ApiRequest</h1>
                    <p>Complete <code>ApiRequest.Builder</code>:</p>
                    <ul>
                      <li><code>url(String)</code>, <code>method(String)</code> (default <code>"GET"</code>), and <code>timeoutMs(int)</code> (default <code>3000</code>).</li>
                      <li>In <code>build()</code>: if <code>url == null || url.isBlank()</code>, or <code>timeoutMs &le; 0</code>, throw <code>IllegalArgumentException</code>!</li>
                    </ul>
                    """,
                    "Store defaults `method = \"GET\"` and `timeoutMs = 3000` in Builder; validate `url` and `timeoutMs` inside `build()` before constructing `ApiRequest`.",
                    """
                    public class Solution {
                        public static final class ApiRequest {
                            private final String url;
                            private final String method;
                            private final int timeoutMs;

                            private ApiRequest(Builder b) {
                                this.url = b.url;
                                this.method = b.method;
                                this.timeoutMs = b.timeoutMs;
                            }

                            public String getUrl() { return url; }
                            public String getMethod() { return method; }
                            public int getTimeoutMs() { return timeoutMs; }

                            public static final class Builder {
                                private String url;
                                private String method = "GET";
                                private int timeoutMs = 3000;

                                public Builder url(String url) {
                                    // TODO
                                    return this;
                                }

                                public Builder method(String method) {
                                    // TODO
                                    return this;
                                }

                                public Builder timeoutMs(int timeoutMs) {
                                    // TODO
                                    return this;
                                }

                                public ApiRequest build() {
                                    // TODO: Validate url non-blank & timeoutMs > 0
                                    return new ApiRequest(this);
                                }
                            }
                        }
                    }
                    """,
                    """
                    public class Solution {
                        public static final class ApiRequest {
                            private final String url;
                            private final String method;
                            private final int timeoutMs;

                            private ApiRequest(Builder b) {
                                this.url = b.url;
                                this.method = b.method;
                                this.timeoutMs = b.timeoutMs;
                            }

                            public String getUrl() { return url; }
                            public String getMethod() { return method; }
                            public int getTimeoutMs() { return timeoutMs; }

                            public static final class Builder {
                                private String url;
                                private String method = "GET";
                                private int timeoutMs = 3000;

                                public Builder url(String url) { this.url = url; return this; }
                                public Builder method(String method) { this.method = method; return this; }
                                public Builder timeoutMs(int timeoutMs) { this.timeoutMs = timeoutMs; return this; }

                                public ApiRequest build() {
                                    if (url == null || url.isBlank() || timeoutMs <= 0) {
                                        throw new IllegalArgumentException("Invalid request spec");
                                    }
                                    return new ApiRequest(this);
                                }
                            }
                        }
                    }
                    """,
                    (cls, rec) -> {
                        ClassLoader cl = cls.getClassLoader();
                        Class<?> reqCls = Class.forName("Solution$ApiRequest", true, cl);
                        Class<?> bCls = Class.forName("Solution$ApiRequest$Builder", true, cl);

                        Object b1 = bCls.getConstructor().newInstance();
                        bCls.getMethod("url", String.class).invoke(b1, "https://api.example.org/v1");
                        Object req1 = bCls.getMethod("build").invoke(b1);
                        rec.checkEquals("default method", "GET", reqCls.getMethod("getMethod").invoke(req1));
                        rec.checkEquals("default timeout", 3000, reqCls.getMethod("getTimeoutMs").invoke(req1));

                        Object bBad = bCls.getConstructor().newInstance();
                        rec.checkThrows("missing URL throws", IllegalArgumentException.class,
                                () -> bCls.getMethod("build").invoke(bBad));
                    }
            ),
            new CodingProblem(
                    "P8.3", "08. Enterprise & Design Patterns", "Per-Client API Rate Limiter", "Medium",
                    """
                    <h1>P8.3 — Per-Client Sliding Counter Rate Limiter</h1>
                    <p>Implement <code>allowRequest(String clientId, long timestampSec)</code> allowing at most <code>maxRequests</code> within each fixed window bucket <code>timestampSec / windowSec</code> per client!</p>
                    """,
                    "Compute bucket key `clientId + \":\" + (timestampSec / windowSec)` and track counts in a `Map<String, Integer>`!",
                    """
                    import java.util.*;

                    public class Solution {
                        public static class RateLimiter {
                            private final int maxRequests;
                            private final long windowSec;

                            public RateLimiter(int maxRequests, long windowSec) {
                                this.maxRequests = maxRequests;
                                this.windowSec = windowSec;
                            }

                            public boolean allowRequest(String clientId, long timestampSec) {
                                // TODO: Allow up to maxRequests per (timestampSec / windowSec) bucket
                                return false;
                            }
                        }
                    }
                    """,
                    """
                    import java.util.*;
                    import java.util.concurrent.ConcurrentHashMap;

                    public class Solution {
                        public static class RateLimiter {
                            private final int maxRequests;
                            private final long windowSec;
                            private final Map<String, Integer> counts = new ConcurrentHashMap<>();

                            public RateLimiter(int maxRequests, long windowSec) {
                                this.maxRequests = maxRequests;
                                this.windowSec = windowSec;
                            }

                            public boolean allowRequest(String clientId, long timestampSec) {
                                String bucket = clientId + ":" + (timestampSec / windowSec);
                                int next = counts.getOrDefault(bucket, 0) + 1;
                                if (next > maxRequests) return false;
                                counts.put(bucket, next);
                                return true;
                            }
                        }
                    }
                    """,
                    (cls, rec) -> {
                        Class<?> rlCls = Class.forName("Solution$RateLimiter", true, cls.getClassLoader());
                        Object rl = rlCls.getConstructor(int.class, long.class).newInstance(2, 10L);
                        Method allow = rlCls.getMethod("allowRequest", String.class, long.class);

                        rec.checkEquals("u1 t=1 (#1)", true, allow.invoke(rl, "u1", 1L));
                        rec.checkEquals("u1 t=5 (#2)", true, allow.invoke(rl, "u1", 5L));
                        rec.checkEquals("u1 t=8 (#3 throttled)", false, allow.invoke(rl, "u1", 8L));
                        rec.checkEquals("u2 t=8 (separate user)", true, allow.invoke(rl, "u2", 8L));
                        rec.checkEquals("u1 t=12 (new window)", true, allow.invoke(rl, "u1", 12L));
                    }
            ),
            new CodingProblem(
                    "P8.4", "08. Enterprise & Design Patterns", "Async CompletableFuture Combiner", "Medium",
                    """
                    <h1>P8.4 — Resilient CompletableFuture Price + Tax Combiner</h1>
                    <p>Given two asynchronous futures <code>priceFuture</code> and <code>taxRateFuture</code>:</p>
                    <ul>
                      <li>If <code>taxRateFuture</code> fails exceptionally, fall back to default tax rate <code>0.10</code> (<code>exceptionally</code>)!</li>
                      <li>Combine both futures to return <code>Math.round(price * (1.0 + taxRate))</code> as a <code>long</code>!</li>
                    </ul>
                    """,
                    "Use `taxRateFuture.exceptionally(ex -> 0.10)` then `priceFuture.thenCombine(safeTax, (p, t) -> Math.round(p * (1.0 + t))).join()`.",
                    """
                    import java.util.concurrent.CompletableFuture;

                    public class Solution {
                        public static long computeFinalAmount(
                                CompletableFuture<Double> priceFuture,
                                CompletableFuture<Double> taxRateFuture) {
                            // TODO: Fallback tax to 0.10 on error, combine & join
                            return 0L;
                        }
                    }
                    """,
                    """
                    import java.util.concurrent.CompletableFuture;

                    public class Solution {
                        public static long computeFinalAmount(
                                CompletableFuture<Double> priceFuture,
                                CompletableFuture<Double> taxRateFuture) {
                            CompletableFuture<Double> safeTax = taxRateFuture.exceptionally(ex -> 0.10);
                            return priceFuture
                                    .thenCombine(safeTax, (price, tax) -> Math.round(price * (1.0 + tax)))
                                    .join();
                        }
                    }
                    """,
                    (cls, rec) -> {
                        Method m = cls.getMethod("computeFinalAmount",
                                CompletableFuture.class, CompletableFuture.class);
                        Object ok = m.invoke(null,
                                CompletableFuture.completedFuture(1000.0),
                                CompletableFuture.completedFuture(0.18));
                        rec.checkEquals("1000 + 18% tax", 1180L, ok);

                        Object fallback = m.invoke(null,
                                CompletableFuture.completedFuture(500.0),
                                CompletableFuture.failedFuture(new RuntimeException("Tax timeout")));
                        rec.checkEquals("500 + 10% fallback on error", 550L, fallback);
                    }
            )
    );
}
