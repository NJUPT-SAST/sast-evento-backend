package sast.evento.controller;

import org.springframework.web.bind.annotation.*;
import sast.evento.annotation.DefaultActionState;
import sast.evento.annotation.OperateLog;
import sast.evento.common.enums.ActionState;
import sast.evento.model.Action;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/action")
public class ActionController {

    @GetMapping("/list")
    @OperateLog("获取接口列表")
    @DefaultActionState(ActionState.ADMIN)
    public List<Action> getActionList() {
        return null;
    }

    @GetMapping("/states")
    @OperateLog("获取所有可用的活动状态")
    @DefaultActionState(ActionState.ADMIN)
    public List<ActionState> getStates() {
        return Arrays.stream(ActionState.values()).toList();
    }

    @PatchMapping("/info")
    @OperateLog("编辑接口状态,分组,描述")
    @DefaultActionState(ActionState.ADMIN)
    public String updateAction(@RequestParam(required = false) String methodName,
                               @RequestParam(required = false) String actionState,
                               @RequestParam(required = false) String group,
                               @RequestParam(required = false) String description) {
        return null;
    }

}
