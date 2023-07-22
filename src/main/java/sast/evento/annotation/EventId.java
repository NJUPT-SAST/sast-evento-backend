package sast.evento.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)

public @interface EventId {
    @AliasFor(attribute = "ParamName")
    String value() default "";
    @AliasFor(attribute = "value")
    String ParamName() default "";
}
