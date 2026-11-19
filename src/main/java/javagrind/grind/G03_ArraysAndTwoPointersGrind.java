package javagrind.grind;

import javagrind.util.TestRunner;

/**
 * ==========================================================================
 * GRIND 03: Arrays, In-Place Two Pointers & Kadane's Algorithm (5 Problems)
 * ==========================================================================
 * Prerequisite: Run & read (L01_BasicsTypesAndMemory.java:1) first!
 * Stuck for >15 mins? Check (S03_ArraysAndTwoPointersSolutions.java:1).
 */
public class G03_ArraysAndTwoPointersGrind {

    /**
     * P3.1: Second Largest Distinct Element (Single Pass O(n))
     * Return the 2nd largest DISTINCT value in `nums` WITHOUT sorting!
     * If fewer than 2 distinct values exist (e.g. `{7, 7, 7}` or `{5}`),
     * return `Integer.MIN_VALUE`.
     *
     * Examples:
     *   secondLargestDistinct(new int[]{10, 20, 20, 8, 15}) -> 15
     *   secondLargestDistinct(new int[]{5, 5, 5})           -> Integer.MIN_VALUE
     *   secondLargestDistinct(new int[]{-10, -3, -20})      -> -10
     */
    public static int secondLargestDistinct(int[] nums) {
        return TestRunner.todo();
    }

    /**
     * P3.2: Move Zeroes to End (In-Place, O(1) Space)
     * Move all `0`s to the end of `nums` while maintaining the relative order
     * of the non-zero elements. Modify `nums` directly in-place!
     *
     * Example:
     *   nums = [0, 1, 0, 3, 12] -> becomes [1, 3, 12, 0, 0]
     */
    public static void moveZeroes(int[] nums) {
        TestRunner.todo();
    }

    /**
     * P3.3: Rotate Array Right by K Steps (In-Place 3-Reverse Trick!)
     * Rotate `nums` to the right by `k` steps (`k >= 0`) in O(n) time, O(1) space.
     * Watch out: `k` can be larger than `nums.length`!
     *
     * Example:
     *   nums = [1, 2, 3, 4, 5, 6, 7], k = 3 -> [5, 6, 7, 1, 2, 3, 4]
     */
    public static void rotateRight(int[] nums, int k) {
        TestRunner.todo();
    }

    /**
     * P3.4: Maximum Subarray Sum (Kadane's Algorithm — MUST KNOW!)
     * Find the contiguous non-empty subarray with the largest sum and return its sum.
     * Watch out for all-negative arrays like `[-5, -2, -9]` -> answer is `-2` (NOT 0!).
     *
     * Examples:
     *   maxSubarraySum(new int[]{-2, 1, -3, 4, -1, 2, 1, -5, 4}) -> 6  ([4,-1,2,1])
     *   maxSubarraySum(new int[]{-5, -2, -9})                    -> -2
     */
    public static int maxSubarraySum(int[] nums) {
        return TestRunner.todo();
    }

    /**
     * P3.5: Missing Number in Range [0..n]
     * Given `nums` containing `n` distinct numbers in the range `[0, n]`,
     * return the single number in `[0, n]` missing from the array.
     *
     * Examples:
     *   missingNumber(new int[]{3, 0, 1})                   -> 2
     *   missingNumber(new int[]{9,6,4,2,3,5,7,0,1})         -> 8
     */
    public static int missingNumber(int[] nums) {
        return TestRunner.todo();
    }

    // ==================== DO NOT EDIT BELOW THIS LINE (TEST HARNESS) ====================

    public static TestRunner.ModuleResult runTests(boolean verbose) {
        return new TestRunner.Suite("GRIND 03: Arrays & Two Pointers")
                .problem("P3.1", "secondLargestDistinct(int[] nums)",
                        "Track `long first = Long.MIN_VALUE, second = Long.MIN_VALUE;` and skip duplicates `x == first`!",
                        v -> {
                            v.assertEquals("[10,20,20,8,15]", 15, secondLargestDistinct(new int[]{10, 20, 20, 8, 15}));
                            v.assertEquals("[5,5,5]", Integer.MIN_VALUE, secondLargestDistinct(new int[]{5, 5, 5}));
                            v.assertEquals("[42]", Integer.MIN_VALUE, secondLargestDistinct(new int[]{42}));
                            v.assertEquals("[-10,-3,-20]", -10, secondLargestDistinct(new int[]{-10, -3, -20}));
                            v.assertEquals("[5, MIN_VALUE]", Integer.MIN_VALUE,
                                    secondLargestDistinct(new int[]{5, Integer.MIN_VALUE}));
                        })
                .problem("P3.2", "moveZeroes(int[] nums)",
                        "Keep `writeIdx = 0`; copy every non-zero to `nums[writeIdx++]`, then fill rest with 0.",
                        v -> {
                            int[] a1 = {0, 1, 0, 3, 12};
                            moveZeroes(a1);
                            v.assertEquals("[0,1,0,3,12]", new int[]{1, 3, 12, 0, 0}, a1);

                            int[] a2 = {0, 0, 1};
                            moveZeroes(a2);
                            v.assertEquals("[0,0,1]", new int[]{1, 0, 0}, a2);

                            int[] a3 = {4, 2};
                            moveZeroes(a3);
                            v.assertEquals("[4,2]", new int[]{4, 2}, a3);
                        })
                .problem("P3.3", "rotateRight(int[] nums, int k)",
                        "`k %= n;` Reverse(0, n-1), Reverse(0, k-1), Reverse(k, n-1)!",
                        v -> {
                            int[] a1 = {1, 2, 3, 4, 5, 6, 7};
                            rotateRight(a1, 3);
                            v.assertEquals("[1..7], k=3", new int[]{5, 6, 7, 1, 2, 3, 4}, a1);

                            int[] a2 = {-1, -100, 3, 99};
                            rotateRight(a2, 2);
                            v.assertEquals("[-1,-100,3,99], k=2", new int[]{3, 99, -1, -100}, a2);

                            int[] a3 = {1, 2};
                            rotateRight(a3, 5); // k > n!
                            v.assertEquals("[1,2], k=5", new int[]{2, 1}, a3);
                        })
                .problem("P3.4", "maxSubarraySum(int[] nums)",
                        "Start `curr = nums[0], best = nums[0]`; from i=1: `curr = Math.max(nums[i], curr + nums[i])`.",
                        v -> {
                            v.assertEquals("[-2,1,-3,4,-1,2,1,-5,4]", 6,
                                    maxSubarraySum(new int[]{-2, 1, -3, 4, -1, 2, 1, -5, 4}));
                            v.assertEquals("[5,4,-1,7,8]", 23, maxSubarraySum(new int[]{5, 4, -1, 7, 8}));
                            v.assertEquals("[-5,-2,-9]", -2, maxSubarraySum(new int[]{-5, -2, -9}));
                            v.assertEquals("[7]", 7, maxSubarraySum(new int[]{7}));
                        })
                .problem("P3.5", "missingNumber(int[] nums)",
                        "Expected sum `(long) n * (n + 1) / 2` minus actual sum (or XOR `0..n` with `nums[i]`)!",
                        v -> {
                            v.assertEquals("[3,0,1]", 2, missingNumber(new int[]{3, 0, 1}));
                            v.assertEquals("[0,1]", 2, missingNumber(new int[]{0, 1}));
                            v.assertEquals("[9,6,4,2,3,5,7,0,1]", 8,
                                    missingNumber(new int[]{9, 6, 4, 2, 3, 5, 7, 0, 1}));
                        })
                .run(verbose);
    }

    public static void main(String[] args) {
        runTests(true);
    }
}
