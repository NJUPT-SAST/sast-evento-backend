package sast.evento.entitiy;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/7/14 12:32
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "permission", autoResultMap = true)
public class Permission {
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    private Integer eventId;

    private String userId;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> methodNames;

    @TableField(value = "gmt_update")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    public Permission updateUpTime(){
        this.setUpdateTime(new Date());
        return this;
    }

}
