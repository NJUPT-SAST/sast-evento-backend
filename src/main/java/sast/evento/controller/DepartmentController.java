package sast.evento.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import sast.evento.annotation.DefaultActionState;
import sast.evento.annotation.OperateLog;
import sast.evento.common.enums.ActionState;
import sast.evento.entitiy.Department;
import sast.evento.service.DepartmentService;

import java.util.List;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/8/23 17:38
 */
@RestController
@RequestMapping("/admin")
public class DepartmentController {
    @Resource
    private DepartmentService departmentService;

    @OperateLog("添加组织部门")
    @DefaultActionState(ActionState.ADMIN)
    @PostMapping("/department")
    public Integer addDepartment(@RequestParam String departmentName){
        return departmentService.addDepartment(departmentName);
    }

    @OperateLog("删除部门")
    @DefaultActionState(ActionState.ADMIN)
    @DeleteMapping("/department")
    public void deleteDepartment(@RequestParam Integer departmentId){
        departmentService.deleteDepartment(departmentId);
    }

    @OperateLog("获取全部组织部门")
    @DefaultActionState(ActionState.ADMIN)
    @GetMapping("/departments")
    public List<Department> getDepartments(){
        return departmentService.getDepartments();
    }

    @OperateLog("修改组织部门名称")
    @DefaultActionState(ActionState.ADMIN)
    @PutMapping("/department")
    public void putDepartment(@RequestParam Integer departmentId,
                              @RequestParam String departmentName){
        departmentService.putDepartment(departmentId,departmentName);
    }

}
