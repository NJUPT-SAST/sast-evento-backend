package sast.evento.entitiy;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/7/18 21:55
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "event")
public class Event {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String title;

    private String description;

    private Date gmtEventStart;

    private Date gmtEventEnd;

    private Date gmtRegistrationStart;

    private Date gmtRegistrationEnd;

    private Integer typeId;

    private Integer locationId;

    private String department;
    @TableField(value = "attachment_url")
    private String attachmentURL;

    private String tag;//和前端商讨一下格式，可以放数组json

}
