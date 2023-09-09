package sast.evento.common.enums;

import sast.evento.exception.LocalRunTimeException;

import java.util.Arrays;

public enum EventState {
    NOT_STARTED(1, "未开始"),
    CHECKING_IN(2, "报名中"),
    IN_PROGRESS(3, "进行中"),
    CANCELED(4, "已取消"),
    ENDED(5, "已结束");
    private final Integer state;
    private final String description;

    EventState(Integer state, String description) {
        this.state = state;
        this.description = description;
    }

    public static EventState getEventState(Integer state) {
        return Arrays.stream(EventState.values())
                .filter(EventState -> EventState.getState().equals(state))
                .findAny()
                .orElseThrow(() -> new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "Invalid event_state value"));
    }

    public Integer getState() {
        return state;
    }


}
