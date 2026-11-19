package javagrind.solutions;

import javagrind.util.TestRunner;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * REFERENCE SOLUTIONS FOR GRIND 05 (Collections, Hashing, Stack & Heaps).
 */
public class S05_CollectionsAndHashingSolutions {

    // P5.1: Single-Pass HashMap — O(n) Time | O(n) Space
    public static int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> seenIndex = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            if (seenIndex.containsKey(complement)) {
                return new int[]{seenIndex.get(complement), i};
            }
            seenIndex.put(nums[i], i);
        }
        return new int[]{-1, -1};
    }

    // P5.2: ArrayDeque Stack — O(n) Time | O(n) Space
    public static boolean isValidBrackets(String s) {
        if (s == null) return false;
        Deque<Character> stack = new ArrayDeque<>();
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch == '(') stack.push(')');
            else if (ch == '{') stack.push('}');
            else if (ch == '[') stack.push(']');
            else {
                if (stack.isEmpty() || stack.pop() != ch) {
                    return false;
                }
            }
        }
        return stack.isEmpty();
    }

    // P5.3: Canonical Sorted Key + computeIfAbsent — O(n * L log L) Time | O(n * L) Space
    public static List<List<String>> groupAnagrams(String[] words) {
        Map<String, List<String>> groups = new HashMap<>();
        for (String w : words) {
            char[] chars = w.toCharArray();
            Arrays.sort(chars);
            String key = new String(chars);
            groups.computeIfAbsent(key, k -> new ArrayList<>()).add(w);
        }
        return new ArrayList<>(groups.values());
    }

    // P5.4: Frequency Map + Custom Comparator — O(n log n) Time | O(n) Space
    public static int[] topKFrequent(int[] nums, int k) {
        Map<Integer, Integer> freq = new HashMap<>();
        for (int n : nums) {
            freq.put(n, freq.getOrDefault(n, 0) + 1);
        }
        List<Integer> unique = new ArrayList<>(freq.keySet());
        unique.sort((a, b) -> {
            int cmp = Integer.compare(freq.get(b), freq.get(a)); // Higher freq first
            return cmp != 0 ? cmp : Integer.compare(a, b);       // Tie-break smaller int first
        });
        int[] res = new int[k];
        for (int i = 0; i < k; i++) {
            res[i] = unique.get(i);
        }
        return res;
    }

    // P5.5: HashSet Sequence-Start Filter — O(n) Time | O(n) Space
    public static int longestConsecutive(int[] nums) {
        if (nums == null || nums.length == 0) return 0;
        Set<Integer> set = new HashSet<>();
        for (int n : nums) {
            set.add(n);
        }
        int best = 0;
        for (int n : set) {
            // Only begin walking if `n` is the START of a streak!
            if (!set.contains(n - 1)) {
                int curr = n;
                int streak = 1;
                while (set.contains(curr + 1)) {
                    curr++;
                    streak++;
                }
                best = Math.max(best, streak);
            }
        }
        return best;
    }

    public static TestRunner.ModuleResult verify(boolean verbose) {
        return new TestRunner.Suite("SOLUTIONS 05: Collections & Hashing")
                .problem("P5.1", "twoSum", "", v -> {
                    v.assertEquals("twoSum", new int[]{0, 1}, twoSum(new int[]{2, 7, 11, 15}, 9));
                })
                .problem("P5.2", "isValidBrackets", "", v -> {
                    v.assertTrue("valid", isValidBrackets("({[]})"));
                    v.assertFalse("invalid", isValidBrackets("([)]"));
                })
                .problem("P5.3", "groupAnagrams", "", v -> {
                    v.assertEquals("3 groups", 3,
                            groupAnagrams(new String[]{"eat", "tea", "tan", "ate", "nat", "bat"}).size());
                })
                .problem("P5.4", "topKFrequent", "", v -> {
                    v.assertEquals("top 2", new int[]{1, 4}, topKFrequent(new int[]{4, 4, 1, 1, 2}, 2));
                })
                .problem("P5.5", "longestConsecutive", "", v -> {
                    v.assertEquals("longest 4", 4, longestConsecutive(new int[]{100, 4, 200, 1, 3, 2}));
                })
                .run(verbose);
    }

    public static void main(String[] args) {
        verify(true);
    }
}
