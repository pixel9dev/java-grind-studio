package javagrind.app;

import java.util.List;

/**
 * Structured lessons, live runnable Java playgrounds, 20 MCQ questions,
 * 25 interview flashcards, and the 48-hour study plan for the desktop app.
 */
public final class CurriculumData {

    private CurriculumData() {}

    public record Lesson(String id, String title, String targetProblemGroup, String html, String playgroundCode) {
        @Override
        public String toString() {
            return title;
        }
    }

    public record QuizItem(String id, String deck, String title, String promptCode,
                           List<String> options, char answer, String explanation) {}

    public record Flashcard(String topic, String question, String answer) {}

    public static final List<Lesson> LESSONS = List.of(
            new Lesson(
                    "L01",
                    "01. Basics, Types, Loops & Pass-by-Value",
                    "01. Warmup & Math",
                    """
                    <h1>01. Java Basics, Primitive Types &amp; Memory</h1>
                    <p>Every Java program starts execution inside <code>public static void main(String[] args)</code>. Java is statically typed: every variable has a known type at compile time.</p>
                    <h2>The 8 Primitive Types</h2>
                    <ul>
                      <li><b>Integers:</b> <code>byte</code> (1B), <code>short</code> (2B), <code>int</code> (4B, ~ &plusmn;2.14 billion), <code>long</code> (8B — add suffix <code>L</code> like <code>9000000000L</code>)</li>
                      <li><b>Decimals:</b> <code>float</code> (4B, suffix <code>f</code>), <code>double</code> (8B, default)</li>
                      <li><b>Other:</b> <code>char</code> (2B UTF-16: <code>'A'=65</code>, <code>'a'=97</code>, <code>'0'=48</code>), <code>boolean</code> (<code>true</code>/<code>false</code> only — never 0/1!)</li>
                    </ul>
                    <div class='trap'>
                      <b>TRAP 1 — Integer Division &amp; Overflow:</b><br/>
                      <code>5 / 2</code> evaluates to <code>2</code> (truncates decimal!). Write <code>5.0 / 2</code> for <code>2.5</code>.<br/>
                      In binary search, <code>(low + high) / 2</code> can overflow <code>int</code>; always write <code>low + (high - low) / 2</code>!
                    </div>
                    <div class='trap'>
                      <b>TRAP 2 — Wrapper Integer Cache (-128 to 127):</b><br/>
                      <code>Integer a = 127, b = 127;</code> &rarr; <code>a == b</code> is <code>true</code> (cached!).<br/>
                      <code>Integer x = 128, y = 128;</code> &rarr; <code>x == y</code> is <code>false</code> (distinct Heap objects!). Always use <code>.equals()</code>!
                    </div>
                    <div class='tip'>
                      <b>INTERVIEW RULE:</b> Java is strictly <b>Pass-by-Value</b>! Passing an array/object passes a <i>copy of the reference</i> — mutating <code>arr[0] = 99</code> is visible to the caller, while reassigning <code>arr = new int[5]</code> is not.
                    </div>
                    <p>Try editing the live Java code on the right and click <b>Run Playground (Ctrl+Enter)</b>!</p>
                    """,
                    """
                    import java.util.Arrays;

                    public class Playground {
                        public static void main(String[] args) {
                            // 1. Integer division vs double division
                            System.out.println("5 / 2     = " + (5 / 2));
                            System.out.println("5.0 / 2   = " + (5.0 / 2));

                            // 2. Left-to-right '+' evaluation
                            System.out.println(10 + 20 + "Java");   // 30Java
                            System.out.println("Java" + 10 + 20);   // Java1020

                            // 3. Wrapper cache (-128..127)
                            Integer c1 = 127, c2 = 127;
                            Integer h1 = 128, h2 = 128;
                            System.out.println("127 == 127 -> " + (c1 == c2));
                            System.out.println("128 == 128 -> " + (h1 == h2) + " (equals=" + h1.equals(h2) + ")");

                            // 4. Arrays & sorting
                            int[] nums = {42, 7, 19, 3};
                            Arrays.sort(nums);
                            System.out.println("Sorted: " + Arrays.toString(nums));
                        }
                    }
                    """
            ),
            new Lesson(
                    "L02",
                    "02. Strings, String Pool & StringBuilder",
                    "02. Strings",
                    """
                    <h1>02. Strings, Immutability &amp; StringBuilder</h1>
                    <p><code>String</code> in Java is <b>immutable</b>: once created, its characters can never change. Methods like <code>s.toUpperCase()</code> or <code>s.trim()</code> return a <b>brand-new String</b>!</p>
                    <h2>String Constant Pool (SCP)</h2>
                    <ul>
                      <li><code>String a = "Java"; String b = "Java";</code> &rarr; Both point to the same pooled object (<code>a == b</code> is <code>true</code>).</li>
                      <li><code>String c = new String("Java");</code> &rarr; Allocates a separate Heap object (<code>a == c</code> is <code>false</code>, <code>a.equals(c)</code> is <code>true</code>).</li>
                    </ul>
                    <h2>Essential Coding-Round Methods</h2>
                    <pre>s.length()                 // parentheses! (arrays use arr.length)
                    s.charAt(i)                // 0-indexed char
                    s.substring(start, end)    // start inclusive, end EXCLUSIVE
                    s.trim().split("\\\\s+")     // split words across multiple spaces
                    s.toCharArray()            // convert to char[] for sorting/swapping
                    Character.isLetterOrDigit(ch) / Character.toLowerCase(ch)</pre>
                    <div class='trap'>
                      <b>NEVER concatenate <code>str += ch</code> in a loop!</b> That copies the whole string every iteration (<code>O(n^2)</code>). Always use <code>StringBuilder</code> (<code>O(n)</code>)!
                    </div>
                    """,
                    """
                    import java.util.Arrays;

                    public class Playground {
                        public static void main(String[] args) {
                            String pool1 = "Java";
                            String pool2 = "Java";
                            String heapStr = new String("Java");

                            System.out.println("pool1 == pool2   : " + (pool1 == pool2));
                            System.out.println("pool1 == heapStr : " + (pool1 == heapStr));
                            System.out.println("equals()         : " + pool1.equals(heapStr));

                            // Word splitting & StringBuilder
                            String messy = "  Java   makes   strings  easy ";
                            String[] words = messy.trim().split("\\\\s+");
                            System.out.println("Words: " + Arrays.toString(words));

                            StringBuilder sb = new StringBuilder();
                            for (int i = words.length - 1; i >= 0; i--) {
                                sb.append(words[i]);
                                if (i > 0) sb.append(" ");
                            }
                            System.out.println("Reversed sentence: " + sb);
                        }
                    }
                    """
            ),
            new Lesson(
                    "L03",
                    "03. OOPs 4 Pillars, Interfaces & Records",
                    "04. OOP Design",
                    """
                    <h1>03. Object-Oriented Programming (OOPs) &amp; Java 17</h1>
                    <p>Roughly 40% of technical interview questions come directly from OOP fundamentals:</p>
                    <ul>
                      <li><b>Encapsulation:</b> Keep fields <code>private</code>; expose validated behavior via methods (<code>deposit()</code>, <code>withdraw()</code>).</li>
                      <li><b>Inheritance:</b> <code>class Child extends Parent</code> (single class inheritance). Call parent constructor via <code>super(...)</code> on line 1.</li>
                      <li><b>Polymorphism:</b>
                        <ul>
                          <li><i>Compile-time (Overloading):</i> Same method name, different parameter types/count.</li>
                          <li><i>Runtime (Overriding):</i> Subclass redefines an instance method; JVM dispatches based on the <b>actual Heap object</b>!</li>
                        </ul>
                      </li>
                      <li><b>Abstraction:</b> <code>abstract class</code> (shared state + constructors + partial logic) vs <code>interface</code> (pure capability contract, multiple implementation via <code>implements A, B</code>, supports Java 8+ <code>default</code> methods).</li>
                    </ul>
                    <div class='trap'>
                      <b>STATIC HIDING TRAP:</b> <code>static</code> methods belong to the <i>class</i>, NOT the object — they are resolved at compile time by reference type, never overridden!
                    </div>
                    <div class='tip'>
                      <b>Java 17 Bonus:</b> <code>record Employee(int id, String name) {}</code> auto-generates immutable fields, constructor, accessors, <code>equals()</code>, <code>hashCode()</code>, and <code>toString()</code> in one line!
                    </div>
                    """,
                    """
                    import java.util.List;

                    public class Playground {
                        interface Payable {
                            double monthlyPay();
                        }

                        record FullTime(String name, double salary) implements Payable {
                            @Override public double monthlyPay() { return salary; }
                        }

                        record Contractor(String name, double hourly, int hours) implements Payable {
                            @Override public double monthlyPay() { return hourly * hours; }
                        }

                        public static void main(String[] args) {
                            List<Payable> team = List.of(
                                new FullTime("Aarav", 75000),
                                new Contractor("Meera", 600, 140)
                            );

                            for (Payable p : team) {
                                // Runtime polymorphism + Java 17 pattern matching instanceof!
                                System.out.println(p + " -> Pay = " + p.monthlyPay());
                            }
                        }
                    }
                    """
            ),
            new Lesson(
                    "L04",
                    "04. Collections Framework (List, Map, Set, Heap)",
                    "05. Collections & Hashing",
                    """
                    <h1>04. Java Collections Framework — Coding Round Superpower</h1>
                    <p>Choosing the right data structure turns 30 lines of nested loops (<code>O(n^2)</code>) into 6 clean lines (<code>O(n)</code>):</p>
                    <ul>
                      <li><code>ArrayList&lt;E&gt;</code>: Dynamic array, <code>O(1)</code> index access. Sort with <code>list.sort(Comparator.comparingInt(...))</code>.</li>
                      <li><code>HashSet&lt;E&gt;</code>: <code>O(1)</code> <code>add</code>/<code>contains</code>. Note: <code>set.add(x)</code> returns <code>false</code> if <code>x</code> was already present!</li>
                      <li><code>HashMap&lt;K,V&gt;</code>: <code>O(1)</code> key-value lookup. Memorize:
                        <pre>freq.put(x, freq.getOrDefault(x, 0) + 1);
                    groups.computeIfAbsent(key, k -&gt; new ArrayList&lt;&gt;()).add(val);</pre>
                      </li>
                      <li><code>ArrayDeque&lt;E&gt;</code>: Preferred over legacy <code>Stack</code>! Use <code>push/pop/peek</code> for Stack (LIFO) and <code>offer/poll/peek</code> for Queue (FIFO).</li>
                      <li><code>PriorityQueue&lt;E&gt;</code>: Min-Heap by default; pass <code>Comparator.reverseOrder()</code> for Max-Heap!</li>
                    </ul>
                    <div class='tip'>
                      <b>HashMap Internals (Interview Must-Know):</b> Uses <code>hashCode()</code> &rarr; bucket index &amp; <code>equals()</code> to resolve collisions. In Java 8+, when a bucket hits <b>8 nodes</b> (capacity &ge; 64), its linked list converts into a <b>Red-Black Tree</b> (<code>O(n) &rarr; O(log n)</code> worst case)!
                    </div>
                    """,
                    """
                    import java.util.*;

                    public class Playground {
                        public static void main(String[] args) {
                            String[] words = {"java", "code", "java", "array", "code", "java"};

                            // 1. Frequency map in 3 lines
                            Map<String, Integer> freq = new HashMap<>();
                            for (String w : words) {
                                freq.put(w, freq.getOrDefault(w, 0) + 1);
                            }
                            System.out.println("Frequencies: " + freq);

                            // 2. Stack with ArrayDeque
                            Deque<Character> stack = new ArrayDeque<>();
                            stack.push('(');
                            stack.push('[');
                            System.out.println("Popped from stack: " + stack.pop());

                            // 3. Max-Heap with PriorityQueue
                            PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Comparator.reverseOrder());
                            maxHeap.addAll(List.of(25, 90, 10, 60));
                            System.out.println("Largest in heap: " + maxHeap.poll());
                        }
                    }
                    """
            ),
            new Lesson(
                    "L05",
                    "05. Exceptions, Lambdas, Stream API & Optional",
                    "06. Streams & Exceptions",
                    """
                    <h1>05. Exception Handling &amp; Java 8+ Stream API</h1>
                    <h2>Checked vs Unchecked Exceptions</h2>
                    <ul>
                      <li><b>Checked</b> (<code>IOException</code>, <code>SQLException</code>): Compiler forces <code>try-catch</code> or <code>throws</code>.</li>
                      <li><b>Unchecked</b> (subclasses of <code>RuntimeException</code>: <code>NullPointerException</code>, <code>IllegalArgumentException</code>, <code>NumberFormatException</code>): Caused by logic bugs at runtime.</li>
                      <li><code>finally</code> ALWAYS executes (cleanup), or even better: use <b>try-with-resources</b> (<code>try (var br = ...)</code>)!</li>
                    </ul>
                    <h2>Stream Pipeline Anatomy</h2>
                    <pre>List&lt;String&gt; res = items.stream()
                        .filter(Objects::nonNull)        // Predicate&lt;T&gt;   (lazy)
                        .map(String::trim)               // Function&lt;T,R&gt;  (lazy)
                        .distinct()                      // deduplicate    (lazy)
                        .sorted()                        // natural order  (lazy)
                        .toList();                       // terminal op triggers execution!</pre>
                    <div class='tip'>
                      <b>4 Core Functional Interfaces:</b> <code>Predicate&lt;T&gt;</code> (<code>T &rarr; boolean</code>), <code>Function&lt;T,R&gt;</code> (<code>T &rarr; R</code>), <code>Consumer&lt;T&gt;</code> (<code>T &rarr; void</code>), <code>Supplier&lt;T&gt;</code> (<code>() &rarr; T</code>).
                    </div>
                    """,
                    """
                    import java.util.*;
                    import java.util.stream.*;

                    public class Playground {
                        record Emp(String name, String dept, int salary) {}

                        public static void main(String[] args) {
                            List<Emp> staff = List.of(
                                new Emp("Ananya", "Backend", 120),
                                new Emp("Rohan", "Backend", 95),
                                new Emp("Kabir", "QA", 105),
                                new Emp("Dev", "Backend", 145)
                            );

                            // Group count by department
                            Map<String, Long> counts = staff.stream()
                                .collect(Collectors.groupingBy(Emp::dept, Collectors.counting()));
                            System.out.println("Headcount by dept: " + counts);

                            // 2nd highest salary using Streams
                            Optional<Emp> secondTop = staff.stream()
                                .sorted(Comparator.comparingInt(Emp::salary).reversed())
                                .skip(1)
                                .findFirst();
                            System.out.println("2nd highest earner: " + secondTop.orElse(null));
                        }
                    }
                    """
            ),
            new Lesson(
                    "L06",
                    "06. JVM Memory Model & Multithreading",
                    "03. Arrays & Two Pointers",
                    """
                    <h1>06. JVM Architecture, Garbage Collection &amp; Threads</h1>
                    <h2>JDK vs JRE vs JVM &amp; Memory</h2>
                    <ul>
                      <li><b>JDK</b> = JRE + Compiler (<code>javac</code>) + Tools &nbsp;|&nbsp; <b>JRE</b> = JVM + Standard Libraries.</li>
                      <li><b>Stack (Per-Thread):</b> Stores method call frames, primitive local variables, and object references.</li>
                      <li><b>Heap (Shared):</b> Stores all objects (<code>new ...</code>), arrays, and the String Constant Pool. Reclaimed by Garbage Collector (Young Gen Eden/Survivor + Old Gen).</li>
                    </ul>
                    <h2>Concurrency Essentials</h2>
                    <ul>
                      <li>Always start a thread with <code>thread.start()</code> (calling <code>thread.run()</code> runs synchronously on the current thread!).</li>
                      <li><code>count++</code> is <b>NOT atomic</b> (read &rarr; add &rarr; write race condition!).</li>
                      <li><code>volatile</code> ensures <b>visibility</b> across CPU caches, NOT atomicity.</li>
                      <li><code>synchronized</code> (locks) and <code>AtomicInteger</code> (CAS) ensure both visibility and atomicity!</li>
                    </ul>
                    """,
                    """
                    import java.util.concurrent.atomic.AtomicInteger;

                    public class Playground {
                        static int unsafeCount = 0;
                        static final AtomicInteger safeCount = new AtomicInteger(0);

                        public static void main(String[] args) throws InterruptedException {
                            Runnable work = () -> {
                                for (int i = 0; i < 10_000; i++) {
                                    unsafeCount++;
                                    safeCount.incrementAndGet();
                                }
                            };

                            Thread t1 = new Thread(work);
                            Thread t2 = new Thread(work);
                            t1.start();
                            t2.start();
                            t1.join();
                            t2.join();

                            System.out.println("Unsafe int++       : " + unsafeCount + " (may lose updates!)");
                            System.out.println("Safe AtomicInteger : " + safeCount.get() + " (always 20000!)");
                        }
                    }
                    """
            ),
            new Lesson(
                    "L07",
                    "07. Live SQL Queries, JDBC & REST Crash Course",
                    "07. Linked Lists, Matrices & DP",
                    """
                    <h1>07. Live SQL Queries, JDBC &amp; REST Backend Essentials</h1>
                    <p>Technical interviewers routinely ask you to write 1–2 SQL queries live alongside Core Java:</p>
                    <h2>Top 4 Must-Memorize SQL Queries</h2>
                    <pre>-- 1. Second Highest Distinct Salary (safe even when top salaries tie!):
                    SELECT MAX(salary) FROM Employee
                    WHERE salary &lt; (SELECT MAX(salary) FROM Employee);

                    -- 2. N-th Highest Salary using DENSE_RANK():
                    SELECT salary FROM (
                        SELECT salary, DENSE_RANK() OVER (ORDER BY salary DESC) AS rnk FROM Employee
                    ) t WHERE rnk = 2;

                    -- 3. Departments with &gt; 5 Employees (WHERE = row filter, HAVING = group filter!):
                    SELECT dept_id, COUNT(*) FROM Employee GROUP BY dept_id HAVING COUNT(*) &gt; 5;

                    -- 4. Employees with NO Matching Department (LEFT JOIN + IS NULL):
                    SELECT e.name FROM Employee e
                    LEFT JOIN Department d ON e.dept_id = d.id WHERE d.id IS NULL;</pre>
                    <div class='tip'>
                      <b>JDBC &amp; REST Quick Hits:</b><br/>
                      &bull; Always use <code>PreparedStatement</code> with <code>?</code> parameters to prevent SQL Injection.<br/>
                      &bull; <code>@RestController</code> = <code>@Controller</code> + <code>@ResponseBody</code> (returns JSON).<br/>
                      &bull; <code>/users/42?active=true</code> &rarr; <code>42</code> via <code>@PathVariable</code>, <code>true</code> via <code>@RequestParam</code>.
                    </div>
                    """,
                    """
                    import java.util.*;
                    import java.util.stream.*;

                    public class Playground {
                        // Notice how SQL clauses map directly to Java Stream operations!
                        record Employee(int id, String name, String dept, int salary) {}

                        public static void main(String[] args) {
                            List<Employee> table = List.of(
                                new Employee(1, "Asha", "Eng", 150_000),
                                new Employee(2, "Vikram", "Eng", 150_000), // Tied top salary!
                                new Employee(3, "Riya", "QA", 110_000),
                                new Employee(4, "Karan", "QA", 90_000)
                            );

                            // Equivalent of: SELECT MAX(salary) WHERE salary < (SELECT MAX(salary))
                            int maxSal = table.stream().mapToInt(Employee::salary).max().orElse(-1);
                            int secondDistinct = table.stream()
                                .mapToInt(Employee::salary)
                                .filter(s -> s < maxSal)
                                .max()
                                .orElse(-1);

                            System.out.println("Top Salary             : " + maxSal);
                            System.out.println("2nd Distinct Salary    : " + secondDistinct + " (Handles 150k tie!)");
                        }
                    }
                    """
            ),
            new Lesson(
                    "L08",
                    "08. SOLID, Design Patterns, Sealed Types & Async",
                    "08. Enterprise & Design Patterns",
                    """
                    <h1>08. Enterprise Java: SOLID, Design Patterns &amp; Async Pipelines</h1>
                    <p>Large-scale backend teams evaluate how cleanly you structure extensible, thread-safe code:</p>
                    <h2>SOLID Principles in 1 Line Each</h2>
                    <ul>
                      <li><b>S</b>ingle Responsibility: One class, one reason to change.</li>
                      <li><b>O</b>pen/Closed: Extend behavior via interfaces/strategies without editing existing classes.</li>
                      <li><b>L</b>iskov Substitution: Subtypes must honor the base contract without surprises.</li>
                      <li><b>I</b>nterface Segregation: Prefer focused interfaces (<code>Runnable</code>, <code>AutoCloseable</code>) over fat interfaces.</li>
                      <li><b>D</b>ependency Inversion: Depend on abstractions (interfaces), not concrete implementations.</li>
                    </ul>
                    <h2>4 Essential Production Design Patterns</h2>
                    <ul>
                      <li><b>Builder:</b> Constructs complex immutable objects step-by-step without telescoping constructors.</li>
                      <li><b>Strategy:</b> Swaps algorithms (pricing, sorting, validation) via functional interfaces.</li>
                      <li><b>Observer / Event Bus:</b> Decouples event producers from subscribers (<code>CopyOnWriteArrayList</code>).</li>
                      <li><b>Singleton (Holder Idiom):</b> Thread-safe lazy initialization via static inner holder class.</li>
                    </ul>
                    <div class='tip'>
                      <b>Modern Java 17 + Async:</b> Combine <code>sealed interface</code> outcome types with <code>CompletableFuture.supplyAsync(...).thenCombine(...).exceptionally(...)</code> for non-blocking microservice orchestration!
                    </div>
                    """,
                    """
                    import java.util.concurrent.*;

                    public class Playground {
                        // Java 17 Sealed Hierarchy
                        sealed interface ApiResult permits Ok, Fallback {}
                        record Ok(String data) implements ApiResult {}
                        record Fallback(String reason) implements ApiResult {}

                        public static void main(String[] args) {
                            CompletableFuture<Double> priceFut = CompletableFuture.supplyAsync(() -> 1000.0);
                            CompletableFuture<Double> taxFut = CompletableFuture.supplyAsync(() -> 0.18);

                            double total = priceFut.thenCombine(taxFut, (p, t) -> p * (1.0 + t)).join();
                            ApiResult res = new Ok("Final Quote = " + total);

                            if (res instanceof Ok ok) {
                                System.out.println("Parallel async result -> " + ok.data());
                            }
                        }
                    }
                    """
            )
    );

