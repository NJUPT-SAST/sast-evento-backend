package sast.evento.model.wxServiceDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/7/28 16:44
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccessTokenResponse {

    private String access_token;

    /* 任意时刻发起调用获取到的 stable_token 有效期至少为 5 分钟 */
    private Integer expires_in;
}
