package javagrind.learn;

import javagrind.util.TestRunner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * MODULE 6 (LEARN): JVM Architecture, Memory Model & Multithreading Essentials
 *
 * You rarely have to write complex threads in Round 2 coding,
 * BEFORE Round 3 interviews you MUST know JVM memory + synchronized vs volatile vs Atomic!
 */
public class L06_MultithreadingAndJVM {

    private static int unsafeCounter = 0;
    private static int syncedCounter = 0;
    private static final AtomicInteger atomicCounter = new AtomicInteger(0);

    private static synchronized void incrementSafely() {
        syncedCounter++;
    }

    public static void main(String[] args) throws Exception {
        TestRunner.printBanner("LEARN 06: JVM Internals, Garbage Collection & Concurrency");

        jvmArchitectureSummary();
        raceConditionVsThreadSafety();
        executorServiceAndCallable();

        System.out.println("\nReady to test your eye for tricky code? Click -> (OutputTrapQuiz.java:1)");
    }

    private static void jvmArchitectureSummary() {
        TestRunner.printSection("1. 60-Second JVM Architecture Recap (Memorize for Viva/Interview)");
        System.out.println("""
                  JDK = JRE + Compiler (javac) + Dev Tools
                  JRE = JVM + Core Standard Libraries (java.lang, java.util, ...)
                  JVM = Loads .class bytecode -> Verifies -> Interprets + JIT-compiles hot code to native CPU!

                  JVM MEMORY AREAS:
                  - Stack (Per Thread) : Method call frames, local primitives, object references. Fast LIFO.
                  - Heap  (Shared)     : ALL objects (`new ...`), arrays, and String Constant Pool. Cleaned by GC!
                  - Metaspace          : Class metadata, static method bytecode (replaced old PermGen in Java 8).
                """);
    }

    private static void raceConditionVsThreadSafety() throws InterruptedException {
        TestRunner.printSection("2. Race Condition (`count++` is NOT atomic!) vs `synchronized` & `AtomicInteger`");
        unsafeCounter = 0;
        syncedCounter = 0;
        atomicCounter.set(0);

        Runnable task = () -> {
            for (int i = 0; i < 10_000; i++) {
                unsafeCounter++;            // BUG: read -> +1 -> write can interleave between threads!
                incrementSafely();          // Lock-protected mutual exclusion
                atomicCounter.incrementAndGet(); // Lock-free CPU CAS (Compare-And-Swap)
            }
        };

        Thread t1 = new Thread(task, "Worker-1");
        Thread t2 = new Thread(task, "Worker-2");
        t1.start(); // NOTE: Calling t1.run() directly runs on MAIN thread (classic MCQ trap!). Use start()!
        t2.start();
        t1.join();
        t2.join();

        System.out.println("Expected = 20000 | unsafeCounter = " + unsafeCounter + " (Lost updates possible!)");
        System.out.println("Expected = 20000 | syncedCounter = " + syncedCounter + " (Always exact!)");
        System.out.println("Expected = 20000 | atomicCounter = " + atomicCounter.get() + " (Always exact!)");
        System.out.println("Key distinction: `volatile` guarantees VISIBILITY across threads, NOT atomicity of `x++`!");
    }

    private static void executorServiceAndCallable() throws Exception {
        TestRunner.printSection("3. Modern Thread Pools: ExecutorService + Callable<T> + Future<T>");
        ExecutorService pool = Executors.newFixedThreadPool(2);
        try {
            // Unlike Runnable (returns void), Callable<T> returns a value and can throw checked exceptions!
            Future<Integer> futureSum = pool.submit(() -> {
                int sum = 0;
                for (int i = 1; i <= 100; i++) sum += i;
                return sum;
            });
            System.out.println("Computed asynchronously on thread pool: sum(1..100) = " + futureSum.get());
        } finally {
            pool.shutdown();
        }
    }
}
