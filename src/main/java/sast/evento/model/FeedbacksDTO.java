package sast.evento.model;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sast.evento.common.typehandler.FeedbackScoreTypeHandler;

import java.util.List;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/8/1 13:56
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbacksDTO {
    private Integer eventId;
    @TableField(typeHandler = FeedbackScoreTypeHandler.class)
    private Double average;
    private Integer subscribeNum;
    private Integer registrationNum;
    private Integer participantNum;
    private List<FeedbackModel> feedbacks;
}
