package sast.evento.service;

import sast.evento.model.Action;

public interface ActionService {
    Action getAction(String method);
    void setActionVisible(String method, Boolean isVisible);
    void setActionPublic(String method, Boolean isPublic);
    void setDescription(String method, String actionName);
}
