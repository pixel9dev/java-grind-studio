# Java Placement Drive Playbook — Exam Strategy, Live SQL & Backend Notes

A focused playbook for clearing **Online Assessments + Technical Screen-Share Rounds + HR Rounds** in Java developer hiring drives.

---

## 1. Round-by-Round Strategy

1. **Stage 1 — Online Assessment (90–120 mins)**
   - **Aptitude, Logical & Verbal**: Check upfront if there is **no negative marking** — if none, never leave any MCQ blank! Watch sectional timers strictly.
   - **Technical MCQs** (~15–30 Qs): Core Java output traps (`OutputTrapQuiz.java`), OOPs, SQL/DBMS, and Java 8 (`SqlAndBackendQuiz.java`).
   - **Coding Section** (1–2 Problems, 20–45 mins):
     - Problem 1: Usually **String / Array / Digit logic** (Move `#` to Front, First Non-Repeating Char, Count Borrows, Second Largest, Palindrome, Anagram).
     - Problem 2: Usually **Array / Hashing / Matrix / 1D-DP** (Kadane's Max Subarray, Top-K Frequent, Valid Parentheses, Matrix Diagonals, Max Non-Adjacent Sum).
     - **Hidden Test Case Checklist**: Always guard against `null` / empty inputs, `n == 1`, negative numbers, and integer overflow (`long` accumulators)!

2. **Stage 2 — Technical Interview (30–45 mins, Live Screen-Share)**
   - **Project Walkthrough** (first 5 mins): Be ready to explain your architecture, database tables, API flow, and one bug/challenge you solved in your resume project.
   - **Core Java + OOPs + Collections + Java 8 Streams** (15 mins — covered in `INTERVIEW_CHEATSHEET.md`).
   - **Live Coding** (10 mins): Talk out loud while writing *First Non-Repeating Char*, *Find Duplicates*, *Reverse Words*, *Two Sum*, or *Reverse a Linked List*. State time & space complexity at the end!
   - **Live SQL Query Writing** (5–10 mins — memorize the 5 queries below!).

---

## 2. The 5 SQL Queries Interviewers Ask You to Write Live

### Query 1: Second Highest (or N-th Highest) Distinct Salary
```sql
-- Cleanest portable SQL for 2nd highest (handles ties properly!):
SELECT MAX(salary) AS second_highest_salary
FROM Employee
WHERE salary < (SELECT MAX(salary) FROM Employee);

-- General N-th highest (e.g. N = 2 or N = 3) using Window Function:
SELECT salary
FROM (
    SELECT salary, DENSE_RANK() OVER (ORDER BY salary DESC) AS rnk
    FROM Employee
) ranked
WHERE rnk = 2;
```

### Query 2: Department-Wise Headcount & Average Salary (Only Depts with > 5 Staff)
```sql
SELECT dept_id, COUNT(*) AS emp_count, AVG(salary) AS avg_salary
FROM Employee
GROUP BY dept_id
HAVING COUNT(*) > 5;
```

### Query 3: Find Duplicate Emails in a Users Table
```sql
SELECT email, COUNT(*) AS cnt
FROM Users
GROUP BY email
HAVING COUNT(*) > 1;
```

### Query 4: Highest Paid Employee in Each Department
```sql
SELECT e.name, e.dept_id, e.salary
FROM Employee e
JOIN (
    SELECT dept_id, MAX(salary) AS max_sal
    FROM Employee
    GROUP BY dept_id
) m ON e.dept_id = m.dept_id AND e.salary = m.max_sal;
```

### Query 5: `LEFT JOIN` — Find Employees Not Assigned to Any Valid Department
```sql
SELECT e.id, e.name
FROM Employee e
LEFT JOIN Department d ON e.dept_id = d.id
WHERE d.id IS NULL;
```

---

## 3. 3-Minute JDBC & REST Backend Crash Sheet

- **JDBC 5 Steps**: Load Driver -> `DriverManager.getConnection(url, user, pass)` -> `conn.prepareStatement("SELECT * FROM emp WHERE id = ?")` -> `ps.setInt(1, id); ResultSet rs = ps.executeQuery()` -> close via `try-with-resources`.
- **Why `PreparedStatement`?** Pre-compiled SQL execution plan + prevents **SQL Injection**.
- **Dependency Injection (DI) & IoC**: Instead of classes creating dependencies directly with `new`, the IoC container creates and injects beans (`@Autowired` / constructor injection), keeping classes loosely coupled and easy to test.
- **Core REST Annotations**:
  - `@RestController` (`@Controller` + `@ResponseBody`): Handles HTTP requests and serializes return values as JSON.
  - `@Service`: Business logic layer.
  - `@Repository`: Persistence / data-access layer.
- **`@PathVariable` vs `@RequestParam` vs `@RequestBody`**:
  - `/employees/101` -> `@PathVariable Long id`
  - `/employees?dept=IT` -> `@RequestParam String dept`
  - JSON POST payload -> `@RequestBody EmployeeDto dto`
- **HTTP Methods (`POST` vs `PUT` vs `PATCH`)**:
  - `POST`: Create resource (NOT idempotent).
  - `PUT`: Replace entire resource (Idempotent).
  - `PATCH`: Partial field update.
