package javagrind.enterprise;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom runtime-retained repeatable annotation used to self-document and dynamically
 * discover enterprise Java capabilities implemented across this codebase via Reflection.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(EnterpriseSkills.class)
public @interface EnterpriseSkill {
    String category();
    String name();
    String summary();
}
