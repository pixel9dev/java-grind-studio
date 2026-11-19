# Java Drive Rapid-Fire Interview Cheat Sheet (Top 25 Q&A)

Read these **out loud** on Day 2 evening and the morning of your drive. Keep answers crisp, structured, and concrete!

---

## 1. Core Java & JVM Memory

1. **Difference between JDK, JRE, and JVM?**
   - **JVM** executes `.class` bytecode on the host OS (via interpreter + JIT compiler) and manages memory/GC.
   - **JRE** = JVM + standard core libraries (`java.lang`, `java.util`) needed to *run* Java apps.
   - **JDK** = JRE + compiler (`javac`), debugger, and tools needed to *develop* Java apps.

2. **Why is Java platform-independent, and is JVM platform-independent?**
   - Java **bytecode** (`.class`) is platform-independent ("Write Once, Run Anywhere"), whereas the **JVM itself is platform-dependent** (Windows JVM differs from Linux JVM).

3. **Stack vs Heap memory in Java?**
   - **Stack** is per-thread, fast LIFO memory storing method frames, primitive local variables, and object references.
   - **Heap** is shared across all threads and stores all actual objects (`new ...`), arrays, and the String Constant Pool; reclaimed by Garbage Collection.

4. **Is Java Pass-by-Reference or Pass-by-Value?**
   - **100% Pass-by-Value.** For primitives, a copy of the value is passed. For objects, a **copy of the reference (address)** is passed — so mutating `obj.field` affects the caller's object, while reassigning `obj = new Foo()` inside the method does NOT affect the caller's variable.

5. **`==` vs `.equals()`?**
   - `==` compares primitive values OR memory addresses (whether two references point to the exact same Heap object).
   - `.equals()` compares logical value equality (overridden by `String`, `Integer`, `List`, `record`, etc.).

6. **Why is `String` immutable in Java?**
   - Enables the **String Constant Pool** (memory savings), makes `String` inherently **thread-safe**, allows **caching `hashCode()`** (making `String` the fastest `HashMap` key), and prevents security tampering (file paths, DB URLs, class names).

7. **`String` vs `StringBuilder` vs `StringBuffer`?**
   - `String`: Immutable; `+` inside a loop is $O(n^2)$ due to repeated copying.
   - `StringBuilder`: Mutable, **not** synchronized, fast $O(1)$ amortized `.append()` — use in 99% of single-threaded code.
   - `StringBuffer`: Mutable, synchronized (thread-safe), slower legacy class.

8. **`final` vs `finally` vs `finalize()`?**
   - `final`: Keyword — `final` variable can't be reassigned, `final` method can't be overridden, `final` class (`String`) can't be inherited.
   - `finally`: Block after `try/catch` that always executes for resource cleanup.
   - `finalize()`: Deprecated `Object` method formerly called before GC (replaced by `try-with-resources`).

---

## 2. Object-Oriented Programming (OOPs)

9. **Explain the 4 Pillars of OOP with one real example.**
   - **Encapsulation**: Hiding internal fields (`private double balance`) behind validated methods (`deposit()`, `withdraw()`).
   - **Abstraction**: Exposing *what* an object does while hiding *how* (`PaymentGateway.charge()` hides UPI vs Card network calls).
   - **Inheritance**: Reusing/extending common state and behavior (`SavingsAccount extends BankAccount`).
   - **Polymorphism**: One interface, multiple implementations — `Payable.monthlyPay()` behaves differently at runtime for `FullTimeEmployee` vs `Freelancer`.

10. **Method Overloading vs Method Overriding?**
    - **Overloading (Compile-time)**: Same method name in the same class with **different parameter types/count**. Return type alone CANNOT overload.
    - **Overriding (Runtime)**: Subclass provides its own implementation of an inherited method with the **exact same signature** (covariant return type allowed; access modifier cannot be more restrictive).

11. **Can we override a `static` or `private` method?**
    - **No.** `private` methods are invisible to subclasses, and `static` methods belong to the class (resolved at compile time -> **method hiding**, not overriding).

12. **`abstract class` vs `interface` (Java 8+)?**
    - **Abstract class**: Can have instance fields (`protected`/`private`), constructors, and partial implementation. Single inheritance (`extends` one class). Use for *"is-a"* shared base state.
    - **Interface**: Only `public static final` constants, no constructors, abstract + `default`/`static` methods. Multiple implementation (`implements A, B`). Use for capabilities/contracts (`Comparable`, `Runnable`, `Payable`).

13. **What is the `equals()` and `hashCode()` contract?**
    - If `a.equals(b)` is `true`, then `a.hashCode() == b.hashCode()` **MUST** be `true`!
    - The converse is not required (two unequal objects can collide in hashCode). Breaking this causes lost keys/duplicates in `HashMap` and `HashSet`.

---

## 3. Collections Framework & Generics

14. **How does `HashMap` work internally (Java 8+)?**
    - Computes `hash(key.hashCode())` and maps to bucket index `(n - 1) & hash`.
    - Bucket holds a linked list of `Node<K,V>`. On collision (`==` bucket), walks nodes checking `hash` and `key.equals(...)`.
    - **Java 8 upgrade**: When a single bucket reaches **>= 8 nodes** (and table capacity >= 64), the linked list converts into a **Red-Black Tree**, improving worst-case lookup from $O(n)$ to **$O(\log n)$**! Resizes (2x) when size exceeds `capacity * loadFactor (0.75)`.

