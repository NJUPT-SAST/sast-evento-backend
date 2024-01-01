package sast.evento.model;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sast.evento.common.typehandler.FeedbackScoreTypeHandler;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/8/1 14:28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackModel {

    private Integer id;

    private String content;

    @TableField(typeHandler = FeedbackScoreTypeHandler.class)
    private Double score;

    private Integer eventId;

}
