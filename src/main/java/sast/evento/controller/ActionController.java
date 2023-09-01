package sast.evento.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import sast.evento.annotation.DefaultActionState;
import sast.evento.annotation.OperateLog;
import sast.evento.common.enums.ActionState;
import sast.evento.common.enums.ErrorEnum;
import sast.evento.config.ActionRegister;
import sast.evento.exception.LocalRunTimeException;
import sast.evento.model.Action;
import sast.evento.service.ActionService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/action")
public class ActionController {
    @Resource
    ActionService actionService;

    @GetMapping("/list")
    @OperateLog("获取接口列表")
    @DefaultActionState(ActionState.INVISIBLE)
    public List<Action> getActionList() {
        return ActionRegister.actionName2action.values().stream().toList();
    }

    @GetMapping("/states")
    @OperateLog("获取所有可用的活动状态")
    @DefaultActionState(ActionState.INVISIBLE)
    public List<ActionState> getStates() {
        return Arrays.stream(ActionState.values()).toList();
    }

    @PatchMapping("/info")
    @OperateLog("编辑接口状态,分组")
    @DefaultActionState(ActionState.INVISIBLE)
    public String updateAction(@RequestParam String methodName,
                               @RequestParam(required = false) String actionState,
                               @RequestParam(required = false) String group) {
        if (methodName.equals("updateAction")) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "warning, unsupported service");
        }
        Action action = Optional.ofNullable(actionService.getAction(methodName))
                .orElseThrow(() -> new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "method not exist"));
        if (!actionState.isEmpty()) {
            try {
                action.setActionState(ActionState.valueOf(actionState));
            } catch (Exception e) {
                throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "unsupported actionState");
            }
        }
        if (!group.isEmpty()) {
            action.setGroup(group);
        }
        return "ok";
    }

}
