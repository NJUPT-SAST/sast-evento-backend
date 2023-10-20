package sast.evento.entitiy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserPassword {
    private String id;
    private String studentId;
    private String password;
    private String salt;
}
