package javagrind.learn;

import javagrind.util.TestRunner;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * MODULE 5 (LEARN): Exception Handling, Functional Interfaces, Stream API & Optional
 *
 * Java 8+ Streams & Exceptions are asked in virtually 100% of Java technical interviews!
 */
public class L05_ExceptionsAndStreams {

    // Custom Unchecked Exception (extends RuntimeException)
    static class InvalidAgeException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        InvalidAgeException(String message) {
            super(message);
        }
    }

    record Emp(String name, String dept, int salary) {}

    public static void main(String[] args) {
        TestRunner.printBanner("LEARN 05: Exceptions, Lambdas, Stream API & Optional");

        exceptionHierarchyAndFinally();
        streamPipelineEssentials();
        groupingAndAggregatingStreams();
        optionalNullSafety();

        System.out.println("\nReady to practice Streams & Exceptions? Click -> (G06_StreamsAndExceptionsGrind.java:1)");
    }

    private static void exceptionHierarchyAndFinally() {
        TestRunner.printSection("1. Checked vs Unchecked Exceptions & `finally` Execution Guarantee");
        // Throwable
        //  ├── Error (JVM fatal: StackOverflowError, OutOfMemoryError — do NOT catch)
        //  └── Exception
        //       ├── Checked (IOException, SQLException — compiler forces try/catch or `throws`)
        //       └── RuntimeException / Unchecked (NullPointerException, IllegalArgumentException)

        try {
            validateCandidateAge(16);
        } catch (InvalidAgeException ex) {
            System.out.println("Caught custom exception -> " + ex.getMessage());
        } finally {
            // ALWAYS runs whether exception occurred or not (unless System.exit() / JVM crash)!
            System.out.println("`finally` block executed cleanly (used for closing/cleanup).");
        }
    }

    private static void validateCandidateAge(int age) {
        if (age < 18) {
            throw new InvalidAgeException("Candidate must be >= 18, got: " + age);
        }
    }

    private static void streamPipelineEssentials() {
        TestRunner.printSection("2. Stream Pipeline: Source -> Intermediate (Lazy) -> Terminal (Triggers work)");
        List<Integer> nums = List.of(5, 2, 8, 2, 9, 4, 10, 3);

        // Task: Take unique even numbers, square them, sort descending!
        List<Integer> result = nums.stream()
                .distinct()                             // Intermediate: remove duplicates
                .filter(n -> n % 2 == 0)                // Intermediate: Predicate<Integer>
                .map(n -> n * n)                        // Intermediate: Function<Integer, Integer>
                .sorted(Comparator.reverseOrder())      // Intermediate: sort descending
                .toList();                              // Terminal (Java 16+ immutable List!)

        System.out.println("Unique even squares (desc): " + result);
    }

    private static void groupingAndAggregatingStreams() {
        TestRunner.printSection("3. INTERVIEW CLASSIC: Collectors.groupingBy, counting & Average/Max");
        List<Emp> team = List.of(
                new Emp("Ananya", "Backend", 120_000),
                new Emp("Rohan", "Backend", 95_000),
                new Emp("Kabir", "Frontend", 105_000),
                new Emp("Meera", "Frontend", 115_000),
                new Emp("Dev", "Backend", 140_000)
        );

        // Count employees per department
        Map<String, Long> headcount = team.stream()
                .collect(Collectors.groupingBy(Emp::dept, Collectors.counting()));
        System.out.println("Headcount by dept : " + headcount);

        // Second-highest salary overall (SUPER COMMON INTERVIEW QUESTION!)
        Optional<Emp> secondHighest = team.stream()
                .sorted(Comparator.comparingInt(Emp::salary).reversed())
                .skip(1)
                .findFirst();
        System.out.println("2nd highest earner: " + secondHighest.orElse(null));
    }

    private static void optionalNullSafety() {
        TestRunner.printSection("4. Optional<T>: Eliminating NullPointerException Cleanly");
        Optional<String> present = Optional.of("Java 17");
        Optional<String> empty = Optional.empty(); // Use Optional.ofNullable(x) when x might be null!

        System.out.println("present.map(Upper) : " + present.map(String::toUpperCase).orElse("DEFAULT"));
        System.out.println("empty.orElse(...)  : " + empty.orElse("Fallback Value"));
    }
}
