package sast.evento.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    private Double score;

    private Integer eventId;

}
