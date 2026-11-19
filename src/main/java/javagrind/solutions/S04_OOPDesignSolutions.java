package javagrind.solutions;

import javagrind.util.TestRunner;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/**
 * REFERENCE SOLUTIONS FOR GRIND 04 (OOP Design Lab).
 */
public class S04_OOPDesignSolutions {

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

        public void deposit(double amount) {
            if (amount <= 0) {
                throw new IllegalArgumentException("Amount must be positive");
            }
            balance += amount;
        }

        public void withdraw(double amount) {
            if (amount <= 0) {
                throw new IllegalArgumentException("Amount must be positive");
            }
            if (amount > balance) {
                throw new IllegalStateException("Insufficient funds");
            }
            balance -= amount;
        }
    }

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

        @Override
        public double monthlyPay() {
            return baseSalary + monthlyBonus;
        }
    }

    public static class Freelancer implements Payable {
        private final double hourlyRate;
        private final int hoursWorked;

        public Freelancer(double hourlyRate, int hoursWorked) {
            this.hourlyRate = hourlyRate;
            this.hoursWorked = hoursWorked;
        }

        @Override
        public double monthlyPay() {
            int regularHours = Math.min(hoursWorked, 160);
            int overtimeHours = Math.max(0, hoursWorked - 160);
            return (regularHours * hourlyRate) + (overtimeHours * hourlyRate * 1.5);
        }
    }

    public static double totalPayroll(List<Payable> staff) {
        double sum = 0.0;
        for (Payable p : staff) {
            sum += p.monthlyPay();
        }
        return sum;
    }

    public static class StudentKey {
        private final int rollNo;
        private final String email;

        public StudentKey(int rollNo, String email) {
            this.rollNo = rollNo;
            this.email = email == null ? "" : email;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (!(other instanceof StudentKey that)) return false;
            return this.rollNo == that.rollNo && this.email.equalsIgnoreCase(that.email);
        }

        @Override
        public int hashCode() {
            return Objects.hash(rollNo, email.toLowerCase(Locale.ROOT));
        }
    }

    public static TestRunner.ModuleResult verify(boolean verbose) {
        return new TestRunner.Suite("SOLUTIONS 04: OOP Design Lab")
                .problem("P4.1", "BankAccount", "", v -> {
                    BankAccount acc = new BankAccount("Kiran", 1000.0);
                    acc.deposit(500.0);
                    acc.withdraw(300.0);
                    v.assertEquals("balance", 1200.0, acc.getBalance());
                    v.assertThrows("overdraw", IllegalStateException.class, () -> acc.withdraw(5000));
                })
                .problem("P4.2", "Payable payroll", "", v -> {
                    Payable ft = new FullTimeEmployee(50_000, 10_000);
                    Payable fl = new Freelancer(100, 170);
                    v.assertEquals("totalPayroll", 77_500.0, totalPayroll(List.of(ft, fl)));
                })
                .problem("P4.3", "StudentKey HashSet", "", v -> {
                    Set<StudentKey> set = new HashSet<>();
                    set.add(new StudentKey(101, "Dev@Java.com"));
                    set.add(new StudentKey(101, "dev@java.com"));
                    v.assertEquals("deduped size", 1, set.size());
                })
                .run(verbose);
    }

    public static void main(String[] args) {
        verify(true);
    }
}
