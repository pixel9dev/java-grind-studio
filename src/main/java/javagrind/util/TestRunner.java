package javagrind.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Zero-dependency test harness for the Java 2-Day Grind project.
 * Supports clickable IntelliJ IDEA console links like (FileName.java:42).
 */
public final class TestRunner {

    public static final String RESET = "\u001B[0m";
    public static final String GREEN = "\u001B[32m";
    public static final String RED = "\u001B[31m";
    public static final String YELLOW = "\u001B[33m";
    public static final String CYAN = "\u001B[36m";
    public static final String BOLD = "\u001B[1m";
    public static final String DIM = "\u001B[2m";

    private TestRunner() {}

    /**
     * Marker exception used inside starter stubs before you write your solution.
     */
    public static final class TodoException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public TodoException() {
            super("TODO: Implement this method");
        }
    }

    /**
     * Placeholder used in starter methods. Delete the `return TestRunner.todo();`
     * line when you are ready to write your solution!
     */
    public static <T> T todo() {
        throw new TodoException();
    }

    public record ModuleResult(String moduleName, int passed, int failed, int todoCount) {
        public int total() {
            return passed + failed + todoCount;
        }
    }

    @FunctionalInterface
    public interface ProblemCheck {
        void verify(CaseVerifier v) throws Exception;
    }

    public static final class CaseVerifier {
        private int caseCount = 0;
        private String failureMessage = null;

        public <T> void assertEquals(String callExpr, T expected, T actual) {
            if (failureMessage != null) {
                return;
            }
            caseCount++;
            if (!deepEquals(expected, actual)) {
                failureMessage = callExpr
                        + "\n             Expected: " + formatValue(expected)
                        + "\n             Actual  : " + formatValue(actual);
            }
        }

        public void assertTrue(String callExpr, boolean condition) {
            assertEquals(callExpr, true, condition);
        }

        public void assertFalse(String callExpr, boolean condition) {
            assertEquals(callExpr, false, condition);
        }

        public void assertThrows(String callExpr, Class<? extends Throwable> expectedType, Runnable action) {
            if (failureMessage != null) {
                return;
            }
            caseCount++;
            try {
                action.run();
                failureMessage = callExpr
                        + "\n             Expected exception: " + expectedType.getSimpleName()
                        + "\n             Actual            : No exception was thrown";
            } catch (TodoException todo) {
                throw todo;
            } catch (Throwable t) {
                if (!expectedType.isInstance(t)) {
                    failureMessage = callExpr
                            + "\n             Expected exception: " + expectedType.getSimpleName()
                            + "\n             Actual exception  : " + t.getClass().getSimpleName() + " (" + t.getMessage() + ")";
                }
            }
        }
    }

    private record ProblemEntry(String id, String title, String hint, ProblemCheck check) {}

    public static final class Suite {
        private final String moduleName;
        private final List<ProblemEntry> problems = new ArrayList<>();

        public Suite(String moduleName) {
            this.moduleName = moduleName;
        }

        public Suite problem(String id, String title, String hint, ProblemCheck check) {
            problems.add(new ProblemEntry(id, title, hint, check));
            return this;
        }

        public ModuleResult run(boolean verbose) {
            if (verbose) {
                printBanner(moduleName);
            }
            int passed = 0;
            int failed = 0;
            int todoCount = 0;

            for (ProblemEntry p : problems) {
                CaseVerifier verifier = new CaseVerifier();
                try {
                    p.check().verify(verifier);
                    if (verifier.failureMessage == null) {
                        passed++;
                        if (verbose) {
                            System.out.printf("  %s[PASS]%s %-6s %-34s %s(%d test cases)%s%n",
                                    GREEN, RESET, p.id(), p.title(), DIM, verifier.caseCount, RESET);
                        }
                    } else {
                        failed++;
                        if (verbose) {
                            System.out.printf("  %s[FAIL]%s %-6s %s%n", RED, RESET, p.id(), p.title());
                            System.out.printf("         Case : %s%n", verifier.failureMessage);
                            System.out.printf("         %sHint : %s%s%n", YELLOW, p.hint(), RESET);
                        }
                    }
                } catch (TodoException todo) {
                    todoCount++;
                    if (verbose) {
                        String loc = findProjectLocation(todo);
                        System.out.printf("  %s[TODO]%s %-6s %-34s -> Open %s%n",
                                YELLOW, RESET, p.id(), p.title(), loc);
                    }
                } catch (Throwable t) {
                    failed++;
                    if (verbose) {
                        String loc = findProjectLocation(t);
                        System.out.printf("  %s[ERR ]%s %-6s %s%n", RED, RESET, p.id(), p.title());
                        System.out.printf("         Threw %s: %s at %s%n",
                                t.getClass().getSimpleName(), t.getMessage(), loc);
                        System.out.printf("         %sHint : %s%s%n", YELLOW, p.hint(), RESET);
                    }
                }
            }

            ModuleResult res = new ModuleResult(moduleName, passed, failed, todoCount);
            if (verbose) {
                System.out.println("  ------------------------------------------------------------------------");
                System.out.printf("  Progress: %s  %d/%d passed",
                        progressBar(passed, res.total(), 20), passed, res.total());
                if (failed > 0) {
                    System.out.printf(" | %s%d failing%s", RED, failed, RESET);
                }
                if (todoCount > 0) {
                    System.out.printf(" | %s%d remaining%s", YELLOW, todoCount, RESET);
                }
                System.out.println("\n");
            }
            return res;
        }
    }

    public static void printBanner(String title) {
        System.out.println(CYAN + BOLD + "==========================================================================" + RESET);
        System.out.println(CYAN + BOLD + "  " + title + RESET);
        System.out.println(CYAN + BOLD + "==========================================================================" + RESET);
    }

    public static void printSection(String title) {
        System.out.println("\n" + BOLD + CYAN + ">>> " + title + RESET);
    }

    public static String progressBar(int done, int total, int width) {
        if (total <= 0) {
            return "[" + "-".repeat(width) + "]";
        }
        int filled = (int) Math.round(((double) done / total) * width);
        filled = Math.max(0, Math.min(width, filled));
        return GREEN + "[" + "#".repeat(filled) + RESET + DIM + ".".repeat(width - filled) + RESET + GREEN + "]" + RESET;
    }

    private static String findProjectLocation(Throwable t) {
        for (StackTraceElement el : t.getStackTrace()) {
            String cls = el.getClassName();
            if (cls.startsWith("javagrind.") && !cls.equals(TestRunner.class.getName())
                    && !cls.startsWith(TestRunner.class.getName() + "$")) {
                return "(" + el.getFileName() + ":" + el.getLineNumber() + ")";
            }
        }
        StackTraceElement[] trace = t.getStackTrace();
        if (trace.length > 0) {
            return "(" + trace[0].getFileName() + ":" + trace[0].getLineNumber() + ")";
        }
        return "(unknown)";
    }

    private static boolean deepEquals(Object a, Object b) {
        if (a == b) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        if (a instanceof int[] ia && b instanceof int[] ib) {
            return Arrays.equals(ia, ib);
        }
        if (a instanceof long[] la && b instanceof long[] lb) {
            return Arrays.equals(la, lb);
        }
        if (a instanceof double[] da && b instanceof double[] db) {
            return Arrays.equals(da, db);
        }
        if (a instanceof boolean[] ba && b instanceof boolean[] bb) {
            return Arrays.equals(ba, bb);
        }
        if (a instanceof char[] ca && b instanceof char[] cb) {
            return Arrays.equals(ca, cb);
        }
        if (a instanceof Object[] oa && b instanceof Object[] ob) {
            return Arrays.deepEquals(oa, ob);
        }
        return Objects.equals(a, b);
    }

    private static String formatValue(Object val) {
        if (val == null) {
            return "null";
        }
        if (val instanceof String s) {
            return "\"" + s + "\"";
        }
        if (val instanceof Character c) {
            return "'" + c + "'";
        }
        if (val instanceof int[] arr) {
            return Arrays.toString(arr);
        }
        if (val instanceof long[] arr) {
            return Arrays.toString(arr);
        }
        if (val instanceof double[] arr) {
            return Arrays.toString(arr);
        }
        if (val instanceof boolean[] arr) {
            return Arrays.toString(arr);
        }
        if (val instanceof char[] arr) {
            return Arrays.toString(arr);
        }
        if (val instanceof Object[] arr) {
            return Arrays.deepToString(arr);
        }
        return String.valueOf(val);
    }
}
