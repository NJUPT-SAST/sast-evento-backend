package sast.evento.common.enums;


import sast.evento.exception.LocalRunTimeException;

import java.util.Arrays;

public enum ActionState {
    /* 权限分类,不作为权限分配的标准,作为分类获取权限的依据 */
    ADMIN(0),
    MANAGER(1),
    PUBLIC(2),
    LOGIN(3),
    INVISIBLE(4);
    final int num;

    ActionState(int num) {
        this.num = num;
    }

    ActionState getActionByNum(int num) {
        return Arrays.stream(ActionState.values())
                .filter(state -> state.num == num)
                .findAny()
                .orElseThrow(() -> new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "action state not exist."));
    }

}
