package sast.evento.model.wxServiceDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/7/28 16:44
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class StableTokenRequest {

    private String grant_type = "client_credential";

    private String appid;

    private String secret;

    private Boolean force_refresh = false;
}
