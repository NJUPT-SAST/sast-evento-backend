package sast.evento.service.impl;

import org.springframework.stereotype.Service;
import sast.evento.config.ActionRegister;
import sast.evento.model.Action;
import sast.evento.enums.ErrorEnum;
import sast.evento.exception.LocalRunTimeException;
import sast.evento.service.ActionService;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/7/17 19:52
 */
@Service
public class ActionServiceImpl implements ActionService {

    @Override
    public Action getAction(String method) {
        return ActionRegister.actionName2action.get(method);
    }

    @Override
    public void setActionVisible(String method, Boolean isVisible) {
        getAction(method).setIsVisible(isVisible);
    }

    @Override
    public void setActionPublic(String method, Boolean isPublic) {
        getAction(method).setIsPublic(isPublic);
    }

    @Override
    public void setDescription(String method, String description) {
        Action action = getAction(method);
        if (action.getDescription().equals(description)) {
            throw new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "Same actionName has been already set.");
        }
        action.setDescription(description);
    }



}
