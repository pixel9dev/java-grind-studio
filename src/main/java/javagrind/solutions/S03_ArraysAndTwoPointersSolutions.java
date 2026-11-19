package javagrind.solutions;

import javagrind.util.TestRunner;

/**
 * REFERENCE SOLUTIONS FOR GRIND 03 (Arrays & Two Pointers).
 */
public class S03_ArraysAndTwoPointersSolutions {

    // P3.1: Single-Pass with long sentinels — O(n) Time | O(1) Space
    public static int secondLargestDistinct(int[] nums) {
        if (nums == null || nums.length < 2) {
            return Integer.MIN_VALUE;
        }
        long first = Long.MIN_VALUE;
        long second = Long.MIN_VALUE;
        for (int num : nums) {
            if (num > first) {
                second = first;
                first = num;
            } else if (num < first && num > second) {
                second = num;
            }
        }
        return second == Long.MIN_VALUE ? Integer.MIN_VALUE : (int) second;
    }

    // P3.2: Compaction Pointer — O(n) Time | O(1) Space
    public static void moveZeroes(int[] nums) {
        if (nums == null) return;
        int write = 0;
        for (int num : nums) {
            if (num != 0) {
                nums[write++] = num;
            }
        }
        while (write < nums.length) {
            nums[write++] = 0;
        }
    }

    // P3.3: Three-Reverse Trick — O(n) Time | O(1) Space
    public static void rotateRight(int[] nums, int k) {
        if (nums == null || nums.length <= 1) return;
        int n = nums.length;
        k %= n;
        if (k == 0) return;
        reverse(nums, 0, n - 1);
        reverse(nums, 0, k - 1);
        reverse(nums, k, n - 1);
    }

    private static void reverse(int[] nums, int left, int right) {
        while (left < right) {
            int tmp = nums[left];
            nums[left++] = nums[right];
            nums[right--] = tmp;
        }
    }

    // P3.4: Kadane's Algorithm — O(n) Time | O(1) Space
    public static int maxSubarraySum(int[] nums) {
        int current = nums[0];
        int best = nums[0];
        for (int i = 1; i < nums.length; i++) {
            current = Math.max(nums[i], current + nums[i]);
            best = Math.max(best, current);
        }
        return best;
    }

    // P3.5: XOR Trick (immune to integer overflow!) — O(n) Time | O(1) Space
    public static int missingNumber(int[] nums) {
        int xor = nums.length;
        for (int i = 0; i < nums.length; i++) {
            xor ^= i ^ nums[i];
        }
        return xor;
    }

    public static TestRunner.ModuleResult verify(boolean verbose) {
        return new TestRunner.Suite("SOLUTIONS 03: Arrays & Two Pointers")
                .problem("P3.1", "secondLargestDistinct", "", v -> {
                    v.assertEquals("[10,20,20,8,15]", 15, secondLargestDistinct(new int[]{10, 20, 20, 8, 15}));
                    v.assertEquals("[5,5,5]", Integer.MIN_VALUE, secondLargestDistinct(new int[]{5, 5, 5}));
                })
                .problem("P3.2", "moveZeroes", "", v -> {
                    int[] a = {0, 1, 0, 3, 12};
                    moveZeroes(a);
                    v.assertEquals("moveZeroes", new int[]{1, 3, 12, 0, 0}, a);
                })
                .problem("P3.3", "rotateRight", "", v -> {
                    int[] a = {1, 2, 3, 4, 5, 6, 7};
                    rotateRight(a, 3);
                    v.assertEquals("rotateRight", new int[]{5, 6, 7, 1, 2, 3, 4}, a);
                })
                .problem("P3.4", "maxSubarraySum", "", v -> {
                    v.assertEquals("Kadane", 6, maxSubarraySum(new int[]{-2, 1, -3, 4, -1, 2, 1, -5, 4}));
                    v.assertEquals("All negative", -2, maxSubarraySum(new int[]{-5, -2, -9}));
                })
                .problem("P3.5", "missingNumber", "", v -> {
                    v.assertEquals("missing 2", 2, missingNumber(new int[]{3, 0, 1}));
                })
                .run(verbose);
    }

    public static void main(String[] args) {
        verify(true);
    }
}
