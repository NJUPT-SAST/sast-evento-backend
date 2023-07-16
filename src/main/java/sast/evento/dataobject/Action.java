package sast.evento.dataobject;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/7/14 17:44
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "action")
public class Action {
    @TableId(value = "id")
    private Integer id;

    private String actionName;

    private String method;

    private String URL;

    private Boolean isVisible;

    private Boolean isPublic;

}
