package javagrind.enterprise;

import javagrind.app.LiveJavaEngine;
import javagrind.app.ProgressStore;
import java.util.ArrayList;
import java.util.List;

/**
 * Runtime Reflection metadata scanner inspecting {@link EnterpriseSkill} annotations
 * across the application's core subsystems to produce a verified Java capability report.
 */
@EnterpriseSkill(
        category = "Metaprogramming & Reflection",
        name = "Custom Runtime Annotation Scanner",
        summary = "Discovers @EnterpriseSkill annotations via Class.getAnnotationsByType at runtime."
)
public final class SkillRegistryScanner {

    public record DiscoveredSkill(String category, String name, String implementingClass, String summary) {}

    private static final List<Class<?>> REGISTERED_COMPONENTS = List.of(
            LiveJavaEngine.class,
            ProgressStore.class,
            ExecutionOutcome.class,
            ThreadSafeLruCache.class,
            TokenBucketRateLimiter.class,
            AuditEventBus.class,
            AsyncPipelineOrchestrator.class,
            EmbeddedRestServer.class,
            SkillRegistryScanner.class
    );

    private SkillRegistryScanner() {}

    public static List<DiscoveredSkill> scanAll() {
        List<DiscoveredSkill> found = new ArrayList<>();
        for (Class<?> clazz : REGISTERED_COMPONENTS) {
            EnterpriseSkill[] annotations = clazz.getAnnotationsByType(EnterpriseSkill.class);
            for (EnterpriseSkill ann : annotations) {
                found.add(new DiscoveredSkill(
                        ann.category(),
                        ann.name(),
                        clazz.getSimpleName(),
                        ann.summary()
                ));
            }
        }
        return found;
    }

    public static String toJsonArray() {
        List<DiscoveredSkill> skills = scanAll();
        StringBuilder sb = new StringBuilder("[\n");
        for (int i = 0; i < skills.size(); i++) {
            DiscoveredSkill s = skills.get(i);
            sb.append("  {\"category\":\"").append(escape(s.category()))
              .append("\",\"name\":\"").append(escape(s.name()))
              .append("\",\"class\":\"").append(escape(s.implementingClass()))
              .append("\",\"summary\":\"").append(escape(s.summary())).append("\"}");
            if (i < skills.size() - 1) sb.append(',');
            sb.append('\n');
        }
        sb.append(']');
        return sb.toString();
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
