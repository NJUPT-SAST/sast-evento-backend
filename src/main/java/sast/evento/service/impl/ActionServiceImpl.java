package sast.evento.service.impl;

import org.springframework.stereotype.Service;
import sast.evento.config.ActionRegister;
import sast.evento.enums.ActionState;
import sast.evento.enums.ErrorEnum;
import sast.evento.exception.LocalRunTimeException;
import sast.evento.model.Action;
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
    public void setActionState(String method, ActionState actionState) {
        getAction(method).setActionState(actionState);
    }

    @Override
    public void setDescription(String method, String description) {
        Action action = getAction(method);
        if (action.getDescription().equals(description)) {
            throw new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "Same actionName has been already set.");
        }
        action.setDescription(description);
    }

    @Override
    public void setGroup(String method, String group) {
        Action action = getAction(method);
        if (action.getGroup().equals(group)) {
            throw new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "Same group has been already set.");
        }
        action.setGroup(group);
    }


}
