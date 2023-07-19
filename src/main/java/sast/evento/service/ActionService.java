package sast.evento.service;

import sast.evento.entitiy.Action;

public interface ActionService {
    Action getActionByAPI(String url, String requestMethod);

    Action addActionByAPI(String url, String requestMethod, String actionName);

    Action setActionVisible(String url, String requestMethod, Boolean isVisible);

    Action setActionPublic(String url, String requestMethod, Boolean isPublic);

    Action setActionName(String url, String requestMethod, String actionName);
}