    public static final List<QuizItem> QUIZZES = List.of(
            new QuizItem("Q01", "Core Java Traps", "String Pool vs Heap Reference",
                    """
                    String a = "Java";
                    String b = "Ja" + "va";
                    String c = new String("Java");
                    System.out.println((a == b) + " " + (a == c));""",
                    List.of("A) true true", "B) true false", "C) false false", "D) false true"),
                    'B',
                    "\"Ja\" + \"va\" is folded at compile time into \"Java\" in the String Pool, so a == b is true. `new String(...)` creates a distinct Heap object, so a == c is false."),
            new QuizItem("Q02", "Core Java Traps", "Autoboxing & Integer Cache (-128 to 127)",
                    """
                    Integer p = 100, q = 100;
                    Integer x = 200, y = 200;
                    System.out.println((p == q) + " " + (x == y));""",
                    List.of("A) true true", "B) false false", "C) true false", "D) Compilation error"),
                    'C',
                    "Integer.valueOf() caches values from -128 to 127. 100 reuses the cached object (true), while 200 allocates two separate objects (false). Always compare wrappers with .equals()!"),
            new QuizItem("Q03", "Core Java Traps", "Post-Increment Reassignment (`x = x++`)",
                    """
                    int x = 5;
                    x = x++;
                    System.out.println(x);""",
                    List.of("A) 5", "B) 6", "C) 0", "D) Compilation error"),
                    'A',
                    "`x++` evaluates to the old value (5), increments `x` to 6, and then the assignment `x = ...` overwrites `x` back with 5!"),
            new QuizItem("Q04", "Core Java Traps", "`char` Addition vs String Concatenation",
                    """
                    System.out.println('A' + 'B');
                    System.out.println("" + 'A' + 'B');""",
                    List.of("A) AB then AB", "B) 131 then AB", "C) 131 then 131", "D) Compilation error"),
                    'B',
                    "`'A' + 'B'` promotes both `char`s to `int`: 65 + 66 = 131. Prefixing `\"\"` switches `+` to String concatenation -> \"AB\"."),
            new QuizItem("Q05", "Core Java Traps", "`List<Integer>.remove(1)` Overload Trap",
                    """
                    List<Integer> list = new ArrayList<>(List.of(10, 20, 1));
                    list.remove(1);
                    System.out.println(list);""",
                    List.of("A) [10, 20]", "B) [10, 1]", "C) [20, 1]", "D) UnsupportedOperationException"),
                    'B',
                    "`remove(int index)` beats `remove(Object o)` without autoboxing, removing index 1 (`20`)! Use `remove(Integer.valueOf(1))` to delete value 1."),
            new QuizItem("Q06", "Core Java Traps", "Static Method Hiding vs Runtime Overriding",
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
                    "Static methods bind at compile time to reference type `Parent` (\"P\"). Instance methods dispatch at runtime to actual Heap object `Child` (\"C\")."),
            new QuizItem("Q07", "Core Java Traps", "`try - finally` Return Value Override",
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
                    "`finally` always runs before `try` completes; returning inside `finally` overrides the pending return value `10` with `20`."),
            new QuizItem("Q08", "Core Java Traps", "Overload Resolution with `null`",
                    """
                    static void print(Object o) { System.out.println("Object"); }
                    static void print(String s) { System.out.println("String"); }
                    // Call:
                    print(null);""",
                    List.of("A) Object", "B) String", "C) NullPointerException", "D) Ambiguous compile error"),
                    'B',
                    "Java picks the MOST SPECIFIC matching overload at compile time. Since `String` extends `Object`, `print(String)` wins!"),
            new QuizItem("Q09", "Core Java Traps", "Short-Circuit `||` Evaluation",
                    """
                    int a = 1, b = 1;
                    if (a == 1 || ++b > 1) {
                        System.out.println(a + " " + b);
                    }""",
                    List.of("A) 1 2", "B) 1 1", "C) 2 2", "D) Compilation error"),
                    'B',
                    "Because `a == 1` is `true`, `||` short-circuits and never executes `++b > 1`, so `b` stays `1`."),
            new QuizItem("Q10", "Core Java Traps", "Static & Constructor Initialization Order",
                    """
                    class Base {
                        static { System.out.print("1"); }
                        Base() { System.out.print("2"); }
                    }
                    class Sub extends Base {
                        static { System.out.print("3"); }
                        Sub()  { System.out.print("4"); }
                    }
                    // First call: new Sub();""",
                    List.of("A) 1234", "B) 1324", "C) 3142", "D) 2413"),
                    'B',
                    "Execution order: Parent static (1) -> Child static (3) -> Parent constructor (2) -> Child constructor (4) = 1324!"),
            new QuizItem("Q11", "SQL & Backend", "Second Highest Distinct Salary",
                    """
                    Table: Employee(id, name, salary)
                    Two top employees tie at 100,000 and the next earns 85,000.
                    Which query reliably returns 85,000?""",
                    List.of(
                            "A) SELECT salary FROM Employee ORDER BY salary DESC LIMIT 1 OFFSET 1;",
                            "B) SELECT MAX(salary) FROM Employee WHERE salary < (SELECT MAX(salary) FROM Employee);",
                            "C) SELECT TOP 2 salary FROM Employee;",
                            "D) SELECT salary FROM Employee GROUP BY id HAVING MAX(salary);"
                    ),
                    'B',
                    "Without `DISTINCT`, `OFFSET 1` returns the second 100,000 row! Filtering `salary < (SELECT MAX(salary) ...)` or using `DENSE_RANK()` handles ties properly."),
            new QuizItem("Q12", "SQL & Backend", "`WHERE` vs `HAVING` in Aggregations",
                    """
                    Which clause filters departments whose AVERAGE salary exceeds 75,000?""",
                    List.of(
                            "A) WHERE AVG(salary) > 75000 GROUP BY dept",
                            "B) GROUP BY dept HAVING AVG(salary) > 75000",
                            "C) ORDER BY AVG(salary) WHERE dept > 75000",
                            "D) DISTINCT dept WHERE AVG(salary) > 75000"
                    ),
                    'B',
                    "`WHERE` filters rows BEFORE grouping and cannot take aggregate functions. `HAVING` filters groups AFTER `GROUP BY`."),
            new QuizItem("Q13", "SQL & Backend", "`DELETE` vs `TRUNCATE` vs `DROP`",
                    """
                    Which fast DDL command removes ALL rows from a table while keeping the table schema intact?""",
                    List.of(
                            "A) DELETE FROM Employee;",
                            "B) DROP TABLE Employee;",
                            "C) TRUNCATE TABLE Employee;",
                            "D) REMOVE ALL FROM Employee;"
                    ),
                    'C',
                    "`TRUNCATE` is DDL (fast page deallocation, keeps schema). `DELETE` is row-by-row DML (supports `WHERE`). `DROP` removes the table itself."),
            new QuizItem("Q14", "SQL & Backend", "`PRIMARY KEY` vs `UNIQUE` Constraint",
                    """
                    How many NULLs are permitted in a PRIMARY KEY column vs a UNIQUE column?""",
                    List.of(
                            "A) PRIMARY KEY: 1 NULL | UNIQUE: 0 NULLs",
                            "B) PRIMARY KEY: 0 NULLs | UNIQUE: NULLs allowed",
                            "C) Both forbid NULLs",
                            "D) Both allow unlimited NULLs"
                    ),
                    'B',
                    "`PRIMARY KEY` = `UNIQUE` + `NOT NULL` (max 1 PK per table). `UNIQUE` allows NULLs and multiple columns can have `UNIQUE` constraints."),
            new QuizItem("Q15", "SQL & Backend", "Unmatched Rows via `LEFT JOIN`",
                    """
                    How do you find Employees whose `dept_id` has NO match in `Department(id)`?""",
                    List.of(
                            "A) ... INNER JOIN Department d ON e.dept_id = d.id",
                            "B) ... LEFT JOIN Department d ON e.dept_id = d.id WHERE d.id IS NULL",
                            "C) ... LEFT JOIN Department d ON e.dept_id = d.id WHERE d.id = NULL",
                            "D) ... CROSS JOIN Department d"
                    ),
                    'B',
                    "`LEFT JOIN` preserves unmatched left rows with NULL right columns, and SQL requires `IS NULL` (never `= NULL`)."),
            new QuizItem("Q16", "SQL & Backend", "JDBC `PreparedStatement` Security",
                    """
                    Why is `PreparedStatement` strongly preferred over `Statement` in JDBC?""",
                    List.of(
                            "A) It works only with NoSQL stores",
                            "B) Parameterized `?` placeholders prevent SQL Injection & allow plan caching",
                            "C) It auto-generates foreign keys",
                            "D) `Statement` cannot run SELECT queries"
                    ),
                    'B',
                    "`PreparedStatement` binds parameters strictly as literal values, neutralizing SQL injection payloads like `' OR '1'='1`."),
            new QuizItem("Q17", "SQL & Backend", "Java 8 Stream Functional Interfaces",
                    """
                    In `stream.filter(...).map(...).forEach(...)`, which functional interfaces are passed?""",
                    List.of(
                            "A) Function, Predicate, Supplier",
                            "B) Predicate<T>, Function<T,R>, Consumer<T>",
                            "C) Consumer<T>, Supplier<T>, Predicate<T>",
                            "D) Supplier<T>, Consumer<T>, Function<T,R>"
                    ),
                    'B',
                    "`Predicate<T>` (`T -> boolean` for `filter`), `Function<T,R>` (`T -> R` for `map`), `Consumer<T>` (`T -> void` for `forEach`)."),
            new QuizItem("Q18", "SQL & Backend", "Java 8+ `HashMap` Collision Treeification",
                    """
                    When a single `HashMap` bucket reaches 8 colliding nodes (and table capacity >= 64), what happens?""",
                    List.of(
                            "A) Throws IllegalStateException",
                            "B) Evicts oldest entry",
                            "C) Bucket converts from Linked List to Red-Black Tree (O(n) -> O(log n))",
                            "D) Converts entire map to TreeMap"
                    ),
                    'C',
                    "Java 8 introduced Red-Black Tree buckets at `TREEIFY_THRESHOLD = 8`, guaranteeing O(log n) worst-case bucket lookup."),
            new QuizItem("Q19", "SQL & Backend", "`@RestController` Composition",
                    """
                    `@RestController` combines which two annotations?""",
                    List.of(
                            "A) @Service + @Repository",
                            "B) @Controller + @ResponseBody",
                            "C) @Component + @Autowired",
                            "D) @Bean + @Configuration"
                    ),
                    'B',
                    "`@RestController = @Controller + @ResponseBody`, serializing return objects directly as JSON HTTP responses."),
            new QuizItem("Q20", "SQL & Backend", "`@PathVariable` vs `@RequestParam`",
                    """
                    For `GET /api/orders/99?status=PAID`, how do you bind `99` and `PAID`?""",
                    List.of(
                            "A) 99 via @RequestParam, PAID via @PathVariable",
                            "B) 99 via @PathVariable, PAID via @RequestParam",
                            "C) Both via @RequestBody",
                            "D) Both via @Autowired"
                    ),
                    'B',
                    "`@PathVariable` binds URI path segments (`/orders/{id}`), whereas `@RequestParam` binds query parameters (`?status=PAID`).")
    );

