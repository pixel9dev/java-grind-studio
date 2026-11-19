package javagrind.enterprise;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Container annotation enabling multiple {@link EnterpriseSkill} annotations on a single type.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EnterpriseSkills {
    EnterpriseSkill[] value();
}
