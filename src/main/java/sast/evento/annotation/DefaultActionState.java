package sast.evento.annotation;

import sast.evento.common.enums.ActionState;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DefaultActionState {
    /* 添加注释后,默认为管理员操作 */
    ActionState value() default ActionState.ADMIN;
}
