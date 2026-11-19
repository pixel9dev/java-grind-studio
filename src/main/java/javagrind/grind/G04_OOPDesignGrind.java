package javagrind.grind;

import javagrind.util.TestRunner;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * ==========================================================================
 * GRIND 04: Object-Oriented Programming Design Lab (3 Problems)
 * ==========================================================================
 * Prerequisite: Run & read (L03_OOPPillarsAndJava17.java:1) first!
 * Stuck for >15 mins? Check (S04_OOPDesignSolutions.java:1).
 */
public class G04_OOPDesignGrind {

    // ----------------------------------------------------------------------
    // P4.1: Encapsulation & Validation — BankAccount
    // ----------------------------------------------------------------------
    public static class BankAccount {
        private final String accountHolder;
        private double balance;

        public BankAccount(String accountHolder, double initialBalance) {
            if (initialBalance < 0) {
                throw new IllegalArgumentException("Initial balance cannot be negative");
            }
            this.accountHolder = accountHolder;
            this.balance = initialBalance;
        }

        public String getAccountHolder() { return accountHolder; }
        public double getBalance() { return balance; }

        /**
         * TODO P4.1a:
         * - If `amount <= 0`, throw `new IllegalArgumentException("Amount must be positive")`.
         * - Otherwise add `amount` to `balance`.
         */
        public void deposit(double amount) {
            TestRunner.todo();
        }

        /**
         * TODO P4.1b:
         * - If `amount <= 0`, throw `new IllegalArgumentException("Amount must be positive")`.
         * - If `amount > balance`, throw `new IllegalStateException("Insufficient funds")`.
         * - Otherwise subtract `amount` from `balance`.
         */
        public void withdraw(double amount) {
            TestRunner.todo();
        }
    }

    // ----------------------------------------------------------------------
    // P4.2: Polymorphism & Interfaces — Company Payroll
    // ----------------------------------------------------------------------
    public interface Payable {
        double monthlyPay();
    }

    public static class FullTimeEmployee implements Payable {
        private final double baseSalary;
        private final double monthlyBonus;

        public FullTimeEmployee(double baseSalary, double monthlyBonus) {
            this.baseSalary = baseSalary;
            this.monthlyBonus = monthlyBonus;
        }

        /**
         * TODO P4.2a: Return `baseSalary + monthlyBonus`.
         */
        @Override
        public double monthlyPay() {
            return TestRunner.todo();
        }
    }

    public static class Freelancer implements Payable {
        private final double hourlyRate;
        private final int hoursWorked;

        public Freelancer(double hourlyRate, int hoursWorked) {
            this.hourlyRate = hourlyRate;
            this.hoursWorked = hoursWorked;
        }

        /**
         * TODO P4.2b:
         * - Regular hours up to 160 are paid at `hourlyRate`.
         * - Any overtime hours strictly above 160 are paid at `1.5 * hourlyRate`!
         * Example: rate=100, hours=170 -> (160 * 100) + (10 * 150) = 17500.0
         */
        @Override
        public double monthlyPay() {
            return TestRunner.todo();
        }
    }

    /**
     * TODO P4.2c: Sum `monthlyPay()` across every `Payable` in `staff` using polymorphism!
     */
    public static double totalPayroll(List<Payable> staff) {
        return TestRunner.todo();
    }

    // ----------------------------------------------------------------------
    // P4.3: The `equals()` and `hashCode()` Contract for HashMap / HashSet!
    // ----------------------------------------------------------------------
    public static class StudentKey {
        private final int rollNo;
        private final String email;

        public StudentKey(int rollNo, String email) {
            this.rollNo = rollNo;
            this.email = email == null ? "" : email;
        }

        public int getRollNo() { return rollNo; }
        public String getEmail() { return email; }

        /**
         * TODO P4.3a:
         * Two `StudentKey` objects are equal if and only if:
         * 1. `other` is an instance of `StudentKey`, AND
         * 2. `this.rollNo == that.rollNo`, AND
         * 3. `this.email.equalsIgnoreCase(that.email)`!
         */
        @Override
        public boolean equals(Object other) {
            return TestRunner.todo();
        }

        /**
         * TODO P4.3b:
         * Must be CONSISTENT with `equals`!
         * Hint: `Objects.hash(rollNo, email.toLowerCase())` — why `.toLowerCase()`?
         * Because "Dev@Java.com" and "dev@java.com" are equal, so they MUST have the SAME hashCode!
         */
        @Override
        public int hashCode() {
            return TestRunner.todo();
        }
    }

    // ==================== DO NOT EDIT BELOW THIS LINE (TEST HARNESS) ====================

    public static TestRunner.ModuleResult runTests(boolean verbose) {
        return new TestRunner.Suite("GRIND 04: OOP Design, Polymorphism & equals/hashCode")
                .problem("P4.1", "BankAccount encapsulation",
                        "Validate `amount <= 0` (IllegalArgumentException) and `amount > balance` (IllegalStateException).",
                        v -> {
                            BankAccount acc = new BankAccount("Kiran", 1000.0);
                            acc.deposit(500.0);
                            v.assertEquals("balance after deposit(500)", 1500.0, acc.getBalance());
                            acc.withdraw(300.0);
                            v.assertEquals("balance after withdraw(300)", 1200.0, acc.getBalance());
                            v.assertThrows("deposit(-10)", IllegalArgumentException.class, () -> acc.deposit(-10));
                            v.assertThrows("deposit(0)", IllegalArgumentException.class, () -> acc.deposit(0));
                            v.assertThrows("withdraw(5000)", IllegalStateException.class, () -> acc.withdraw(5000));
                        })
                .problem("P4.2", "Payable runtime polymorphism",
                        "Implement FullTimeEmployee.monthlyPay(), Freelancer overtime (>160h at 1.5x), and loop over staff.",
                        v -> {
                            Payable ft = new FullTimeEmployee(50_000, 10_000);
                            Payable flNormal = new Freelancer(100, 120);
                            Payable flOvertime = new Freelancer(100, 170);
                            v.assertEquals("FullTime(50k, 10k)", 60_000.0, ft.monthlyPay());
                            v.assertEquals("Freelancer(100, 120h)", 12_000.0, flNormal.monthlyPay());
                            v.assertEquals("Freelancer(100, 170h)", 17_500.0, flOvertime.monthlyPay());
                            v.assertEquals("totalPayroll", 89_500.0, totalPayroll(List.of(ft, flNormal, flOvertime)));
                        })
                .problem("P4.3", "StudentKey equals() & hashCode()",
                        "Use `Objects.hash(rollNo, email.toLowerCase())` so equal keys land in the same HashSet bucket!",
                        v -> {
                            StudentKey k1 = new StudentKey(101, "Dev@Java.com");
                            StudentKey k2 = new StudentKey(101, "dev@java.com");
                            StudentKey k3 = new StudentKey(102, "dev@java.com");

                            v.assertTrue("k1.equals(k2)", k1.equals(k2));
                            v.assertFalse("k1.equals(k3)", k1.equals(k3));
                            v.assertFalse("k1.equals(\"notAStudent\")", k1.equals("notAStudent"));
                            v.assertEquals("hashCode match for case-insensitive email", k1.hashCode(), k2.hashCode());

                            Set<StudentKey> set = new HashSet<>();
                            set.add(k1);
                            set.add(k2);
                            set.add(k3);
                            v.assertEquals("HashSet size with duplicate k1/k2", 2, set.size());
                        })
                .run(verbose);
    }

    public static void main(String[] args) {
        runTests(true);
    }
}
