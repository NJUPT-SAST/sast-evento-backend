package sast.evento.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import sast.evento.annotation.DefaultActionState;
import sast.evento.annotation.OperateLog;
import sast.evento.common.enums.ActionState;
import sast.evento.common.enums.ErrorEnum;
import sast.evento.entitiy.EventType;
import sast.evento.entitiy.Location;
import sast.evento.exception.LocalRunTimeException;
import sast.evento.model.treeDataNodeDTO.TreeDataNode;
import sast.evento.service.EventTypeService;
import sast.evento.service.LocationService;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Resource
    private EventTypeService eventTypeService;
    @Resource
    private LocationService locationService;

    @OperateLog("添加活动地点")
    @DefaultActionState(ActionState.ADMIN)
    @PostMapping("/location")
    public String addLocation(@RequestBody Location location) {
        if (location.getId() != null) throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "id should be null.");
        return locationService.addLocation(location).toString();
    }

    @OperateLog("删除活动地点")
    @DefaultActionState(ActionState.ADMIN)
    @DeleteMapping("/location")
    public String deleteLocation(@RequestParam Integer locationId) {
        return locationService.deleteLocation(locationId).toString();
    }

    @OperateLog("获取活动地点")
    @DefaultActionState(ActionState.ADMIN)
    @GetMapping("/locations")
    public List<TreeDataNode> getLocations() {
        /* 以树状结构返回 */
        return locationService.getLocations();
    }

    @OperateLog("修改活动地点")
    @DefaultActionState(ActionState.ADMIN)
    @PutMapping("/location")
    public String updateLocation(@RequestParam Integer locationId,
                                 @RequestBody Location location) {
        if (!location.getId().equals(locationId)) throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "invalid id");
        /* 比较复杂，谨慎修改 */
        return locationService.updateLocation(location).toString();

    }

    @OperateLog("添加活动类型")
    @DefaultActionState(ActionState.ADMIN)
    @PostMapping("/type")
    public String addType(@RequestBody EventType type) {
        if (type.getId() != null) throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "id should be null");
        return eventTypeService.addEventType(type).toString();
    }

    @OperateLog("删除活动类型")
    @DefaultActionState(ActionState.ADMIN)
    @DeleteMapping("/type")
    public String deleteType(@RequestParam Integer typeId) {
        return eventTypeService.deleteEventType(typeId).toString();
    }

    @OperateLog("获取活动类型")
    @DefaultActionState(ActionState.ADMIN)
    @GetMapping("/types")
    public List<EventType> getTypes() {
        return eventTypeService.getAllEventType();
    }

    @OperateLog("修改活动类型")
    @DefaultActionState(ActionState.ADMIN)
    @PutMapping("/type")
    public String updateType(@RequestParam Integer typeId,
                             @RequestBody EventType type) {
        if (!type.getId().equals(typeId)) throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "invalid id");
        return eventTypeService.editEventType(type).toString();
    }

}
