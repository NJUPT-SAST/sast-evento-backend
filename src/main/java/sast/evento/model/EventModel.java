package sast.evento.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sast.evento.common.enums.EventState;
import sast.evento.entitiy.Department;
import sast.evento.entitiy.EventType;

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
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer typeId;

    private String location;

    private Integer locationId;

    private String tag;


    private EventState state;

    private List<Department> departments;

}
