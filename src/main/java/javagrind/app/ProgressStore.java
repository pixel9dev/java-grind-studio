package javagrind.app;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javagrind.enterprise.EnterpriseSkill;
import java.util.Properties;

/**
 * Persists user progress, saved code drafts, completed lessons, flashcards, and checklist items
 * into `.grind-progress.properties` in the workspace root.
 */
@EnterpriseSkill(
        category = "Java NIO.2 & Thread-Safe Persistence",
        name = "Synchronized NIO.2 State Repository",
        summary = "Thread-safe local state persistence using java.nio.file.Files, Path, and Properties."
)
public final class ProgressStore {

    private final Path storePath = Paths.get(".grind-progress.properties");
    private final Properties props = new Properties();

    public ProgressStore() {
        load();
    }

    public synchronized void load() {
        if (Files.exists(storePath)) {
            try (InputStream in = Files.newInputStream(storePath)) {
                props.load(in);
            } catch (IOException ignored) {
            }
        }
    }

    public synchronized void save() {
        try (OutputStream out = Files.newOutputStream(storePath)) {
            props.store(out, "Java 2-Day Grind App Progress");
        } catch (IOException ignored) {
        }
    }

    public synchronized boolean isLessonDone(String lessonId) {
        return Boolean.parseBoolean(props.getProperty("lesson.done." + lessonId, "false"));
    }

    public synchronized void setLessonDone(String lessonId, boolean done) {
        props.setProperty("lesson.done." + lessonId, String.valueOf(done));
        save();
    }

    public synchronized boolean isProblemSolved(String problemId) {
        return Boolean.parseBoolean(props.getProperty("problem.solved." + problemId, "false"));
    }

    public synchronized void markProblemSolved(String problemId) {
        props.setProperty("problem.solved." + problemId, "true");
        save();
    }

    public synchronized String getDraftCode(String problemId, String fallbackStarter) {
        return props.getProperty("problem.draft." + problemId, fallbackStarter);
    }

    public synchronized void saveDraftCode(String problemId, String code) {
        props.setProperty("problem.draft." + problemId, code);
        save();
    }

    public synchronized void clearDraftCode(String problemId) {
        props.remove("problem.draft." + problemId);
        save();
    }

    public synchronized boolean isQuizAnsweredCorrectly(String quizId) {
        return Boolean.parseBoolean(props.getProperty("quiz.correct." + quizId, "false"));
    }

    public synchronized void setQuizAnsweredCorrectly(String quizId, boolean correct) {
        if (correct) {
            props.setProperty("quiz.correct." + quizId, "true");
            save();
        }
    }

    public synchronized boolean isFlashcardMastered(int index) {
        return Boolean.parseBoolean(props.getProperty("card.mastered." + index, "false"));
    }

    public synchronized void setFlashcardMastered(int index, boolean mastered) {
        props.setProperty("card.mastered." + index, String.valueOf(mastered));
        save();
    }

    public synchronized boolean isPlanStepDone(int index) {
        return Boolean.parseBoolean(props.getProperty("plan.done." + index, "false"));
    }

    public synchronized void setPlanStepDone(int index, boolean done) {
        props.setProperty("plan.done." + index, String.valueOf(done));
        save();
    }
}
