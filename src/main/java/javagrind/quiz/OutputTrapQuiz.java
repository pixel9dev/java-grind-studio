package javagrind.quiz;

import javagrind.util.TestRunner;
import java.util.List;
import java.util.Scanner;

/**
 * ==========================================================================
 * ROUND 1 MCQ SIMULATOR: "What Will This Java Code Print?" (10 Classic Traps)
 * ==========================================================================
 * HOW TO PLAY IN INTELLIJ:
 * 1. Click the green ▶ icon next to `main` (Shift+F10).
 * 2. Click inside the Run console at the bottom, type A / B / C / D, press Enter!
 *    (Type 'R' to reveal all answers immediately, or 'Q' to quit.)
 */
public class OutputTrapQuiz {

    record Question(String title, String snippet, List<String> options, char answer, String explanation) {}

    private static final List<Question> QUESTIONS = List.of(
            new Question(
                    "Trap 1: String Pool vs Heap Reference",
                    """
                    String a = "Java";
                    String b = "Ja" + "va";
                    String c = new String("Java");
                    System.out.println((a == b) + " " + (a == c));""",
                    List.of("A) true true", "B) true false", "C) false false", "D) false true"),
                    'B',
                    "\"Ja\" + \"va\" is folded at COMPILE TIME into \"Java\" in the String Pool, so a == b is true. `new String(...)` allocates a fresh Heap object, so a == c is false!"
            ),
            new Question(
                    "Trap 2: Autoboxing & Integer Cache (-128 to 127)",
                    """
                    Integer p = 100, q = 100;
                    Integer x = 200, y = 200;
                    System.out.println((p == q) + " " + (x == y));""",
                    List.of("A) true true", "B) false false", "C) true false", "D) Compilation error"),
                    'C',
                    "Java caches Integer instances only for -128..127. 100 reuses the cached object (true), whereas 200 creates two distinct Heap objects (false). Always use .equals()!"
            ),
            new Question(
                    "Trap 3: The Infamous `x = x++` Post-Increment Trap",
                    """
                    int x = 5;
                    x = x++;
                    System.out.println(x);""",
                    List.of("A) 5", "B) 6", "C) 0", "D) Undefined behavior"),
                    'A',
                    "In Java (unlike C, evaluation order is strictly defined!): `x++` evaluates to 5, increments `x` to 6, and THEN the assignment `x = ...` overwrites `x` back to 5!"
            ),
            new Question(
                    "Trap 4: `char` Addition vs String Concatenation",
                    """
                    System.out.println('A' + 'B');
                    System.out.println("" + 'A' + 'B');""",
                    List.of("A) AB then AB", "B) 131 then AB", "C) 131 then 131", "D) Compilation error"),
                    'B',
                    "`char + char` promotes both operands to `int`! 'A'(65) + 'B'(66) = 131. Starting with `\"\"` makes '+' left-to-right String concatenation -> \"AB\"."
            ),
            new Question(
                    "Trap 5: `List<Integer>.remove(1)` Overload Trap",
                    """
                    List<Integer> list = new ArrayList<>(List.of(10, 20, 1));
                    list.remove(1);
                    System.out.println(list);""",
                    List.of("A) [10, 20]", "B) [10, 1]", "C) [20, 1]", "D) UnsupportedOperationException"),
                    'B',
                    "`List` has TWO remove methods: `remove(int index)` and `remove(Object o)`. Passing literal `1` matches `int index` (no boxing needed!), removing index 1 (`20`)! Use `remove(Integer.valueOf(1))` to remove value 1."
            ),
            new Question(
                    "Trap 6: Static Method Hiding vs Runtime Overriding",
                    """
                    class Parent {
                        static String tag() { return "P"; }
                        String name() { return "P"; }
                    }
                    class Child extends Parent {
                        static String tag() { return "C"; }
                        @Override String name() { return "C"; }
                    }
                    Parent obj = new Child();
                    System.out.println(obj.tag() + obj.name());""",
                    List.of("A) CC", "B) PP", "C) PC", "D) CP"),
                    'C',
                    "Static methods bind at COMPILE TIME to declared type `Parent` -> \"P\". Instance methods dispatch at RUNTIME to actual Heap object `Child` -> \"C\"!"
            ),
            new Question(
                    "Trap 7: `try - finally` Return Override",
                    """
                    static int compute() {
                        try {
                            return 10;
                        } finally {
                            return 20;
                        }
                    }""",
                    List.of("A) 10", "B) 20", "C) 30", "D) Compilation error"),
                    'B',
                    "`finally` always runs before `try` finishes returning; a `return` inside `finally` replaces the pending return value (10 -> 20) - and even swallows exceptions! Never return from `finally` in real code."
            ),
            new Question(
                    "Trap 8: Overloading Resolution with `null`",
                    """
                    static void print(Object o) { System.out.println("Object"); }
                    static void print(String s) { System.out.println("String"); }
                    // Call:
                    print(null);""",
                    List.of("A) Object", "B) String", "C) NullPointerException", "D) Ambiguous call compile error"),
                    'B',
                    "Java chooses the MOST SPECIFIC applicable overload at compile time. Since `String` is a subtype of `Object`, `print(String)` wins! (If there were ALSO `print(Integer)`, `print(null)` wouldn't compile due to ambiguity.)"
            ),
            new Question(
                    "Trap 9: Short-Circuit `||` Side Effects",
                    """
                    int a = 1, b = 1;
                    if (a == 1 || ++b > 1) {
                        System.out.println(a + " " + b);
                    }""",
                    List.of("A) 1 2", "B) 1 1", "C) 2 2", "D) false"),
                    'B',
                    "Because `a == 1` is already `true`, short-circuit `||` SKIPS evaluating the right-hand side `++b > 1` completely! So `b` remains 1."
            ),
            new Question(
                    "Trap 10: Static & Constructor Initialization Order",
                    """
                    class Base {
                        static { System.out.print("1"); }
                        Base() { System.out.print("2"); }
                    }
                    class Sub extends Base {
                        static { System.out.print("3"); }
                        Sub()  { System.out.print("4"); }
                    }
                    // First time creating: new Sub();""",
                    List.of("A) 1234", "B) 1324", "C) 3142", "D) 2413"),
                    'B',
                    "Order when loading & instantiating a subclass: (1) Parent static block -> (3) Child static block -> (2) Parent constructor -> (4) Child constructor = 1324!"
            )
    );

    public static void main(String[] args) {
        boolean reviewOnly = args.length > 0 && args[0].equalsIgnoreCase("--review");
        TestRunner.printBanner("ROUND 1 QUIZ: 10 Classic Java Output-Prediction Traps");

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
            System.out.println(TestRunner.BOLD + "[" + (i + 1) + "/" + QUESTIONS.size() + "] " + q.title() + TestRunner.RESET);
            System.out.println("------------------------------------------------------------");
            System.out.println(q.snippet());
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
            if (ch == 'Q') {
                break;
            }
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
        System.out.printf("Final Quiz Score: %d / %d%n", score, answered);
        System.out.println("============================================================");
    }

    public static void printAllExplanations() {
        for (int i = 0; i < QUESTIONS.size(); i++) {
            Question q = QUESTIONS.get(i);
            System.out.println("\n" + TestRunner.BOLD + "[" + (i + 1) + "] " + q.title() + TestRunner.RESET);
            System.out.println(q.snippet());
            System.out.println(TestRunner.GREEN + "Answer: " + q.answer() + TestRunner.RESET + " - " + q.explanation());
        }
    }
}
