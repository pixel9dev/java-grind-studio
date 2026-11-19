package javagrind.solutions;

import javagrind.grind.G07_LinkedListMatrixAndDpGrind;
import javagrind.grind.G07_LinkedListMatrixAndDpGrind.ListNode;
import javagrind.util.TestRunner;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * REFERENCE SOLUTIONS FOR GRIND 07 (Linked Lists, Matrices & Intro DP).
 */
public class S07_LinkedListMatrixAndDpSolutions {

    // P7.1: Move '#' to Front — O(n) Time | O(n) Space
    public static String moveHashesToFront(String s) {
        if (s == null || s.isEmpty()) return s == null ? "" : s;
        StringBuilder hashes = new StringBuilder();
        StringBuilder letters = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch == '#') {
                hashes.append('#');
            } else {
                letters.append(ch);
            }
        }
        return hashes.append(letters).toString();
    }

    // P7.2: Count Borrow Operations — O(log10(num1)) Time | O(1) Space
    public static int countBorrows(int num1, int num2) {
        if (num1 < num2) {
            return -1;
        }
        int borrows = 0;
        int borrowCarry = 0;
        while (num1 > 0 || num2 > 0) {
            int d1 = (num1 % 10) - borrowCarry;
            int d2 = num2 % 10;
            if (d1 < d2) {
                borrows++;
                borrowCarry = 1;
            } else {
                borrowCarry = 0;
            }
            num1 /= 10;
            num2 /= 10;
        }
        return borrows;
    }

    // P7.3: Sorted Unique Duplicates — O(n log d) Time | O(n) Space
    public static List<Integer> findDuplicates(int[] nums) {
        if (nums == null) return List.of();
        Set<Integer> seen = new HashSet<>();
        Set<Integer> duplicates = new TreeSet<>();
        for (int n : nums) {
            if (!seen.add(n)) {
                duplicates.add(n);
            }
        }
        return new ArrayList<>(duplicates);
    }

    // P7.4: Iterative Linked List Reversal — O(n) Time | O(1) Space
    public static ListNode reverseLinkedList(ListNode head) {
        ListNode prev = null;
        ListNode curr = head;
        while (curr != null) {
            ListNode nextTemp = curr.next;
            curr.next = prev;
            prev = curr;
            curr = nextTemp;
        }
        return prev;
    }

    // P7.5: Matrix Diagonal Sum — O(n) Time | O(1) Space
    public static int matrixDiagonalSum(int[][] mat) {
        if (mat == null || mat.length == 0) return 0;
        int n = mat.length;
        int sum = 0;
        for (int i = 0; i < n; i++) {
            sum += mat[i][i];
            sum += mat[i][n - 1 - i];
        }
        if (n % 2 == 1) {
            sum -= mat[n / 2][n / 2];
        }
        return sum;
    }

    // P7.6: Space-Optimized 1D DP — O(n) Time | O(1) Space
    public static int maxNonAdjacentSum(int[] nums) {
        if (nums == null || nums.length == 0) return 0;
        int prev2 = 0;
        int prev1 = 0;
        for (int val : nums) {
            int curr = Math.max(prev1, prev2 + val);
            prev2 = prev1;
            prev1 = curr;
        }
        return prev1;
    }

    public static TestRunner.ModuleResult verify(boolean verbose) {
        return new TestRunner.Suite("SOLUTIONS 07: Linked Lists, Matrices & Intro DP")
                .problem("P7.1", "moveHashesToFront", "", v -> {
                    v.assertEquals("Move#Hash#to#Front", "###MoveHashtoFront",
                            moveHashesToFront("Move#Hash#to#Front"));
                })
                .problem("P7.2", "countBorrows", "", v -> {
                    v.assertEquals("754 - 658", 2, countBorrows(754, 658));
                    v.assertEquals("500 - 1", 2, countBorrows(500, 1));
                    v.assertEquals("100 - 200", -1, countBorrows(100, 200));
                })
                .problem("P7.3", "findDuplicates", "", v -> {
                    v.assertEquals("dupes", List.of(2, 3),
                            findDuplicates(new int[]{4, 3, 2, 7, 8, 2, 3, 1, 2}));
                })
                .problem("P7.4", "reverseLinkedList", "", v -> {
                    v.assertEquals("reverse", List.of(4, 3, 2, 1),
                            G07_LinkedListMatrixAndDpGrind.toJavaList(
                                    reverseLinkedList(G07_LinkedListMatrixAndDpGrind.buildList(1, 2, 3, 4))));
                })
                .problem("P7.5", "matrixDiagonalSum", "", v -> {
                    v.assertEquals("3x3", 25, matrixDiagonalSum(new int[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}));
                })
                .problem("P7.6", "maxNonAdjacentSum", "", v -> {
                    v.assertEquals("[2,7,9,3,1]", 12, maxNonAdjacentSum(new int[]{2, 7, 9, 3, 1}));
                })
                .run(verbose);
    }

    public static void main(String[] args) {
        verify(true);
    }
}
