package sast.evento.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EventId {
    @AliasFor("name")
    String value() default "eventId";

    @AliasFor("value")
    String name() default "eventId";
}
