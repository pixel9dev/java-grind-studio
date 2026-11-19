package javagrind.grind;

import javagrind.util.TestRunner;
import java.util.ArrayList;
import java.util.List;

/**
 * ==========================================================================
 * GRIND 07: Linked Lists, Matrices, Digit Math & Intro DP (6 Problems)
 * ==========================================================================
 * High-frequency OA and live screen-share coding problems.
 * Stuck for >15 mins? Check (S07_LinkedListMatrixAndDpSolutions.java:1).
 */
public class G07_LinkedListMatrixAndDpGrind {

    // Singly Linked List Node for P7.4
    public static class ListNode {
        public int val;
        public ListNode next;

        public ListNode(int val) { this.val = val; }
        public ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    /**
     * P7.1: Move All Hashes ('#') to the Front
     * Given a string `s`, move all `'#'` characters to the beginning of the string
     * while keeping the exact relative order of all non-'#' characters!
     *
     * Examples:
     *   moveHashesToFront("Move#Hash#to#Front") -> "###MoveHashtoFront"
     *   moveHashesToFront("BackendJava")        -> "BackendJava"
     *   moveHashesToFront("###")                -> "###"
     */
    public static String moveHashesToFront(String s) {
        return TestRunner.todo();
    }

    /**
     * P7.2: Count Borrow Operations in Subtraction
     * Given two non-negative integers `num1` and `num2`:
     * - If `num1 < num2`, subtraction is not possible without going negative -> return `-1`.
     * - Otherwise simulate column-by-column subtraction from rightmost digit to leftmost
     *   and return the TOTAL number of times a borrow was needed!
     *
     * Examples:
     *   countBorrows(754, 658) -> 2
     *     - Units: 4 < 8 -> borrow 1 (14 - 8 = 6). Tens of num1 becomes 5 - 1 = 4.
     *     - Tens : 4 < 5 -> borrow 1 (14 - 5 = 9). Hundreds of num1 becomes 7 - 1 = 6.
     *     - Hunds: 6 >= 6 -> no borrow. Total borrows = 2!
     *   countBorrows(500, 1)   -> 2  (0<1 borrows, then 0-1=-1<0 borrows!)
     *   countBorrows(100, 200) -> -1 (num1 < num2)
     */
    public static int countBorrows(int num1, int num2) {
        return TestRunner.todo();
    }

    /**
     * P7.3: Find Duplicate Elements (Sorted Unique Output)
     * Return a sorted `List<Integer>` of every distinct number that appears
     * AT LEAST TWICE in `nums`. If none repeat, return an empty list.
     *
     * Examples:
     *   findDuplicates(new int[]{4, 3, 2, 7, 8, 2, 3, 1, 2}) -> [2, 3]
     *   findDuplicates(new int[]{1, 2, 3})                   -> []
     */
    public static List<Integer> findDuplicates(int[] nums) {
        return TestRunner.todo();
    }

    /**
     * P7.4: Reverse a Singly Linked List (Live Coding Classic!)
     * Reverse the linked list starting at `head` in-place (O(1) extra space)
     * and return the new head!
     *
     * Example:
     *   1 -> 2 -> 3 -> 4 -> null   becomes   4 -> 3 -> 2 -> 1 -> null
     */
    public static ListNode reverseLinkedList(ListNode head) {
        return TestRunner.todo();
    }

    /**
     * P7.5: Square Matrix Diagonal Sum
     * Given an `n x n` square matrix `mat`, return the sum of all elements on the
     * primary diagonal (`mat[i][i]`) AND the secondary diagonal (`mat[i][n - 1 - i]`).
     * IMPORTANT: If `n` is odd, count the center element ONLY ONCE!
     *
     * Example:
     *   [[1, 2, 3],
     *    [4, 5, 6],   -> (1 + 5 + 9) + (3 + 7) = 25  (5 counted once!)
     *    [7, 8, 9]]
     */
    public static int matrixDiagonalSum(int[][] mat) {
        return TestRunner.todo();
    }

    /**
     * P7.6: Max Non-Adjacent Sum (Intro 1D Dynamic Programming)
     * Given non-negative values `nums`, return the maximum sum you can pick such
     * that NO TWO picked elements are adjacent (`i` and `i + 1`).
     *
     * Examples:
     *   maxNonAdjacentSum(new int[]{2, 7, 9, 3, 1}) -> 12  (pick 2 + 9 + 1)
     *   maxNonAdjacentSum(new int[]{2, 1, 1, 2})    -> 4   (pick 2 + 2)
     */
    public static int maxNonAdjacentSum(int[] nums) {
        return TestRunner.todo();
    }

    // ==================== DO NOT EDIT BELOW THIS LINE (TEST HARNESS) ====================

