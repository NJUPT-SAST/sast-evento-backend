package sast.evento.annotation;

import sast.evento.enums.ActionState;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DefaultActionState {
    /* 默认不可见 */
    ActionState value() default ActionState.INVISIBLE;
}
