package sast.evento.dataobject;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/7/18 22:01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "feedback")
public class Feedback {
    private Integer id;

    private String content;

    private Integer participateId;

    private Integer userId;

}