15. **`ArrayList` vs `LinkedList`?**
    - `ArrayList`: Dynamic contiguous array -> $O(1)$ `get(i)` random access, cache-friendly, $O(n)$ middle insert/delete. Preferred 95% of the time.
    - `LinkedList`: Doubly-linked nodes -> $O(n)$ `get(i)`, high pointer memory overhead, $O(1)$ add/remove at ends (though `ArrayDeque` is faster for queues/stacks!).

16. **`HashMap` vs `Hashtable` vs `ConcurrentHashMap`?**
    - `HashMap`: Not thread-safe, allows **1 `null` key** and multiple `null` values, fast.
    - `Hashtable`: Legacy, locks the **entire map** on every call, no `null` key/value.
    - `ConcurrentHashMap`: Thread-safe with fine-grained bucket/CAS locking (high concurrency!), no `null` key/value.

17. **`Comparable` vs `Comparator`?**
    - `Comparable<T>` (`compareTo(T other)`): Implemented *inside* the class to define its single **natural ordering** (e.g., `String`, `Integer`).
    - `Comparator<T>` (`compare(T a, T b)`): External strategy/lambda (`Comparator.comparingInt(...)`) allowing **multiple custom sort orders** without modifying the class.

18. **Fail-Fast vs Fail-Safe iterators?**
    - **Fail-Fast** (`ArrayList`, `HashMap`): Throws `ConcurrentModificationException` if the collection is structurally modified while iterating (except via `Iterator.remove()` or `removeIf()`).
    - **Fail-Safe** (`CopyOnWriteArrayList`, `ConcurrentHashMap`): Iterates over a snapshot or weakly-consistent view without throwing.

---

## 4. Exceptions, Streams & Multithreading

19. **Checked vs Unchecked Exceptions?**
    - **Checked** (subclasses of `Exception` excluding `RuntimeException`, e.g. `IOException`, `SQLException`): Verified at **compile time** — must `catch` or declare `throws`.
    - **Unchecked** (`RuntimeException` subclasses: `NullPointerException`, `IllegalArgumentException`, `ArrayIndexOutOfBoundsException`): Occur at runtime due to programming bugs.

20. **What is `try-with-resources`?**
    - `try (BufferedReader br = ...) { ... }` automatically calls `.close()` on any `AutoCloseable` resource in reverse order when exiting the block, replacing messy `finally` boilerplate.

21. **Intermediate vs Terminal operations in Java Streams?**
    - **Intermediate** (`filter`, `map`, `flatMap`, `distinct`, `sorted`): **Lazy** — return a new `Stream` and do nothing until a terminal operation is invoked.
    - **Terminal** (`toList()`, `collect()`, `count()`, `findFirst()`, `reduce()`, `forEach()`): Trigger pipeline execution and close the stream (a stream **cannot be reused**).

22. **`map()` vs `flatMap()` in Streams?**
    - `map()` transforms 1 element -> 1 element (`Stream<List<String>>`).
    - `flatMap()` transforms 1 element -> a Stream of elements and **flattens** them into a single stream (`Stream<String>`).

23. **`Runnable` vs `Callable`?**
    - `Runnable.run()` returns `void` and cannot throw checked exceptions.
    - `Callable<V>.call()` returns a result `V` (wrapped in `Future<V>` via `ExecutorService.submit()`) and can throw checked exceptions.

24. **`synchronized` vs `volatile`?**
    - `volatile`: Guarantees **visibility** (reads/writes go straight to main memory, no stale CPU cache), NOT atomicity (`count++` is still unsafe!).
    - `synchronized`: Guarantees **both mutual exclusion (atomicity) AND visibility** via intrinsic monitor locks.

25. **Java 17 Features Worth Mentioning in Interviews!**
    - `record` (concise immutable data classes), Switch Expressions (`case A -> ...`), Pattern Matching for `instanceof` (`if (obj instanceof String s)`), Text Blocks (`"""..."""`), and `sealed` classes (`permits` restricted hierarchy).

---

## 5. Fast Complexity Cheat Table (Coding Round)

| Data Structure / Pattern | Lookup | Insert / Delete | Best Used When... |
|---|---|---|---|
| `Array` (`int[]`) | $O(1)$ by index | Fixed size | Two pointers, sliding window, prefix sum, `freq[26]` |
| `ArrayList<E>` | $O(1)$ by index | $O(1)$ amortized end | Dynamic result list, sorting |
| `HashSet<E>` / `HashMap<K,V>` | $O(1)$ average | $O(1)$ average | Two Sum, deduplication, frequency counts, $O(n)$ streak check |
| `TreeSet<E>` / `TreeMap<K,V>` | $O(\log n)$ | $O(\log n)$ | Sorted keys, `floorKey()` / `ceilingKey()` range queries |
| `ArrayDeque<E>` | $O(1)$ ends | $O(1)$ ends | Stack (valid brackets, monotonic stack) & Queue (BFS) |
| `PriorityQueue<E>` | $O(1)$ `peek()` | $O(\log n)$ | Top-K elements, K-th largest, Dijkstra |
