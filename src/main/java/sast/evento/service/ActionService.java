package sast.evento.service;

import sast.evento.common.enums.ActionState;
import sast.evento.model.Action;

public interface ActionService {
    Action getAction(String method);
    void setActionState(String method, ActionState actionState);
    void setDescription(String method, String actionName);
    void setGroup(String method, String group);
}
