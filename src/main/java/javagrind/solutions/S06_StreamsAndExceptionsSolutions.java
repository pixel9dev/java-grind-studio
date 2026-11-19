package javagrind.solutions;

import javagrind.grind.G06_StreamsAndExceptionsGrind.Employee;
import javagrind.util.TestRunner;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * REFERENCE SOLUTIONS FOR GRIND 06 (Streams, Collectors & Exception Safety).
 */
public class S06_StreamsAndExceptionsSolutions {

    public static List<String> cleanAndSortTags(List<String> rawTags) {
        if (rawTags == null) return List.of();
        return rawTags.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> s.length() >= 3)
                .map(s -> s.toUpperCase(Locale.ROOT))
                .distinct()
                .sorted()
                .toList();
    }

    public static Map<String, Integer> maxSalaryByDept(List<Employee> employees) {
        if (employees == null) return Map.of();
        return employees.stream()
                .collect(Collectors.toMap(
                        Employee::dept,
                        Employee::salary,
                        Integer::max
                ));
    }

    public static long sumOfSquaresOfEvens(List<Integer> nums) {
        if (nums == null) return 0L;
        return nums.stream()
                .filter(Objects::nonNull)
                .filter(n -> n % 2 == 0)
                .mapToLong(n -> (long) n * n)
                .sum();
    }

    public static int parsePositiveScore(String rawInput) {
        if (rawInput == null) {
            return -1;
        }
        try {
            int score = Integer.parseInt(rawInput.trim());
            return (score >= 1 && score <= 100) ? score : -1;
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    public static TestRunner.ModuleResult verify(boolean verbose) {
        return new TestRunner.Suite("SOLUTIONS 06: Streams & Exception Safety")
                .problem("P6.1", "cleanAndSortTags", "", v -> {
                    v.assertEquals("tags", List.of("JAVA", "SPRING", "SQL"),
                            cleanAndSortTags(Arrays.asList(" java ", null, "ai", "Spring", "JAVA", "sql ")));
                })
                .problem("P6.2", "maxSalaryByDept", "", v -> {
                    List<Employee> emps = List.of(
                            new Employee("Asha", "Eng", 120),
                            new Employee("Vikram", "Eng", 150),
                            new Employee("Raj", "QA", 110)
                    );
                    v.assertEquals("maxSalaryByDept", Map.of("Eng", 150, "QA", 110), maxSalaryByDept(emps));
                })
                .problem("P6.3", "sumOfSquaresOfEvens", "", v -> {
                    v.assertEquals("evens", 24L, sumOfSquaresOfEvens(Arrays.asList(1, 2, 3, 4, -2, null)));
                })
                .problem("P6.4", "parsePositiveScore", "", v -> {
                    v.assertEquals("85", 85, parsePositiveScore(" 85 "));
                    v.assertEquals("bad", -1, parsePositiveScore("85pts"));
                })
                .run(verbose);
    }

    public static void main(String[] args) {
        verify(true);
    }
}
