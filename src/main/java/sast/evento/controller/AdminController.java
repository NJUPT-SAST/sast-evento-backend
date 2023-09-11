package sast.evento.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import sast.evento.annotation.DefaultActionState;
import sast.evento.annotation.OperateLog;
import sast.evento.common.enums.ActionState;
import sast.evento.common.enums.ErrorEnum;
import sast.evento.entitiy.Department;
import sast.evento.entitiy.EventType;
import sast.evento.entitiy.Location;
import sast.evento.exception.LocalRunTimeException;
import sast.evento.model.treeDataNodeDTO.TreeDataNode;
import sast.evento.service.DepartmentService;
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
    @Resource
    private DepartmentService departmentService;

    /**
     * 添加活动地点
     * @param location 活动地点
     * @return 活动地点id
     */
    @OperateLog("添加活动地点")
    @DefaultActionState(value = ActionState.ADMIN,group = "location")
    @PostMapping("/location")
    public String addLocation(@RequestBody Location location) {
        if (location.getId() != null) throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "id should be null.");
        return locationService.addLocation(location).toString();
    }

    /**
     * 删除活动地点
     * @param locationId 活动地点id
     * @return 是否成功
     */
    @OperateLog("删除活动地点")
    @DefaultActionState(value = ActionState.ADMIN,group = "location")
    @DeleteMapping("/location")
    public String deleteLocation(@RequestParam Integer locationId) {
        return locationService.deleteLocation(locationId).toString();
    }

    /**
     * 获取活动地点
     * @return 活动地点列表
     */
    @OperateLog("获取活动地点")
    @DefaultActionState(value = ActionState.ADMIN,group = "location")
    @GetMapping("/locations")
    public List<TreeDataNode> getLocations() {
        /* 以树状结构返回 */
        return locationService.getLocations();
    }

    /**
     * 修改活动地点
     * @param locationId 活动地点id
     * @param location 活动地点
     * @return 是否成功
     */
    @OperateLog("修改活动地点")
    @DefaultActionState(ActionState.INVISIBLE)
    @PutMapping("/location")
    public String updateLocation(@RequestParam Integer locationId,
                                 @RequestBody Location location) {
        if (!location.getId().equals(locationId)) throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "invalid id");
        /* 比较复杂，谨慎修改 */
        return locationService.updateLocation(location).toString();

    }

    /**
     * 修改活动地点名称
     *
     * @param id           活动地点id
     * @param locationName 活动名称
     * @return ok
     */
    @OperateLog("修改活动地点名称")
    @DefaultActionState(value = ActionState.ADMIN,group = "location")
    @PatchMapping("/location")
    public String updateLocationName(@RequestParam Integer id,
                                     @RequestParam String locationName) {
        locationService.updateLocationName(id, locationName);
        return "ok";
    }

    /**
     * 添加活动类型
     * @param type 活动类型
     * @return 活动类型id
     */
    @OperateLog("添加活动类型")
    @DefaultActionState(value = ActionState.ADMIN,group = "type")
    @PostMapping("/type")
    public String addType(@RequestBody EventType type) {
        if (type.getId() != null) throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "id should be null");
        return eventTypeService.addEventType(type).toString();
    }

    /**
     * 删除活动类型
     * @param typeId 活动类型id
     * @return 是否成功
     */
    @OperateLog("删除活动类型")
    @DefaultActionState(value = ActionState.ADMIN,group = "type")
    @DeleteMapping("/type")
    public String deleteType(@RequestParam Integer typeId) {
        return eventTypeService.deleteEventType(typeId).toString();
    }

    /**
     * 获取活动类型
     * @return 活动类型列表
     */
    @OperateLog("获取活动类型")
    @DefaultActionState(value = ActionState.ADMIN,group = "type")
    @GetMapping("/types")
    public List<EventType> getTypes() {
        return eventTypeService.getAllEventType();
    }

    /**
     * 修改活动类型
     * @param typeId 活动类型id
     * @param type 活动类型
     * @return 是否成功
     */
    @OperateLog("修改活动类型")
    @DefaultActionState(value = ActionState.ADMIN,group = "type")
    @PutMapping("/type")
    public String updateType(@RequestParam Integer typeId,
                             @RequestBody EventType type) {
        if (!type.getId().equals(typeId)) throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "invalid id");
        return eventTypeService.editEventType(type).toString();
    }

    @OperateLog("添加组织部门")
    @DefaultActionState(value = ActionState.ADMIN,group = "department")
    @PostMapping("/department")
    public Integer addDepartment(@RequestParam String departmentName){
        return departmentService.addDepartment(departmentName);
    }

    @OperateLog("删除部门")
    @DefaultActionState(value = ActionState.ADMIN,group = "department")
    @DeleteMapping("/department")
    public String deleteDepartment(@RequestParam Integer departmentId){
        departmentService.deleteDepartment(departmentId);
        return "ok";
    }

    @OperateLog("获取全部组织部门")
    @DefaultActionState(value = ActionState.ADMIN,group = "department")
    @GetMapping("/departments")
    public List<Department> getDepartments(){
        return departmentService.getDepartments();
    }

    @OperateLog("修改组织部门名称")
    @DefaultActionState(value = ActionState.ADMIN,group = "department")
    @PutMapping("/department")
    public void putDepartment(@RequestParam Integer departmentId,
                              @RequestParam String departmentName){
        departmentService.putDepartment(departmentId,departmentName);
    }


}
