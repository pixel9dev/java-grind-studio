package javagrind;

import javagrind.app.CurriculumData;
import javagrind.app.JavaGrindApp;
import javagrind.app.LiveJavaEngine;
import javagrind.app.ProblemBank;
import javagrind.enterprise.AsyncPipelineOrchestrator;
import javagrind.enterprise.EmbeddedRestServer;
import javagrind.enterprise.ExecutionOutcome;
import javagrind.enterprise.SkillRegistryScanner;
import javagrind.grind.G01_WarmupAndMathGrind;
import javagrind.grind.G02_StringsGrind;
import javagrind.grind.G03_ArraysAndTwoPointersGrind;
import javagrind.grind.G04_OOPDesignGrind;
import javagrind.grind.G05_CollectionsAndHashingGrind;
import javagrind.grind.G06_StreamsAndExceptionsGrind;
import javagrind.grind.G07_LinkedListMatrixAndDpGrind;
import javagrind.solutions.S01_WarmupAndMathSolutions;
import javagrind.solutions.S02_StringsSolutions;
import javagrind.solutions.S03_ArraysAndTwoPointersSolutions;
import javagrind.solutions.S04_OOPDesignSolutions;
import javagrind.solutions.S05_CollectionsAndHashingSolutions;
import javagrind.solutions.S06_StreamsAndExceptionsSolutions;
import javagrind.solutions.S07_LinkedListMatrixAndDpSolutions;
import javagrind.util.TestRunner;
import javagrind.util.TestRunner.ModuleResult;
import java.awt.GraphicsEnvironment;
import java.util.List;

/**
 * ==========================================================================
 * START HERE! -> Click the green ▶ icon next to `main` (or press Shift+F10)
 * ==========================================================================
 * Launches the interactive **Java Grind Studio** Desktop Application!
 * (Pass `--cli` for terminal scoreboard, or `--verify-all` for full self-test.)
 */
public class MainDashboard {

    private record TrackItem(String dayLabel, String learnFile, String grindFile, ModuleResult result) {}