    public static final List<Flashcard> FLASHCARDS = List.of(
            new Flashcard("JVM & Core", "Difference between JDK, JRE, and JVM?",
                    "JVM executes bytecode (.class) via interpreter + JIT compiler & manages GC.\nJRE = JVM + core libraries to RUN apps.\nJDK = JRE + javac compiler & tools to DEVELOP apps."),
            new Flashcard("JVM & Core", "Stack vs Heap memory in Java?",
                    "Stack: Per-thread LIFO memory storing call frames, primitive locals, and object references.\nHeap: Shared across threads; stores all `new` objects, arrays, and String Constant Pool; managed by GC."),
            new Flashcard("JVM & Core", "Is Java Pass-by-Reference or Pass-by-Value?",
                    "100% Pass-by-Value! Primitives pass a copy of the value; objects pass a COPY of the reference (mutating fields works, reassigning the parameter reference does not affect caller)."),
            new Flashcard("JVM & Core", "Why is String immutable in Java?",
                    "1) Enables String Constant Pool memory sharing\n2) Thread-safe by default\n3) Caches hashCode() for fast HashMap keys\n4) Prevents tampering with security strings (DB URLs, file paths)."),
            new Flashcard("JVM & Core", "String vs StringBuilder vs StringBuffer?",
                    "String: Immutable (+ in loop is O(n^2)).\nStringBuilder: Mutable, NOT synchronized, fast O(1) amortized append (use in 99% of code).\nStringBuffer: Mutable, synchronized (thread-safe), slower legacy class."),
            new Flashcard("JVM & Core", "final vs finally vs finalize()?",
                    "final: Constant variable / non-overridable method / non-inheritable class.\nfinally: Cleanup block that always runs after try/catch.\nfinalize(): Deprecated GC hook (use try-with-resources instead)."),
            new Flashcard("OOPs", "Explain the 4 Pillars of OOP concisely.",
                    "Encapsulation: Private state + validated methods.\nAbstraction: Hiding implementation behind interfaces/abstract classes.\nInheritance: Reusing/extending base state (`extends`).\nPolymorphism: Same interface, runtime-dispatched behavior."),
            new Flashcard("OOPs", "Method Overloading vs Method Overriding?",
                    "Overloading (Compile-time): Same name, different parameter types/count (return type alone cannot overload).\nOverriding (Runtime): Subclass redefines inherited instance method with same signature."),
            new Flashcard("OOPs", "Can you override a static or private method?",
                    "No! `private` methods aren't inherited, and `static` methods belong to the class (compile-time method hiding, not runtime overriding)."),
            new Flashcard("OOPs", "abstract class vs interface?",
                    "Abstract class: Can hold instance fields, constructors & partial code (single `extends`).\nInterface: Constants + abstract/default/static methods, no constructors (multiple `implements`)."),
            new Flashcard("OOPs", "What is the equals() & hashCode() contract?",
                    "If `a.equals(b)` is true, `a.hashCode() == b.hashCode()` MUST be true! Violating this causes duplicate/lost entries in HashSet and HashMap."),
            new Flashcard("Collections", "How does HashMap work internally (Java 8+)?",
                    "Computes hash(key.hashCode()) -> bucket index. Collisions chain in a linked list checked via equals(). At >= 8 nodes (capacity >= 64), bucket converts to a Red-Black Tree (O(log n) worst-case)! Resizes at 0.75 load factor."),
            new Flashcard("Collections", "ArrayList vs LinkedList?",
                    "ArrayList: Contiguous dynamic array -> O(1) get(i), cache-friendly, O(n) middle insert.\nLinkedList: Doubly-linked nodes -> O(n) get(i), high memory overhead. Prefer ArrayList + ArrayDeque!"),
            new Flashcard("Collections", "HashMap vs Hashtable vs ConcurrentHashMap?",
                    "HashMap: Non-thread-safe, allows 1 null key, fast.\nHashtable: Legacy full-map lock, no nulls.\nConcurrentHashMap: Fine-grained bucket/CAS locking for high concurrency, no nulls."),
            new Flashcard("Collections", "Comparable vs Comparator?",
                    "Comparable (`compareTo`): Inside the class for its single natural order.\nComparator (`compare`): External lambda/strategy allowing multiple custom sort orders (e.g. by salary, then name)."),
            new Flashcard("Collections", "Fail-Fast vs Fail-Safe Iterators?",
                    "Fail-Fast (ArrayList, HashMap): Throws ConcurrentModificationException if mutated during iteration.\nFail-Safe (ConcurrentHashMap, CopyOnWriteArrayList): Works on snapshot/weakly-consistent view."),
            new Flashcard("Exceptions & Streams", "Checked vs Unchecked Exceptions?",
                    "Checked (IOException, SQLException): Enforced at compile time.\nUnchecked (RuntimeException: NPE, IllegalArgumentException): Runtime logic bugs."),
            new Flashcard("Exceptions & Streams", "Intermediate vs Terminal Stream operations?",
                    "Intermediate (`filter`, `map`, `sorted`, `distinct`): Lazy, return a Stream.\nTerminal (`toList`, `collect`, `count`, `reduce`): Trigger execution and close the stream."),
            new Flashcard("Exceptions & Streams", "map() vs flatMap() in Streams?",
                    "`map` converts 1 element -> 1 value (`Stream<List<T>>`).\n`flatMap` converts 1 element -> a Stream and flattens them into a single `Stream<T>`."),
            new Flashcard("Threads", "synchronized vs volatile?",
                    "`volatile`: Visibility only (reads/writes bypass stale CPU cache, `x++` still unsafe).\n`synchronized`: Both mutual exclusion (atomicity) AND visibility."),
            new Flashcard("Threads", "Runnable vs Callable?",
                    "`Runnable.run()` returns void and cannot throw checked exceptions.\n`Callable.call()` returns `V` (via `Future<V>`) and can throw checked exceptions."),
            new Flashcard("SQL & Backend", "SQL Query for 2nd Highest Distinct Salary?",
                    "SELECT MAX(salary) FROM Employee WHERE salary < (SELECT MAX(salary) FROM Employee);\n(Or use `DENSE_RANK() OVER (ORDER BY salary DESC) = 2` for N-th highest!)"),
            new Flashcard("SQL & Backend", "WHERE vs HAVING in SQL?",
                    "`WHERE` filters individual rows BEFORE `GROUP BY` (no aggregates allowed).\n`HAVING` filters aggregated groups AFTER `GROUP BY` (e.g. `HAVING COUNT(*) > 5`)."),
            new Flashcard("SQL & Backend", "DELETE vs TRUNCATE vs DROP?",
                    "DELETE: DML, row-by-row, supports WHERE.\nTRUNCATE: Fast DDL, wipes all rows, keeps table structure.\nDROP: DDL, deletes entire table & structure."),
            new Flashcard("SQL & Backend", "Dependency Injection (DI) & @RestController?",
                    "DI: Container injects dependencies (@Autowired / constructor) for loose coupling & easy testing.\n@RestController = @Controller + @ResponseBody (returns JSON directly).")
    );

