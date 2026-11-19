package javagrind.app;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javagrind.enterprise.EnterpriseSkill;
import java.util.stream.Stream;

/**
 * Compiles and executes user-written Java code directly inside the app using JDK 17's
 * built-in `javax.tools.JavaCompiler`, isolated in a fresh ClassLoader with a 2.5s timeout.
 */
@EnterpriseSkill(
        category = "Compiler API, ClassLoaders & Sandboxing",
        name = "Dynamic JDK 17 In-Process Compiler & URLClassLoader Sandbox",
        summary = "Compiles Java source via javax.tools.JavaCompiler into isolated URLClassLoaders with Future timeouts."
)
public final class LiveJavaEngine {

    private static final long EXECUTION_TIMEOUT_MS = 2500L;

    private LiveJavaEngine() {}

    public record RunOutput(boolean compiledOk, boolean timedOut, String output, long elapsedMs) {}

    @FunctionalInterface
    public interface ClassConsumer<R> {
        R accept(Class<?> loadedClass) throws Exception;
    }

    /**
     * Compiles `public class Playground` and runs `Playground.main(String[])`, capturing stdout/stderr.
     */
    public static RunOutput compileAndRunPlayground(String sourceCode) {
        long start = System.currentTimeMillis();
        Path tempDir = null;
        try {
            tempDir = Files.createTempDirectory("javagrind_play_");
            String compileErrors = compileSource(tempDir, "Playground", sourceCode);
            if (compileErrors != null) {
                return new RunOutput(false, false, compileErrors, System.currentTimeMillis() - start);
            }

            Path dirRef = tempDir;
            return runWithTimeout(start, () -> {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                PrintStream capture = new PrintStream(buffer, true, StandardCharsets.UTF_8);
                PrintStream origOut = System.out;
                PrintStream origErr = System.err;
                try (URLClassLoader loader = new URLClassLoader(
                        new URL[]{dirRef.toUri().toURL()}, LiveJavaEngine.class.getClassLoader())) {
                    System.setOut(capture);
                    System.setErr(capture);
                    Class<?> cls = Class.forName("Playground", true, loader);
                    Method mainMethod = cls.getMethod("main", String[].class);
                    mainMethod.invoke(null, (Object) new String[0]);
                } catch (InvocationTargetException ite) {
                    Throwable cause = ite.getTargetException();
                    cause.printStackTrace(capture);
                } finally {
                    System.setOut(origOut);
                    System.setErr(origErr);
                }
                String text = buffer.toString(StandardCharsets.UTF_8);
                if (text.isEmpty()) {
                    text = "(Program finished cleanly with no printed output.)";
                }
                return text;
            });
        } catch (Exception ex) {
            return new RunOutput(false, false, "Engine error: " + ex.getMessage(),
                    System.currentTimeMillis() - start);
        } finally {
            deleteQuietly(tempDir);
        }
    }

    /**
     * Compiles `public class Solution` and passes the loaded Class<?> to `tester` under a 2.5s timeout.
     */
    public static RunOutput compileAndVerifySolution(String sourceCode, ClassConsumer<String> tester) {
        long start = System.currentTimeMillis();
        Path tempDir = null;
        try {
            tempDir = Files.createTempDirectory("javagrind_sol_");
            String compileErrors = compileSource(tempDir, "Solution", sourceCode);
            if (compileErrors != null) {
                return new RunOutput(false, false, compileErrors, System.currentTimeMillis() - start);
            }

            Path dirRef = tempDir;
            return runWithTimeout(start, () -> {
                try (URLClassLoader loader = new URLClassLoader(
                        new URL[]{dirRef.toUri().toURL()}, LiveJavaEngine.class.getClassLoader())) {
                    Class<?> solClass = Class.forName("Solution", true, loader);
                    return tester.accept(solClass);
                }
            });
        } catch (Exception ex) {
            return new RunOutput(false, false, "Engine error: " + ex.getMessage(),
                    System.currentTimeMillis() - start);
        } finally {
            deleteQuietly(tempDir);
        }
    }

    private static String compileSource(Path workDir, String className, String sourceCode) throws IOException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            return """
                   ERROR: No JDK compiler (`javac`) detected in the active Java runtime!
                   In IntelliJ IDEA: Go to File -> Project Structure -> Project -> SDK
                   and select JDK 17 (e.g. Microsoft OpenJDK 17 or Eclipse Temurin 17), NOT a JRE 8!""";
        }

        Path javaFile = workDir.resolve(className + ".java");
        Files.writeString(javaFile, sourceCode, StandardCharsets.UTF_8);

        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        try (StandardJavaFileManager fileManager =
                     compiler.getStandardFileManager(diagnostics, Locale.ENGLISH, StandardCharsets.UTF_8)) {
            Iterable<? extends JavaFileObject> units = fileManager.getJavaFileObjectsFromFiles(
                    List.of(javaFile.toFile()));
            String classPath = System.getProperty("java.class.path", "");
            List<String> options = List.of(
                    "-encoding", "UTF-8",
                    "--release", "17",
                    "-classpath", classPath + File.pathSeparator + workDir.toAbsolutePath(),
                    "-d", workDir.toAbsolutePath().toString()
            );

            Boolean ok = compiler.getTask(null, fileManager, diagnostics, options, null, units).call();
            if (Boolean.TRUE.equals(ok)) {
                return null;
            }
        }

        StringBuilder sb = new StringBuilder("COMPILATION ERROR:\n");
        sb.append("------------------------------------------------------------\n");
        for (Diagnostic<? extends JavaFileObject> d : diagnostics.getDiagnostics()) {
            if (d.getKind() == Diagnostic.Kind.ERROR) {
                sb.append("Line ").append(d.getLineNumber())
                        .append(": ").append(d.getMessage(Locale.ENGLISH)).append('\n');
            }
        }
        return sb.toString();
    }

    private static RunOutput runWithTimeout(long startMillis, Callable<String> task) {
        ExecutorService exec = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "JavaGrind-Sandbox");
            t.setDaemon(true);
            return t;
        });
        Future<String> fut = exec.submit(task);
        try {
            String output = fut.get(EXECUTION_TIMEOUT_MS, TimeUnit.MILLISECONDS);
            return new RunOutput(true, false, output, System.currentTimeMillis() - startMillis);
        } catch (TimeoutException te) {
            fut.cancel(true);
            return new RunOutput(true, true,
                    "TIME LIMIT EXCEEDED (> 2.5 seconds)!\n"
                            + "Check your `while` / `for` loop termination condition for an infinite loop.",
                    System.currentTimeMillis() - startMillis);
        } catch (ExecutionException ee) {
            Throwable cause = ee.getCause() != null ? ee.getCause() : ee;
            if (cause instanceof InvocationTargetException ite && ite.getTargetException() != null) {
                cause = ite.getTargetException();
            }
            return new RunOutput(false, false,
                    "RUNTIME EXCEPTION: " + cause.getClass().getSimpleName() + ": " + cause.getMessage(),
                    System.currentTimeMillis() - startMillis);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            return new RunOutput(false, false, "Execution interrupted.", System.currentTimeMillis() - startMillis);
        } finally {
            exec.shutdownNow();
        }
    }

    private static void deleteQuietly(Path dir) {
        if (dir == null || !Files.exists(dir)) return;
        try (Stream<Path> walk = Files.walk(dir)) {
            walk.sorted(Comparator.reverseOrder()).forEach(p -> {
                try {
                    Files.deleteIfExists(p);
                } catch (IOException ignored) {
                }
            });
        } catch (IOException ignored) {
        }
    }
}
