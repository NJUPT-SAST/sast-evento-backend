package sast.evento.entitiy;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String content;

    private Integer score;

    private Integer participateId;

    private String userId;

}
