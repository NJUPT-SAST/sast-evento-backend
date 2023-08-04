package sast.evento.common.enums;


import sast.evento.exception.LocalRunTimeException;

import java.util.Arrays;

public enum ActionState {
    /* 操作状态,根据操作状态选择鉴权流程 */
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
