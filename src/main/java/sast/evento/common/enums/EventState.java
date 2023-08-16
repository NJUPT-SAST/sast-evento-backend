package sast.evento.common.enums;

import sast.evento.exception.LocalRunTimeException;

import java.util.Arrays;

public enum EventState {
    NOT_STARTED(1,"未开始"),
    CHECKING_IN(2,"报名中"),
    IN_PROGRESS(3,"进行中"),
    CANCELED(4,"已取消"),
    ENDED(5,"已结束")
    ;
    private Integer state;
    private String description;
    EventState(Integer state,String description){
        this.state = state;
        this.description = description;
    }
    public Integer getState() {
        return state;
    }
    public static EventState getEventState(int state){
        return Arrays.stream(EventState.values())
                .filter(EventState ->EventState.getState().equals(state))
                .findAny()
                .orElseThrow(() -> new LocalRunTimeException(ErrorEnum.COMMON_ERROR,"Invalid event_state value"));
    }


}
