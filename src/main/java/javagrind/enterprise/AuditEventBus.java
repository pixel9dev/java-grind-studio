package javagrind.enterprise;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Thread-safe Singleton Observer / Pub-Sub Event Bus recording structured `java.time`
 * audit events across compiler runs, problem submissions, REST API calls, and pattern demos.
 */
@EnterpriseSkill(
        category = "Design Patterns & java.time",
        name = "Pub/Sub Observer Event Bus & ISO Audit Trail",
        summary = "Enum/Holder Singleton event bus using CopyOnWriteArrayList subscribers and Instant timestamps."
)
public final class AuditEventBus {

    private static final DateTimeFormatter TIME_FMT =
            DateTimeFormatter.ofPattern("HH:mm:ss.SSS").withZone(ZoneId.systemDefault());
    private static final int MAX_HISTORY = 200;

    public record AuditEvent(Instant timestamp, String source, String action, String detail) {
        public String formattedLine() {
            return String.format("[%s] %-14s | %-18s | %s",
                    TIME_FMT.format(timestamp), source, action, detail);
        }
    }

    // Initialization-on-demand holder idiom (lazy, lock-free thread-safe Singleton!)
    private static final class Holder {
        private static final AuditEventBus INSTANCE = new AuditEventBus();
    }

    public static AuditEventBus getInstance() {
        return Holder.INSTANCE;
    }

    private final CopyOnWriteArrayList<Consumer<AuditEvent>> subscribers = new CopyOnWriteArrayList<>();
    private final ConcurrentLinkedDeque<AuditEvent> history = new ConcurrentLinkedDeque<>();

    private AuditEventBus() {}

    public void subscribe(Consumer<AuditEvent> listener) {
        if (listener != null) {
            subscribers.add(listener);
        }
    }

    public void publish(String source, String action, String detail) {
        AuditEvent ev = new AuditEvent(Instant.now(), source, action, detail);
        history.addFirst(ev);
        while (history.size() > MAX_HISTORY) {
            history.pollLast();
        }
        for (Consumer<AuditEvent> sub : subscribers) {
            try {
                sub.accept(ev);
            } catch (Exception ignored) {
            }
        }
    }

    public List<AuditEvent> recentEvents() {
        return new ArrayList<>(history);
    }
}
