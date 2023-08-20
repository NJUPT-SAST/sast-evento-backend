package sast.evento.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: NicoAsuka
 * @date: 4/29/23
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OperateLog {
    @AliasFor(attribute = "description")
    String value() default "";

    @AliasFor(attribute = "value")
    String description() default "";
}
