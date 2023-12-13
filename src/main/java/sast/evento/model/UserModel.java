package sast.evento.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/7/14 20:06
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserModel {
    private String id;
    private String studentId;
    private String email;
}
