package javagrind.learn;

import javagrind.util.TestRunner;
import java.util.Arrays;

/**
 * MODULE 1 (LEARN): Java Basics, Primitive Types, Arrays, Loops & Pass-By-Value
 *
 * HOW TO USE: Click the green ▶ icon next to `main` (or press Shift+F10 in IntelliJ)
 * and read the code alongside the console output!
 */
public class L01_BasicsTypesAndMemory {

    public static void main(String[] args) {
        TestRunner.printBanner("LEARN 01: Java Basics, Types, Arrays & Pass-By-Value");

        primitivesAndRanges();
        divisionAndCastingTraps();
        wrapperIntegerCacheTrap();
        arraysAndLoops();
        passByValueProof();

        System.out.println("\nReady to practice? Click -> (G01_WarmupAndMathGrind.java:1)");
    }

    private static void primitivesAndRanges() {
        TestRunner.printSection("1. The 8 Primitive Types (Stored directly on Stack / Object memory)");
        byte b = 127;                    // 1 byte : -128 to 127
        short s = 32_000;                // 2 bytes: -32,768 to 32,767
        int i = 2_000_000_000;           // 4 bytes: ~ -2.14B to +2.14B ( Integer.MAX_VALUE = 2,147,483,647 )
        long big = 9_000_000_000L;       // 8 bytes: MUST add 'L' suffix for literals > int!
        float f = 3.14f;                 // 4 bytes: MUST add 'f' suffix
        double d = 3.141592653589793;    // 8 bytes: Default decimal type
        char ch = 'A';                   // 2 bytes: Unsigned UTF-16 code unit (65 for 'A', 97 for 'a', 48 for '0')
        boolean flag = true;             // true or false ONLY (NEVER 0 or 1 like C/C++!)

        System.out.println("byte=" + b + ", short=" + s + ", int=" + i + ", long=" + big);
        System.out.println("float=" + f + ", double=" + d + ", char=" + ch + " (ASCII=" + (int) ch + "), boolean=" + flag);
    }

    private static void divisionAndCastingTraps() {
        TestRunner.printSection("2. DRIVE TRAP: Integer Division, Overflow & String Concatenation Order");

        // Trap A: int / int ALWAYS truncates toward zero!
        System.out.println("5 / 2       = " + (5 / 2) + "    (int / int drops decimal!)");
        System.out.println("5.0 / 2     = " + (5.0 / 2) + "  (double promotion)");
        System.out.println("(double)(5/2) = " + ((double) (5 / 2)) + "  (TOO LATE! 5/2 already became 2)");

        // Trap B: Left-to-right evaluation with '+'
        System.out.println("10 + 20 + \"Java\" = " + (10 + 20 + "Java") + "  (30 + \"Java\")");
        System.out.println("\"Java\" + 10 + 20 = " + ("Java" + 10 + 20) + "  (\"Java10\" + 20)");

        // Trap C: Midpoint overflow bug in Binary Search!
        int low = 1_500_000_000;
        int high = 1_600_000_000;
        int buggyMid = (low + high) / 2;           // OVERFLOWS int! Becomes negative!
        int safeMid = low + (high - low) / 2;      // ALWAYS safe!
        System.out.println("Buggy (low+high)/2 = " + buggyMid + " vs Safe low+(high-low)/2 = " + safeMid);
    }

    private static void wrapperIntegerCacheTrap() {
        TestRunner.printSection("3. DRIVE TRAP: Autoboxing & Integer Cache (-128 to 127)");
        Integer x1 = 127, x2 = 127;
        Integer y1 = 128, y2 = 128;

        // Java caches Integer objects for -128..127 in Integer.valueOf()!
        // Beyond 127, new Integer objects are created, so `==` compares object addresses!
        System.out.println("Integer 127 == 127 : " + (x1 == x2) + "  (Cached singleton reference)");
        System.out.println("Integer 128 == 128 : " + (y1 == y2) + " (Different objects on Heap!)");
        System.out.println("Integer 128.equals : " + y1.equals(y2) + "  (RULE: Always use .equals() on Wrapper objects!)");
    }

    private static void arraysAndLoops() {
        TestRunner.printSection("4. Arrays, Enhanced For-Loop & Modern Switch Expression (Java 17)");
        int[] scores = {85, 92, 78, 95}; // Fixed size once created! Length is `.length` (field, NO parentheses)

        // Classic index loop (needed when modifying array or needing index i)
        int sum = 0;
        for (int i = 0; i < scores.length; i++) {
            sum += scores[i];
        }

        // Enhanced for-each loop (cleanest for read-only traversal)
        int max = Integer.MIN_VALUE;
        for (int score : scores) {
            max = Math.max(max, score);
        }

        Arrays.sort(scores); // Sorts in-place in O(n log n)
        System.out.println("Sorted scores: " + Arrays.toString(scores) + " | Sum=" + sum + " | Max=" + max);

        // Java 14+ Switch Expression (No accidental fall-through!)
        String dayType = switch ("SAT") {
            case "MON", "TUE", "WED", "THU", "FRI" -> "Weekday";
            case "SAT", "SUN" -> "Weekend";
            default -> "Invalid";
        };
        System.out.println("Switch expression for SAT -> " + dayType);
    }

    private static void passByValueProof() {
        TestRunner.printSection("5. INTERVIEW FAVORITE: 'Is Java Pass-by-Reference?' -> NO! Strictly Pass-by-Value!");
        int num = 10;
        int[] box = {10};
        tryModify(num, box);
        System.out.println("Primitive `num` after method : " + num + " (Unchanged! Copy of value was passed)");
        System.out.println("Array `box[0]` after method  : " + box[0] + " (Mutated! Copy of reference pointed to same Heap array)");
    }

    private static void tryModify(int n, int[] arr) {
        n = 999;          // Only changes local copy
        arr[0] = 999;     // Mutates the shared Heap object
        arr = new int[]{1}; // Reassigning local reference does NOT affect caller's `box`!
    }
}
