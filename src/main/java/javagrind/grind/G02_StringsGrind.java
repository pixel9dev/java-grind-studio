package javagrind.grind;

import javagrind.util.TestRunner;

/**
 * ==========================================================================
 * GRIND 02: Strings, Two-Pointers & StringBuilder (5 Problems)
 * ==========================================================================
 * Prerequisite: Run & read (L02_StringsAndStringBuilder.java:1) first!
 * Stuck for >15 mins? Check (S02_StringsSolutions.java:1).
 */
public class G02_StringsGrind {

    /**
     * P2.1: Valid Alphanumeric Palindrome
     * Return true if `s` reads the same forward and backward after ignoring case
     * and skipping all non-alphanumeric characters (`!Character.isLetterOrDigit(ch)`).
     *
     * Examples:
     *   isValidPalindrome("A man, a plan, a canal: Panama") -> true
     *   isValidPalindrome("race a car")                     -> false
     *   isValidPalindrome(" ")                              -> true
     */
    public static boolean isValidPalindrome(String s) {
        return TestRunner.todo();
    }

    /**
     * P2.2: Valid Anagram (Lowercase English Letters)
     * Return true if `t` contains the exact same characters with the exact same
     * frequencies as `s`. Assume lowercase 'a'-'z'. If either is null, return false.
     *
     * Examples:
     *   isAnagram("listen", "silent") -> true
     *   isAnagram("rat", "car")       -> false
     */
    public static boolean isAnagram(String s, String t) {
        return TestRunner.todo();
    }

    /**
     * P2.3: First Non-Repeating Character (#1 Java Walk-In Favorite!)
     * Given a string `s`, return the FIRST character that appears with frequency 1.
     * If every character repeats (or `s` is empty), return `'_'`.
     *
     * Examples:
     *   firstNonRepeatingChar("swiss")     -> 'w'
     *   firstNonRepeatingChar("developer") -> 'd'
     *   firstNonRepeatingChar("aabb")      -> '_'
     */
    public static char firstNonRepeatingChar(String s) {
        return TestRunner.todo();
    }

    /**
     * P2.4: Reverse Words in a Sentence
     * Reverse the order of words in `sentence`. Remove leading/trailing spaces
     * and reduce multiple spaces between words to a single space.
     * If `sentence` has no words (empty or all spaces), return `""`.
     *
     * Examples:
     *   reverseWords("  the sky   is blue ") -> "blue is sky the"
     *   reverseWords("hello")                -> "hello"
     *   reverseWords("   ")                  -> ""
     */
    public static String reverseWords(String sentence) {
        return TestRunner.todo();
    }

    /**
     * P2.5: Basic String Compression (Run-Length Encoding)
     * Compress consecutive identical characters as `<char><count>`:
     * e.g., "aabcccccaaa" -> "a2b1c5a3".
     * RULE: If the compressed string is NOT strictly shorter than `s`, return `s`!
     *
     * Examples:
     *   compressString("aabcccccaaa") -> "a2b1c5a3" (length 8 < 11)
     *   compressString("abc")         -> "abc"      ("a1b1c1" length 6 >= 3)
     *   compressString("aaaa")        -> "a4"       (length 2 < 4)
     */
    public static String compressString(String s) {
        return TestRunner.todo();
    }

    // ==================== DO NOT EDIT BELOW THIS LINE (TEST HARNESS) ====================

    public static TestRunner.ModuleResult runTests(boolean verbose) {
        return new TestRunner.Suite("GRIND 02: Strings & StringBuilder")
                .problem("P2.1", "isValidPalindrome(String s)",
                        "Use two pointers `left = 0, right = s.length() - 1`, skipping non-alphanumerics.",
                        v -> {
                            v.assertTrue("Panama palindrome", isValidPalindrome("A man, a plan, a canal: Panama"));
                            v.assertFalse("isValidPalindrome(\"race a car\")", isValidPalindrome("race a car"));
                            v.assertTrue("isValidPalindrome(\" \")", isValidPalindrome(" "));
                            v.assertTrue("isValidPalindrome(\"0P0\")", isValidPalindrome("0P0"));
                            v.assertFalse("isValidPalindrome(\"0P\")", isValidPalindrome("0P"));
                        })
                .problem("P2.2", "isAnagram(String s, String t)",
                        "Check s.length() == t.length(), then increment/decrement `int[] freq = new int[26]`.",
                        v -> {
                            v.assertTrue("listen/silent", isAnagram("listen", "silent"));
                            v.assertTrue("anagram/nagaram", isAnagram("anagram", "nagaram"));
                            v.assertFalse("rat/car", isAnagram("rat", "car"));
                            v.assertFalse("ab/a", isAnagram("ab", "a"));
                            v.assertFalse("null check", isAnagram(null, "abc"));
                        })
                .problem("P2.3", "firstNonRepeatingChar(String s)",
                        "Pass 1: count chars in `int[256]` or `HashMap`. Pass 2: scan `s` left-to-right for count == 1.",
                        v -> {
                            v.assertEquals("swiss", 'w', firstNonRepeatingChar("swiss"));
                            v.assertEquals("developer", 'd', firstNonRepeatingChar("developer"));
                            v.assertEquals("stress", 't', firstNonRepeatingChar("stress"));
                            v.assertEquals("aabb", '_', firstNonRepeatingChar("aabb"));
                            v.assertEquals("empty", '_', firstNonRepeatingChar(""));
                        })
                .problem("P2.4", "reverseWords(String sentence)",
                        "`String trimmed = sentence.trim();` if empty return \"\"; `split(\"\\\\s+\")` and append backwards.",
                        v -> {
                            v.assertEquals("the sky is blue", "blue is sky the", reverseWords("  the sky   is blue "));
                            v.assertEquals("single word", "hello", reverseWords("  hello  "));
                            v.assertEquals("blank spaces", "", reverseWords("     "));
                            v.assertEquals("Java drive", "2026 in drive Java", reverseWords("Java drive in 2026"));
                        })
                .problem("P2.5", "compressString(String s)",
                        "Count streak length in a loop, append `<ch><count>` to `StringBuilder`, compare lengths at end!",
                        v -> {
                            v.assertEquals("aabcccccaaa", "a2b1c5a3", compressString("aabcccccaaa"));
                            v.assertEquals("abc", "abc", compressString("abc"));
                            v.assertEquals("aabb", "aabb", compressString("aabb"));
                            v.assertEquals("aaaa", "a4", compressString("aaaa"));
                            v.assertEquals("empty", "", compressString(""));
                        })
                .run(verbose);
    }

    public static void main(String[] args) {
        runTests(true);
    }
}
