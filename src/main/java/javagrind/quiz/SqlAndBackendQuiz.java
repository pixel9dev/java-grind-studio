package javagrind.quiz;

import javagrind.util.TestRunner;
import java.util.List;
import java.util.Scanner;

/**
 * ==========================================================================
 * SQL, DBMS, JDBC, JAVA 8 & REST API QUIZ (10 High-Frequency Questions)
 * ==========================================================================
 * Placement drives heavily test SQL queries, DBMS fundamentals, Java 8
 * Functional Interfaces, and REST/Backend basics alongside Core Java!
 *
 * Click ▶ Run (`Shift+F10`) to test yourself interactively!
 */
public class SqlAndBackendQuiz {

    record Question(String topic, String prompt, List<String> options, char answer, String explanation) {}

    private static final List<Question> QUESTIONS = List.of(
            new Question(
                    "SQL #1: Second Highest Distinct Salary",
                    """
                    Table: Employee(id, name, salary)
                    If two top employees BOTH earn 100,000 and the next earns 85,000,
                    which query reliably returns 85,000 (even with duplicate top salaries)?""",
                    List.of(
                            "A) SELECT salary FROM Employee ORDER BY salary DESC LIMIT 1 OFFSET 1;",
                            "B) SELECT MAX(salary) FROM Employee WHERE salary < (SELECT MAX(salary) FROM Employee);",
                            "C) SELECT TOP 2 salary FROM Employee;",
                            "D) SELECT salary FROM Employee GROUP BY id HAVING MAX(salary);"
                    ),
                    'B',
                    "Option A fails when two people tie at 100,000 (OFFSET 1 still returns 100,000 unless DISTINCT is added!). `WHERE salary < (SELECT MAX(salary) ...)` or `DENSE_RANK() OVER (ORDER BY salary DESC) = 2` handles ties properly!"
            ),
            new Question(
                    "SQL #2: WHERE vs HAVING",
                    """
                    You want departments whose AVERAGE salary exceeds 75,000.
                    Which clause filters aggregated groups AFTER `GROUP BY dept`?""",
                    List.of(
                            "A) WHERE AVG(salary) > 75000 GROUP BY dept",
                            "B) GROUP BY dept HAVING AVG(salary) > 75000",
                            "C) ORDER BY AVG(salary) WHERE dept > 75000",
                            "D) DISTINCT dept WHERE AVG(salary) > 75000"
                    ),
                    'B',
                    "`WHERE` filters individual rows BEFORE grouping and CANNOT contain aggregate functions (`AVG`, `COUNT`, `SUM`). `HAVING` filters groups AFTER `GROUP BY`!"
            ),
            new Question(
                    "DBMS #3: DELETE vs TRUNCATE vs DROP",
                    """
                    Which statement removes ALL rows from a table, keeps the table schema intact,
                    and is classified as a fast DDL operation?""",
                    List.of(
                            "A) DELETE FROM Employee;",
                            "B) DROP TABLE Employee;",
                            "C) TRUNCATE TABLE Employee;",
                            "D) REMOVE ALL FROM Employee;"
                    ),
                    'C',
                    "`DELETE` is DML (row-by-row, supports `WHERE`). `TRUNCATE` is DDL (deallocates data pages quickly, keeps table structure). `DROP` deletes both data AND table structure!"
            ),
            new Question(
                    "SQL #4: PRIMARY KEY vs UNIQUE Constraint",
                    """
                    How many NULL values are allowed in a PRIMARY KEY column vs a UNIQUE column
                    in standard relational SQL?""",
                    List.of(
                            "A) PRIMARY KEY: 1 NULL | UNIQUE: 0 NULLs",
                            "B) PRIMARY KEY: 0 NULLs | UNIQUE: NULLs allowed",
                            "C) Both forbid NULLs",
                            "D) Both allow unlimited NULLs"
                    ),
                    'B',
                    "A `PRIMARY KEY` enforces `UNIQUE + NOT NULL` (at most 1 PK per table, 0 NULLs). A `UNIQUE` key allows NULLs and a table can have multiple `UNIQUE` constraints!"
            ),
            new Question(
                    "SQL #5: Finding Employees WITH NO Department (`LEFT JOIN`)",
                    """
                    Tables: Employee(id, name, dept_id) and Department(id, dept_name)
                    How do you list employees whose `dept_id` has NO match in `Department`?""",
                    List.of(
                            "A) SELECT e.name FROM Employee e INNER JOIN Department d ON e.dept_id = d.id;",
                            "B) SELECT e.name FROM Employee e LEFT JOIN Department d ON e.dept_id = d.id WHERE d.id IS NULL;",
                            "C) SELECT e.name FROM Employee e LEFT JOIN Department d ON e.dept_id = d.id WHERE d.id = NULL;",
                            "D) SELECT e.name FROM Employee e CROSS JOIN Department d;"
                    ),
                    'B',
                    "Two critical rules: (1) `LEFT JOIN` keeps unmatched left rows with NULLs on the right; (2) In SQL, NEVER write `= NULL` (always unknown) - ALWAYS write `IS NULL`!"
            ),
            new Question(
                    "JDBC #6: Preventing SQL Injection in Java",
                    """
                    Why should you always prefer `PreparedStatement` over `Statement` in JDBC?""",
                    List.of(
                            "A) It runs only on document databases",
                            "B) Parameterized `?` placeholders prevent SQL injection & allow query plan pre-compilation",
                            "C) It automatically creates database indexes",
                            "D) `Statement` cannot execute SELECT queries"
                    ),
                    'B',
                    "`PreparedStatement` binds user inputs safely as typed parameters (`ps.setString(1, user)`) so malicious input like `' OR '1'='1` is treated strictly as data, never executable SQL!"
            ),
            new Question(
                    "Java 8 #7: Built-in Functional Interfaces (`java.util.function`)",
                    """
                    Match the 4 core Java 8 Functional Interfaces used in Streams:
                    `filter(...)` takes ____, `map(...)` takes ____, `forEach(...)` takes ____.""",
                    List.of(
                            "A) Function, Predicate, Supplier",
                            "B) Predicate<T>, Function<T,R>, Consumer<T>",
                            "C) Consumer<T>, Supplier<T>, Predicate<T>",
                            "D) Supplier<T>, Consumer<T>, Function<T,R>"
                    ),
                    'B',
                    "Memorize all 4: `Predicate<T>` (`T -> boolean` for `filter`), `Function<T,R>` (`T -> R` for `map`), `Consumer<T>` (`T -> void` for `forEach`), `Supplier<T>` (`() -> T` for `orElseGet`)."
            ),
            new Question(
                    "Java Collections #8: HashMap Java 8 Treeification Threshold",
                    """
                    In Java 8+, when many keys collide into the SAME `HashMap` bucket
                    (assuming table capacity >= 64), what happens at 8 nodes?""",
                    List.of(
                            "A) HashMap throws IllegalStateException",
                            "B) Oldest keys are evicted like an LRU cache",
                            "C) The bucket's linked list converts to a Red-Black Tree (worst-case O(n) -> O(log n))",
                            "D) HashMap converts into a TreeMap"
                    ),
                    'C',
                    "At `TREEIFY_THRESHOLD = 8` (and `MIN_TREEIFY_CAPACITY = 64`), that bucket becomes a balanced Red-Black Tree so hash-collision attacks degrade to O(log n) instead of O(n)!"
            ),
            new Question(
                    "REST Backend #9: `@RestController` vs `@Controller`",
                    """
                    In a Java REST backend, `@RestController` is equivalent to combining which two annotations?""",
                    List.of(
                            "A) @Service + @Repository",
                            "B) @Controller + @ResponseBody",
                            "C) @Component + @Autowired",
                            "D) @Bean + @Configuration"
                    ),
                    'B',
                    "`@RestController = @Controller + @ResponseBody`, meaning return values are serialized directly as JSON/HTTP response bodies rather than resolving HTML view templates!"
            ),
            new Question(
                    "REST Backend #10: `@PathVariable` vs `@RequestParam`",
                    """
                    Given URL: `GET /api/users/42?active=true`
                    How do you extract `42` and `true` in a REST controller method?""",
                    List.of(
                            "A) 42 via @RequestParam, true via @PathVariable",
                            "B) 42 via @PathVariable, true via @RequestParam",
                            "C) Both via @RequestBody",
                            "D) Both via @Autowired"
                    ),
                    'B',
                    "`@PathVariable` extracts segments from the URI path (`/users/{id}` -> `42`), whereas `@RequestParam` extracts query-string key-value pairs (`?active=true`)!"
            )
    );

