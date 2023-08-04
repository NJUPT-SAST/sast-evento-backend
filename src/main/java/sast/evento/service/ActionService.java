package sast.evento.service;

import sast.evento.common.enums.ActionState;
import sast.evento.model.Action;

public interface ActionService {
    Action getAction(String method);
    void setActionState(String method, ActionState actionState);
    void setGroup(String method, String group);
}
