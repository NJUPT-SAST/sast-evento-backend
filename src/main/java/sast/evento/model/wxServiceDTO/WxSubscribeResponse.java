package sast.evento.model.wxServiceDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/7/28 16:47
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WxSubscribeResponse {

    private String errcode;

    private String errmsg;

    @Override
    public String toString() {
        return "response{" +
                "errcode='" + errcode + '\'' +
                ", errmsg='" + errmsg + '\'' +
                '}';
    }
}
