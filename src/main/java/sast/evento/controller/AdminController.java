package sast.evento.controller;

import org.springframework.web.bind.annotation.*;
import sast.evento.annotation.DefaultActionState;
import sast.evento.annotation.OperateLog;
import sast.evento.common.enums.ActionState;
import sast.evento.common.enums.ErrorEnum;
import sast.evento.entitiy.EventType;
import sast.evento.entitiy.Location;
import sast.evento.exception.LocalRunTimeException;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @OperateLog("添加活动地点")
    @DefaultActionState(ActionState.ADMIN)
    @PostMapping("/location")
    public String addLocation(@RequestBody Location location) {
        if (location.getId() != null) throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "id should be null.");
        return null;
    }

    @OperateLog("删除活动地点")
    @DefaultActionState(ActionState.ADMIN)
    @DeleteMapping("/location")
    public String deleteLocation(@RequestParam Integer locationId) {
        return null;
    }

    @OperateLog("获取活动地点")
    @DefaultActionState(ActionState.ADMIN)
    @GetMapping("/locations")
    public Object getLocations(@RequestParam Integer locationId) {
        /* 请和前端协商后决定传参方式如:0-0-1 */
        /* 以树状结构返回，和前端商讨参数和返回格式 */
        return null;
    }

    @OperateLog("修改活动地点")
    @DefaultActionState(ActionState.ADMIN)
    @PutMapping("/location")
    public String updateLocation(@RequestParam Integer locationId,
                                 @RequestBody Location location) {
        if (!location.getId().equals(locationId)) throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "invalid id");
        /* 比较复杂，谨慎修改 */
        return null;

    }

    @OperateLog("添加活动类型")
    @DefaultActionState(ActionState.ADMIN)
    @PostMapping("/type")
    public String addType(@RequestBody EventType type) {
        if (type.getId() != null) throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "id should be null");
        return null;
    }

    @OperateLog("删除活动类型")
    @DefaultActionState(ActionState.ADMIN)
    @DeleteMapping("/type")
    public String deleteType(@RequestParam Integer typeId) {
        return null;
    }

    @OperateLog("获取活动类型")
    @DefaultActionState(ActionState.ADMIN)
    @GetMapping("/types")
    public List<EventType> getTypes(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                    @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        return null;
    }

    @OperateLog("修改活动类型")
    @DefaultActionState(ActionState.ADMIN)
    @PutMapping("/type")
    public String updateType(@RequestParam Integer typeId,
                             @RequestBody EventType type) {
        if (!type.getId().equals(typeId)) throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "invalid id");
        return null;
    }

}
