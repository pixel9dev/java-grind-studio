package javagrind.grind;

import javagrind.util.TestRunner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * ==========================================================================
 * GRIND 05: Collections, HashMap, HashSet, Stack & PriorityQueue (5 Problems)
 * ==========================================================================
 * Prerequisite: Run & read (L04_CollectionsAndGenerics.java:1) first!
 * Stuck for >15 mins? Check (S05_CollectionsAndHashingSolutions.java:1).
 */
public class G05_CollectionsAndHashingGrind {

    /**
     * P5.1: Two Sum (O(n) HashMap Classic — #1 Most Asked Coding Question!)
     * Return indices `new int[]{i, j}` (`i < j`) where `nums[i] + nums[j] == target`.
     * If no valid pair exists, return `new int[]{-1, -1}`.
     *
     * Example:
     *   twoSum(new int[]{2, 7, 11, 15}, 9) -> [0, 1]
     */
    public static int[] twoSum(int[] nums, int target) {
        return TestRunner.todo();
    }

    /**
     * P5.2: Valid Parentheses / Brackets (`ArrayDeque` Stack)
     * Given `s` containing only `'('`, `')'`, `'{'`, `'}'`, `'['`, `']'`,
     * return true if every open bracket is closed by the same type in the right order.
     *
     * Examples:
     *   isValidBrackets("({[]})") -> true
     *   isValidBrackets("([)]")   -> false
     *   isValidBrackets("(((")    -> false
     */
    public static boolean isValidBrackets(String s) {
        return TestRunner.todo();
    }

    /**
     * P5.3: Group Anagrams (`HashMap` + `computeIfAbsent`)
     * Group strings that are anagrams of each other. Order of groups does not matter!
     *
     * Example:
     *   ["eat","tea","tan","ate","nat","bat"]
     *   -> [["ate","eat","tea"], ["nat","tan"], ["bat"]]
     */
    public static List<List<String>> groupAnagrams(String[] words) {
        return TestRunner.todo();
    }

    /**
     * P5.4: Top K Frequent Elements (`HashMap` + `PriorityQueue` / Sorting)
     * Return an `int[]` of length `k` containing the `k` most frequent numbers in `nums`,
     * ordered from HIGHEST frequency to LOWEST frequency (break ties by smaller number first).
     *
     * Example:
     *   topKFrequent(new int[]{1, 1, 1, 2, 2, 3}, 2) -> [1, 2]
     */
    public static int[] topKFrequent(int[] nums, int k) {
        return TestRunner.todo();
    }

    /**
     * P5.5: Longest Consecutive Sequence (O(n) `HashSet` Trick!)
     * Given an unsorted array `nums`, return the length of the longest consecutive
     * sequence (e.g. `x, x+1, x+2, ...`). Must run in O(n) time (NO sorting!).
     *
     * Examples:
     *   longestConsecutive(new int[]{100, 4, 200, 1, 3, 2}) -> 4  ([1, 2, 3, 4])
     *   longestConsecutive(new int[]{})                     -> 0
     */
    public static int longestConsecutive(int[] nums) {
        return TestRunner.todo();
    }

    // ==================== DO NOT EDIT BELOW THIS LINE (TEST HARNESS) ====================

    private static List<List<String>> normalizeGroups(List<List<String>> groups) {
        if (groups == null) return null;
        List<List<String>> copy = new ArrayList<>();
        for (List<String> g : groups) {
            List<String> sortedInner = new ArrayList<>(g);
            Collections.sort(sortedInner);
            copy.add(sortedInner);
        }
        copy.sort(Comparator.comparing(list -> list.isEmpty() ? "" : list.get(0)));
        return copy;
    }

    public static TestRunner.ModuleResult runTests(boolean verbose) {
        return new TestRunner.Suite("GRIND 05: Collections, Hashing, Stack & Heaps")
                .problem("P5.1", "twoSum(int[] nums, int target)",
                        "Store `seen.put(nums[i], i)`; before putting, check `seen.containsKey(target - nums[i])`!",
                        v -> {
                            v.assertEquals("[2,7,11,15], 9", new int[]{0, 1}, twoSum(new int[]{2, 7, 11, 15}, 9));
                            v.assertEquals("[3,2,4], 6", new int[]{1, 2}, twoSum(new int[]{3, 2, 4}, 6));
                            v.assertEquals("[3,3], 6", new int[]{0, 1}, twoSum(new int[]{3, 3}, 6));
                            v.assertEquals("no pair", new int[]{-1, -1}, twoSum(new int[]{1, 2, 3}, 99));
                        })
                .problem("P5.2", "isValidBrackets(String s)",
                        "Use `Deque<Character> st = new ArrayDeque<>()`; push expected closing bracket when opening!",
                        v -> {
                            v.assertTrue("({[]})", isValidBrackets("({[]})"));
                            v.assertTrue("()[]{}", isValidBrackets("()[]{}"));
                            v.assertFalse("([)]", isValidBrackets("([)]"));
                            v.assertFalse("(((", isValidBrackets("((("));
                            v.assertFalse("]", isValidBrackets("]"));
                            v.assertTrue("empty", isValidBrackets(""));
                        })
                .problem("P5.3", "groupAnagrams(String[] words)",
                        "Sort `char[]` of each word as key, then `map.computeIfAbsent(key, k -> new ArrayList<>()).add(w)`.",
                        v -> {
                            List<List<String>> expected = normalizeGroups(List.of(
                                    List.of("ate", "eat", "tea"),
                                    List.of("bat"),
                                    List.of("nat", "tan")
                            ));
                            List<List<String>> actual = normalizeGroups(
                                    groupAnagrams(new String[]{"eat", "tea", "tan", "ate", "nat", "bat"}));
                            v.assertEquals("eat/tea/tan/ate/nat/bat", expected, actual);
                        })
                .problem("P5.4", "topKFrequent(int[] nums, int k)",
                        "Build `Map<Integer,Integer> freq`, sort keys by freq desc then value asc, take first k!",
                        v -> {
                            v.assertEquals("[1,1,1,2,2,3], k=2", new int[]{1, 2},
                                    topKFrequent(new int[]{1, 1, 1, 2, 2, 3}, 2));
                            v.assertEquals("[4,4,1,1,2], k=2 (tie-break smaller first)", new int[]{1, 4},
                                    topKFrequent(new int[]{4, 4, 1, 1, 2}, 2));
                            v.assertEquals("[7], k=1", new int[]{7}, topKFrequent(new int[]{7}, 1));
                        })
                .problem("P5.5", "longestConsecutive(int[] nums)",
                        "Put all in `HashSet`. Only start counting a streak from `x` if `!set.contains(x - 1)`!",
                        v -> {
                            v.assertEquals("[100,4,200,1,3,2]", 4,
                                    longestConsecutive(new int[]{100, 4, 200, 1, 3, 2}));
                            v.assertEquals("[0,3,7,2,5,8,4,6,0,1]", 9,
                                    longestConsecutive(new int[]{0, 3, 7, 2, 5, 8, 4, 6, 0, 1}));
                            v.assertEquals("empty", 0, longestConsecutive(new int[]{}));
                        })
                .run(verbose);
    }

    public static void main(String[] args) {
        runTests(true);
    }
}
