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
public class UserProFile {
    //todo 对接Sast_Link

    private String userId;

    private String nickname;

    private String avatarURL;

    private String organization;

    private String biography;

    private String email;

    private String Link;

    //...

}