    public static void main(String[] args) {
        boolean reviewOnly = args.length > 0 && args[0].equalsIgnoreCase("--review");
        TestRunner.printBanner("QUIZ 02: SQL, DBMS, JDBC, Java 8 & REST Backend");

        if (reviewOnly) {
            printAllExplanations();
            return;
        }

        System.out.println("Type A, B, C, or D and press Enter. (Type R to reveal all answers, Q to quit)\n");
        Scanner sc = new Scanner(System.in);
        int score = 0;
        int answered = 0;

        for (int i = 0; i < QUESTIONS.size(); i++) {
            Question q = QUESTIONS.get(i);
            System.out.println(TestRunner.BOLD + "[" + (i + 1) + "/" + QUESTIONS.size() + "] " + q.topic() + TestRunner.RESET);
            System.out.println("------------------------------------------------------------");
            System.out.println(q.prompt());
            System.out.println("------------------------------------------------------------");
            for (String opt : q.options()) {
                System.out.println("  " + opt);
            }
            System.out.print("\nYour answer (A/B/C/D, R=review all, Q=quit): ");

            if (!sc.hasNextLine()) break;
            String raw = sc.nextLine().trim().toUpperCase();
            if (raw.isEmpty()) {
                i--;
                continue;
            }
            char ch = raw.charAt(0);
            if (ch == 'Q') break;
            if (ch == 'R') {
                printAllExplanations();
                return;
            }
            if (ch < 'A' || ch > 'D') {
                System.out.println(TestRunner.YELLOW + "Please enter A, B, C, D, R, or Q!\n" + TestRunner.RESET);
                i--;
                continue;
            }

            answered++;
            if (ch == q.answer()) {
                score++;
                System.out.println(TestRunner.GREEN + "CORRECT! " + TestRunner.RESET + q.explanation() + "\n");
            } else {
                System.out.println(TestRunner.RED + "NOT QUITE! Correct answer is " + q.answer() + ". "
                        + TestRunner.RESET + q.explanation() + "\n");
            }
        }

        System.out.println("============================================================");
        System.out.printf("SQL & Backend Quiz Score: %d / %d%n", score, answered);
        System.out.println("============================================================");
    }

    public static void printAllExplanations() {
        for (int i = 0; i < QUESTIONS.size(); i++) {
            Question q = QUESTIONS.get(i);
            System.out.println("\n" + TestRunner.BOLD + "[" + (i + 1) + "] " + q.topic() + TestRunner.RESET);
            System.out.println(q.prompt());
            System.out.println(TestRunner.GREEN + "Answer: " + q.answer() + TestRunner.RESET + " - " + q.explanation());
        }
    }
}
