package sast.evento.entitiy;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sast.evento.common.enums.EventState;
import sast.evento.common.enums.EventStateTypeHandler;
import sast.evento.model.EventModel;

import java.util.Date;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/7/18 21:55
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "event", autoResultMap = true)
public class Event {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String title;

    private String description;

    @TableField(value = "gmt_event_start")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gmtEventStart;

    @TableField(value = "gmt_event_end")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gmtEventEnd;

    @TableField(value = "gmt_registration_start")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gmtRegistrationStart;

    @TableField(value = "gmt_registration_end")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gmtRegistrationEnd;

    private Integer typeId;

    private Integer locationId;

    private String tag;

    @TableField(typeHandler = EventStateTypeHandler.class)
    private EventState state;

    public Event(EventModel eventModel) {
        this.id = eventModel.getId();
        this.title = eventModel.getTitle();
        this.description = eventModel.getDescription();
        this.gmtEventStart = eventModel.getGmtEventStart();
        this.gmtEventEnd = eventModel.getGmtEventEnd();
        this.gmtRegistrationStart = eventModel.getGmtRegistrationStart();
        this.gmtRegistrationEnd = eventModel.getGmtRegistrationEnd();
        this.typeId = eventModel.getTypeId();
        this.locationId = eventModel.getLocationId();
        this.tag = eventModel.getTag();
        this.state = eventModel.getState();
    }
}
