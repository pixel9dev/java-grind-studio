package javagrind.learn;

import javagrind.util.TestRunner;
import java.util.List;

/**
 * MODULE 3 (LEARN): OOPs 4 Pillars, Abstract vs Interface, Static Hiding & Java 17 Records
 *
 * In technical interviews, ~40% of verbal questions come directly from this file!
 */
public class L03_OOPPillarsAndJava17 {

    // 1. INTERFACE: 100% contract; fields are implicitly `public static final`
    interface PaymentGateway {
        boolean charge(double amount); // implicitly `public abstract`

        // Java 8+ allows `default` methods so interfaces can evolve without breaking implementors
        default String receiptPrefix() {
            return "TXN-";
        }
    }

    // 2. ABSTRACT CLASS: Can hold state (fields), constructors, AND both abstract & concrete methods
    static abstract class Account {
        private final String owner; // ENCAPSULATION: private state, controlled access
        protected double balance;

        Account(String owner, double initialBalance) {
            this.owner = owner;
            this.balance = initialBalance;
        }

        public String getOwner() { return owner; }
        public double getBalance() { return balance; }

        // Abstract method: subclasses MUST implement their own rule
        public abstract String accountType();

        // DRIVE TRAP: Static methods belong to the CLASS, not the object -> HIDDEN, not overridden!
        public static String category() { return "GenericAccount"; }
    }

    // 3. INHERITANCE + POLYMORPHISM: `extends` ONE class, `implements` MANY interfaces
    static class UpiWalletAccount extends Account implements PaymentGateway {
        private final String upiId;

        UpiWalletAccount(String owner, double initialBalance, String upiId) {
            super(owner, initialBalance); // MUST be first statement in constructor!
            this.upiId = upiId;
        }

        @Override
        public String accountType() {
            return "UPI(" + upiId + ")";
        }

        @Override
        public boolean charge(double amount) {
            if (amount <= 0 || amount > balance) return false;
            balance -= amount;
            return true;
        }

        // Method Overloading (Compile-Time Polymorphism): same name, different parameter list
        public boolean charge(double amount, double cashback) {
            if (!charge(amount)) return false;
            balance += cashback;
            return true;
        }

        public static String category() { return "DigitalWallet"; }
    }

    // 4. JAVA 16+ RECORD: Immutable data carrier in ONE line!
    // Auto-generates: private final fields, constructor, getters `id()`/`name()`, equals(), hashCode(), toString()!
    record Candidate(int id, String name, double cgpa) {}

    public static void main(String[] args) {
        TestRunner.printBanner("LEARN 03: Object-Oriented Programming & Modern Java 17");

        runtimeVsCompileTimePolymorphism();
        staticMethodHidingTrap();
        java17RecordsAndPatternMatching();

        System.out.println("\nReady to practice OOP? Click -> (G04_OOPDesignGrind.java:1)");
    }

    private static void runtimeVsCompileTimePolymorphism() {
        TestRunner.printSection("1. Upcasting, Dynamic Method Dispatch & Overloading");
        // Reference type is `Account`, actual Heap object is `UpiWalletAccount`
        Account acc = new UpiWalletAccount("Aarav", 1000.0, "aarav@okbank");

        // Runtime Polymorphism: JVM looks at ACTUAL Heap object (`UpiWalletAccount`) at runtime!
        System.out.println("acc.accountType() -> " + acc.accountType());

        UpiWalletAccount wallet = (UpiWalletAccount) acc;
        wallet.charge(250.0, 25.0); // Calls overloaded 2-arg method
        System.out.println("Balance after 250 charge + 25 cashback: " + wallet.getBalance()
                + " | Receipt: " + wallet.receiptPrefix() + "001");
    }

    @SuppressWarnings("static")
    private static void staticMethodHidingTrap() {
        TestRunner.printSection("2. DRIVE TRAP: Static Methods Do NOT Override!");
        Account refAsParent = new UpiWalletAccount("Neha", 500.0, "neha@upi");
        UpiWalletAccount refAsChild = (UpiWalletAccount) refAsParent;

        // Static calls are resolved at COMPILE TIME using the variable's declared type!
        System.out.println("refAsParent.category() -> " + refAsParent.category() + " (Resolved on Account!)");
        System.out.println("refAsChild.category()  -> " + refAsChild.category() + " (Resolved on UpiWalletAccount!)");
    }

    private static void java17RecordsAndPatternMatching() {
        TestRunner.printSection("3. Bonus Points in Interviews: Java 17 `record` & Pattern Matching `instanceof`");
        Candidate c1 = new Candidate(101, "Riya", 8.9);
        Candidate c2 = new Candidate(101, "Riya", 8.9);

        System.out.println("Record toString()       : " + c1);
        System.out.println("Record value equals()   : " + c1.equals(c2) + " (Auto-generated value equality!)");

        List<Object> items = List.of("Hello Drive", 404, c1);
        for (Object obj : items) {
            // Java 16+ pattern matching `instanceof` — no manual cast needed!
            if (obj instanceof String s) {
                System.out.println("Matched String of len " + s.length() + ": " + s);
            } else if (obj instanceof Candidate cand) {
                System.out.println("Matched Candidate: " + cand.name() + " (CGPA " + cand.cgpa() + ")");
            }
        }
    }
}