    public static void main(String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("--verify-solutions")) {
                verifyAllReferenceSolutions();
                return;
            }
            if (args[0].equalsIgnoreCase("--verify-app-engine")) {
                verifyAppEngine();
                return;
            }
            if (args[0].equalsIgnoreCase("--cli")) {
                runCliScoreboard();
                return;
            }
        }

        if (GraphicsEnvironment.isHeadless()) {
            runCliScoreboard();
            return;
        }

        System.out.println("Launching Java Grind Studio desktop app...");
        JavaGrindApp.launch();
    }

    private static void runCliScoreboard() {
        TestRunner.printBanner("JAVA 2-DAY PLACEMENT DRIVE - MASTER GRIND DASHBOARD");

        List<TrackItem> tracks = List.of(
                new TrackItem("Day 1 AM", "(L01_BasicsTypesAndMemory.java:1)",
                        "(G01_WarmupAndMathGrind.java:1)", G01_WarmupAndMathGrind.runTests(false)),
                new TrackItem("Day 1 PM", "(L02_StringsAndStringBuilder.java:1)",
                        "(G02_StringsGrind.java:1)", G02_StringsGrind.runTests(false)),
                new TrackItem("Day 1 EV", "(L01_BasicsTypesAndMemory.java:1)",
                        "(G03_ArraysAndTwoPointersGrind.java:1)", G03_ArraysAndTwoPointersGrind.runTests(false)),
                new TrackItem("Day 2 AM", "(L03_OOPPillarsAndJava17.java:1)",
                        "(G04_OOPDesignGrind.java:1)", G04_OOPDesignGrind.runTests(false)),
                new TrackItem("Day 2 PM", "(L04_CollectionsAndGenerics.java:1)",
                        "(G05_CollectionsAndHashingGrind.java:1)", G05_CollectionsAndHashingGrind.runTests(false)),
                new TrackItem("Day 2 EV", "(L05_ExceptionsAndStreams.java:1)",
                        "(G06_StreamsAndExceptionsGrind.java:1)", G06_StreamsAndExceptionsGrind.runTests(false)),
                new TrackItem("Day 2 NT", "(OnlineCodingTemplates.java:1)",
                        "(G07_LinkedListMatrixAndDpGrind.java:1)", G07_LinkedListMatrixAndDpGrind.runTests(false))
        );

        int totalPassed = 0;
        int totalProblems = 0;
        System.out.println();
        for (TrackItem item : tracks) {
            ModuleResult r = item.result();
            totalPassed += r.passed();
            totalProblems += r.total();
            System.out.printf("  [%s] %-53s %s %2d/%-2d -> %s%n",
                    item.dayLabel(), r.moduleName(),
                    TestRunner.progressBar(r.passed(), r.total(), 10),
                    r.passed(), r.total(), item.grindFile());
        }
        System.out.printf("%n  OVERALL CLI SCORE: %d / %d Solved%n", totalPassed, totalProblems);
    }

    private static void verifyAllReferenceSolutions() {
        List<ModuleResult> results = List.of(
                S01_WarmupAndMathSolutions.verify(false),
                S02_StringsSolutions.verify(false),
                S03_ArraysAndTwoPointersSolutions.verify(false),
                S04_OOPDesignSolutions.verify(false),
                S05_CollectionsAndHashingSolutions.verify(false),
                S06_StreamsAndExceptionsSolutions.verify(false),
                S07_LinkedListMatrixAndDpSolutions.verify(false)
        );
        int passed = results.stream().mapToInt(ModuleResult::passed).sum();
        int total = results.stream().mapToInt(ModuleResult::total).sum();
        System.out.printf("CLI Reference Solutions Verified: %d / %d passed.%n", passed, total);
        if (passed != total) System.exit(1);
    }

    private static void verifyAppEngine() {
        System.out.println("1. Verifying all " + CurriculumData.LESSONS.size() + " Live Lesson Playgrounds...");
        for (CurriculumData.Lesson lesson : CurriculumData.LESSONS) {
            LiveJavaEngine.RunOutput res = LiveJavaEngine.compileAndRunPlayground(lesson.playgroundCode());
            if (!res.compiledOk() || res.timedOut()) {
                System.err.println("FAILED Playground " + lesson.id() + ":\n" + res.output());
                System.exit(1);
            }
            System.out.println("  [OK] " + lesson.id() + " (" + res.elapsedMs() + " ms)");
        }

        System.out.println("2. Verifying all " + ProblemBank.PROBLEMS.size() + " In-App Coding Problem Suites...");
        for (ProblemBank.CodingProblem p : ProblemBank.PROBLEMS) {
            LiveJavaEngine.RunOutput res = ProblemBank.evaluate(p, p.solutionCode());
            if (!res.compiledOk() || res.timedOut() || !res.output().startsWith(ProblemBank.ALL_PASSED_TOKEN)) {
                System.err.println("FAILED Problem " + p.id() + ":\n" + res.output());
                System.exit(1);
            }
            System.out.println("  [OK] " + p.id() + " " + p.title() + " (" + res.elapsedMs() + " ms)");
        }

        System.out.println("3. Verifying Enterprise Subsystems (REST Server, HttpClient, RateLimiter, LRU, Async, Reflection)...");
        var skills = SkillRegistryScanner.scanAll();
        if (skills.size() < 8) {
            System.err.println("Expected >= 8 @EnterpriseSkill components, got " + skills.size());
            System.exit(1);
        }
        System.out.println("  [OK] Reflection scanner discovered " + skills.size() + " @EnterpriseSkill subsystems");

        var asyncOk = AsyncPipelineOrchestrator.executeParallelCheckout(
                "SKU-TEST", AsyncPipelineOrchestrator.DiscountStrategy.enterpriseTier(), false);
        var asyncFallback = AsyncPipelineOrchestrator.executeParallelCheckout(
                "SKU-TEST", AsyncPipelineOrchestrator.DiscountStrategy.enterpriseTier(), true);
        if (!(asyncOk instanceof ExecutionOutcome.Success<?>) || !(asyncFallback instanceof ExecutionOutcome.Success<?>)) {
            System.err.println("Async CompletableFuture pipeline failed!");
            System.exit(1);
        }
        System.out.println("  [OK] CompletableFuture parallel fan-out & fault fallback verified");

        try (EmbeddedRestServer server = new EmbeddedRestServer(() -> "{\"status\":\"ok\"}")) {
            int port = server.start();
            var r1 = server.invokeLocalGet("/api/v1/skills");
            var r2 = server.invokeLocalGet("/api/v1/skills");
            if (!(r1 instanceof ExecutionOutcome.Success<?>) || !(r2 instanceof ExecutionOutcome.Success<?>)) {
                System.err.println("REST /api/v1/skills call failed!");
                System.exit(1);
            }
            if (server.cacheStats().hits() < 1) {
                System.err.println("Expected ThreadSafeLruCache hit on second /api/v1/skills call!");
                System.exit(1);
            }
            System.out.println("  [OK] EmbeddedRestServer (port " + port + ") + HttpClient + ThreadSafeLruCache verified");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }

        System.out.println("SUCCESS: All " + CurriculumData.LESSONS.size() + " playgrounds, "
                + ProblemBank.PROBLEMS.size() + " problem suites, and enterprise subsystems passed!");
    }
}
