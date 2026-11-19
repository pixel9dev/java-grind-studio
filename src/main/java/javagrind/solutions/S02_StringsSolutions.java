package javagrind.solutions;

import javagrind.util.TestRunner;

/**
 * REFERENCE SOLUTIONS FOR GRIND 02 (Strings & StringBuilder).
 */
public class S02_StringsSolutions {

    // P2.1: Two Pointers — O(n) Time | O(1) Space
    public static boolean isValidPalindrome(String s) {
        if (s == null) return false;
        int left = 0;
        int right = s.length() - 1;
        while (left < right) {
            while (left < right && !Character.isLetterOrDigit(s.charAt(left))) {
                left++;
            }
            while (left < right && !Character.isLetterOrDigit(s.charAt(right))) {
                right--;
            }
            if (Character.toLowerCase(s.charAt(left)) != Character.toLowerCase(s.charAt(right))) {
                return false;
            }
            left++;
            right--;
        }
        return true;
    }

    // P2.2: Frequency Bucket — O(n) Time | O(1) Space (26 ints)
    public static boolean isAnagram(String s, String t) {
        if (s == null || t == null || s.length() != t.length()) {
            return false;
        }
        int[] freq = new int[26];
        for (int i = 0; i < s.length(); i++) {
            freq[s.charAt(i) - 'a']++;
            freq[t.charAt(i) - 'a']--;
        }
        for (int count : freq) {
            if (count != 0) return false;
        }
        return true;
    }

    // P2.3: Two-Pass Frequency — O(n) Time | O(1) Space
    public static char firstNonRepeatingChar(String s) {
        if (s == null || s.isEmpty()) return '_';
        int[] counts = new int[256];
        for (int i = 0; i < s.length(); i++) {
            counts[s.charAt(i)]++;
        }
        for (int i = 0; i < s.length(); i++) {
            if (counts[s.charAt(i)] == 1) {
                return s.charAt(i);
            }
        }
        return '_';
    }

    // P2.4: Split + StringBuilder — O(n) Time | O(n) Space
    public static String reverseWords(String sentence) {
        if (sentence == null) return "";
        String trimmed = sentence.trim();
        if (trimmed.isEmpty()) return "";

        String[] words = trimmed.split("\\s+");
        StringBuilder sb = new StringBuilder(trimmed.length());
        for (int i = words.length - 1; i >= 0; i--) {
            sb.append(words[i]);
            if (i > 0) sb.append(' ');
        }
        return sb.toString();
    }

    // P2.5: Run-Length Encoding — O(n) Time | O(n) Space
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

    public static TestRunner.ModuleResult verify(boolean verbose) {
        return new TestRunner.Suite("SOLUTIONS 02: Strings & StringBuilder")
                .problem("P2.1", "isValidPalindrome", "", v -> {
                    v.assertTrue("Panama", isValidPalindrome("A man, a plan, a canal: Panama"));
                    v.assertFalse("0P", isValidPalindrome("0P"));
                })
                .problem("P2.2", "isAnagram", "", v -> {
                    v.assertTrue("listen/silent", isAnagram("listen", "silent"));
                    v.assertFalse("rat/car", isAnagram("rat", "car"));
                })
                .problem("P2.3", "firstNonRepeatingChar", "", v -> {
                    v.assertEquals("swiss", 'w', firstNonRepeatingChar("swiss"));
                    v.assertEquals("aabb", '_', firstNonRepeatingChar("aabb"));
                })
                .problem("P2.4", "reverseWords", "", v -> {
                    v.assertEquals("the sky is blue", "blue is sky the", reverseWords("  the sky   is blue "));
                    v.assertEquals("blank", "", reverseWords("   "));
                })
                .problem("P2.5", "compressString", "", v -> {
                    v.assertEquals("aabcccccaaa", "a2b1c5a3", compressString("aabcccccaaa"));
                    v.assertEquals("aabb", "aabb", compressString("aabb"));
                })
                .run(verbose);
    }

    public static void main(String[] args) {
        verify(true);
    }
}