    public static final List<String> BATTLE_PLAN_STEPS = List.of(
            "Day 1 Morning: Complete Lesson 01 + Solve Grind 01 (Prime, Overflow Reverse, Fibonacci, GCD)",
            "Day 1 Afternoon: Complete Lesson 02 + Solve Grind 02 (Palindrome, Anagram, First Unique Char, Reverse Words, Compression)",
            "Day 1 Evening: Solve Grind 03 Arrays (2nd Largest, Move Zeroes, Rotate Right K, Kadane's Max Subarray, Missing Number)",
            "Day 1 Night: Play Core Java Output-Trap Quiz (Questions 1-10) until 10/10",
            "Day 2 Morning: Complete Lesson 03 + Solve Grind 04 OOP Lab (BankAccount, Payable Polymorphism, equals/hashCode)",
            "Day 2 Afternoon: Complete Lesson 04 + Solve Grind 05 Collections (Two Sum, Valid Brackets, Group Anagrams, Top K, Longest Seq)",
            "Day 2 Evening: Complete Lessons 05-07 + Solve Grind 06 Streams & Grind 07 (Move '#', Borrows, Duplicates, Linked List, Matrix, 1D DP)",
            "Day 2 Night: Score 10/10 on SQL & Backend Quiz + Master all 25 Rapid-Fire Interview Flashcards"
    );
}
