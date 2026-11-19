package javagrind.grind;

import javagrind.util.TestRunner;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * ==========================================================================
 * GRIND 06: Java 8–17 Stream API & Safe Exception Handling (4 Problems)
 * ==========================================================================
 * Prerequisite: Run & read (L05_ExceptionsAndStreams.java:1) first!
 * Stuck for >15 mins? Check (S06_StreamsAndExceptionsSolutions.java:1).
 */
public class G06_StreamsAndExceptionsGrind {

    public record Employee(String name, String dept, int salary) {}

    /**
     * P6.1: Stream Pipeline — Clean, Filter, Uppercase, Deduplicate & Sort Tags
     * Given `rawTags` (which may contain `null` elements and extra spaces!):
     * 1. Ignore `null` elements (`Objects::nonNull`)
     * 2. `trim()` each string
     * 3. Keep only strings with `length() >= 3`
     * 4. Convert to `toUpperCase()`
     * 5. Deduplicate (`distinct()`) and sort alphabetically (`sorted()`)!
     *
     * Example:
     *   [" java ", null, "ai", "Spring", "JAVA", "sql "] -> ["JAVA", "SPRING", "SQL"]
     */
    public static List<String> cleanAndSortTags(List<String> rawTags) {
        return TestRunner.todo();
    }

    /**
     * P6.2: Collectors.groupingBy — Highest Salary per Department
     * Return a `Map<String, Integer>` mapping each department name to the
     * MAXIMUM salary among employees in that department!
     *
     * Hint: `Collectors.toMap(Employee::dept, Employee::salary, Integer::max)`
     *       OR `Collectors.groupingBy(...)`!
     */
    public static Map<String, Integer> maxSalaryByDept(List<Employee> employees) {
        return TestRunner.todo();
    }

    /**
     * P6.3: Primitive Stream Reduction — Sum of Squares of Even Numbers
     * Return the `long` sum of `(long) n * n` for every non-null even number in `nums`.
     * Note: Negative even numbers like `-4` ARE even (`(-4) % 2 == 0` -> square is `16`)!
     *
     * Example:
     *   [1, 2, 3, 4, -2] -> 4 + 16 + 4 = 24L
     */
    public static long sumOfSquaresOfEvens(List<Integer> nums) {
        return TestRunner.todo();
    }

    /**
     * P6.4: Defensive Parsing & Exception Handling
     * Parse `rawInput` into a valid exam score in `[1, 100]` (inclusive):
     * - If `rawInput == null`, return `-1`.
     * - Trim whitespace first (`rawInput.trim()`).
     * - If parsing fails (`NumberFormatException`) OR score < 1 OR score > 100, return `-1`.
     * - Otherwise return the score!
     */
    public static int parsePositiveScore(String rawInput) {
        return TestRunner.todo();
    }

    // ==================== DO NOT EDIT BELOW THIS LINE (TEST HARNESS) ====================

    public static TestRunner.ModuleResult runTests(boolean verbose) {
        return new TestRunner.Suite("GRIND 06: Streams, Collectors & Exception Safety")
                .problem("P6.1", "cleanAndSortTags(List<String>)",
                        "filter(Objects::nonNull).map(String::trim).filter(s -> s.length() >= 3).map(String::toUpperCase).distinct().sorted().toList()",
                        v -> {
                            List<String> input = Arrays.asList(" java ", null, "ai", "Spring", "JAVA", "sql ", "  ");
                            v.assertEquals("mixed tags", List.of("JAVA", "SPRING", "SQL"), cleanAndSortTags(input));
                        })
                .problem("P6.2", "maxSalaryByDept(List<Employee>)",
                        "`employees.stream().collect(Collectors.toMap(Employee::dept, Employee::salary, Integer::max))`",
                        v -> {
                            List<Employee> emps = List.of(
                                    new Employee("Asha", "Eng", 120),
                                    new Employee("Vikram", "Eng", 150),
                                    new Employee("Nisha", "QA", 95),
                                    new Employee("Raj", "QA", 110)
                            );
                            v.assertEquals("max by dept", Map.of("Eng", 150, "QA", 110), maxSalaryByDept(emps));
                        })
                .problem("P6.3", "sumOfSquaresOfEvens(List<Integer>)",
                        "filter(Objects::nonNull).filter(n -> n % 2 == 0).mapToLong(n -> (long) n * n).sum()",
                        v -> {
                            v.assertEquals("[1,2,3,4,-2,null]", 24L,
                                    sumOfSquaresOfEvens(Arrays.asList(1, 2, 3, 4, -2, null)));
                            v.assertEquals("all odds", 0L, sumOfSquaresOfEvens(List.of(1, 3, 5)));
                        })
                .problem("P6.4", "parsePositiveScore(String)",
                        "Check null, trim, `Integer.parseInt` inside `try-catch (NumberFormatException e)`, validate 1..100.",
                        v -> {
                            v.assertEquals("\" 85 \"", 85, parsePositiveScore(" 85 "));
                            v.assertEquals("\"100\"", 100, parsePositiveScore("100"));
                            v.assertEquals("\"0\"", -1, parsePositiveScore("0"));
                            v.assertEquals("\"105\"", -1, parsePositiveScore("105"));
                            v.assertEquals("\"85pts\"", -1, parsePositiveScore("85pts"));
                            v.assertEquals("null", -1, parsePositiveScore(null));
                        })
                .run(verbose);
    }

    public static void main(String[] args) {
        runTests(true);
    }
}
