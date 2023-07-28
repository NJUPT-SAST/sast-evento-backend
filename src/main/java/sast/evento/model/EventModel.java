package sast.evento.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sast.evento.entitiy.Department;
import sast.evento.entitiy.EventDepartment;
import sast.evento.entitiy.EventType;
import sast.evento.entitiy.Location;
import sast.evento.enums.EventState;
import sast.evento.enums.EventStateTypeHandler;

import java.util.Date;
import java.util.List;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/7/26 19:58
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventModel {

    private Integer id;

    private String title;

    private String description;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gmtEventStart;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gmtEventEnd;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gmtRegistrationStart;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gmtRegistrationEnd;

    private EventType eventType;

    private Location location;

    private String tag;

    private EventState state;

    private List<Department> departments;

}