    public static ListNode buildList(int... vals) {
        ListNode dummy = new ListNode(0);
        ListNode tail = dummy;
        for (int v : vals) {
            tail.next = new ListNode(v);
            tail = tail.next;
        }
        return dummy.next;
    }

    public static List<Integer> toJavaList(ListNode head) {
        List<Integer> out = new ArrayList<>();
        int guard = 0;
        while (head != null && guard++ < 1000) {
            out.add(head.val);
            head = head.next;
        }
        return out;
    }

    public static TestRunner.ModuleResult runTests(boolean verbose) {
        return new TestRunner.Suite("GRIND 07: Linked Lists, Matrices & Intro DP")
                .problem("P7.1", "moveHashesToFront(String s)",
                        "Count '#' first, append that many '#' to StringBuilder, then append all non-'#' chars!",
                        v -> {
                            v.assertEquals("Move#Hash#to#Front", "###MoveHashtoFront",
                                    moveHashesToFront("Move#Hash#to#Front"));
                            v.assertEquals("no hash", "BackendJava", moveHashesToFront("BackendJava"));
                            v.assertEquals("all hashes", "###", moveHashesToFront("###"));
                            v.assertEquals("trailing#", "#abc", moveHashesToFront("abc#"));
                        })
                .problem("P7.2", "countBorrows(int num1, int num2)",
                        "If num1 < num2 return -1. Loop while num1 > 0 || num2 > 0 tracking `carryBorrow` (0 or 1).",
                        v -> {
                            v.assertEquals("754 - 658", 2, countBorrows(754, 658));
                            v.assertEquals("500 - 1", 2, countBorrows(500, 1));
                            v.assertEquals("654 - 321 (no borrow)", 0, countBorrows(654, 321));
                            v.assertEquals("100 - 200 (impossible)", -1, countBorrows(100, 200));
                            v.assertEquals("1000 - 9", 3, countBorrows(1000, 9));
                        })
                .problem("P7.3", "findDuplicates(int[] nums)",
                        "Use `Set<Integer> seen` + `Set<Integer> dupes = new TreeSet<>()` (auto-sorted & unique!).",
                        v -> {
                            v.assertEquals("[4,3,2,7,8,2,3,1,2]", List.of(2, 3),
                                    findDuplicates(new int[]{4, 3, 2, 7, 8, 2, 3, 1, 2}));
                            v.assertEquals("[1,2,3]", List.of(), findDuplicates(new int[]{1, 2, 3}));
                            v.assertEquals("[-1,5,-1,5]", List.of(-1, 5), findDuplicates(new int[]{-1, 5, -1, 5}));
                        })
                .problem("P7.4", "reverseLinkedList(ListNode head)",
                        "`ListNode prev = null, curr = head;` In loop: `next = curr.next; curr.next = prev; prev = curr; curr = next;`",
                        v -> {
                            v.assertEquals("1->2->3->4", List.of(4, 3, 2, 1),
                                    toJavaList(reverseLinkedList(buildList(1, 2, 3, 4))));
                            v.assertEquals("single [9]", List.of(9),
                                    toJavaList(reverseLinkedList(buildList(9))));
                            v.assertEquals("empty null", List.of(),
                                    toJavaList(reverseLinkedList(null)));
                        })
                .problem("P7.5", "matrixDiagonalSum(int[][] mat)",
                        "Add `mat[i][i] + mat[i][n-1-i]` for each row `i`; if `n % 2 == 1`, subtract `mat[n/2][n/2]`!",
                        v -> {
                            int[][] m3 = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
                            int[][] m2 = {{1, 1}, {1, 1}};
                            int[][] m1 = {{7}};
                            v.assertEquals("3x3 matrix", 25, matrixDiagonalSum(m3));
                            v.assertEquals("2x2 matrix", 4, matrixDiagonalSum(m2));
                            v.assertEquals("1x1 matrix", 7, matrixDiagonalSum(m1));
                        })
                .problem("P7.6", "maxNonAdjacentSum(int[] nums)",
                        "Track `prev2 = 0, prev1 = 0`; for each `x`: `int cur = Math.max(prev1, prev2 + x); prev2 = prev1; prev1 = cur;`",
                        v -> {
                            v.assertEquals("[2,7,9,3,1]", 12, maxNonAdjacentSum(new int[]{2, 7, 9, 3, 1}));
                            v.assertEquals("[2,1,1,2]", 4, maxNonAdjacentSum(new int[]{2, 1, 1, 2}));
                            v.assertEquals("[10]", 10, maxNonAdjacentSum(new int[]{10}));
                            v.assertEquals("empty", 0, maxNonAdjacentSum(new int[]{}));
                        })
                .run(verbose);
    }

    public static void main(String[] args) {
        runTests(true);
    }
}
