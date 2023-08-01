package sast.evento.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sast.evento.annotation.DefaultActionState;
import sast.evento.annotation.OperateLog;
import sast.evento.common.enums.ActionState;
import sast.evento.model.Action;

import java.util.List;

@Controller
@RequestMapping("/action")
public class ActionController {

    @GetMapping("/list")
    @OperateLog("获取接口列表")
    @DefaultActionState(ActionState.ADMIN)
    public List<Action> getActionList() {
        return null;
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
