package sast.evento.model.wxServiceDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JsCodeSessionResponse {
    private String session_key;
    private String unionid;
    private String errmsg;
    private String openid;
    private Integer errcode;
}
