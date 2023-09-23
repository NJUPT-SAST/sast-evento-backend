package sast.evento.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @projectName: sast-evento-backend
 * @author Aiden
 * @date 2023/9/12 21:32
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackNumModel {
    private Integer eventId;
    private Integer feedbackCount;
    private String title;
}
