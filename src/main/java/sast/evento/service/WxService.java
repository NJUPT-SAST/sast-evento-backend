package sast.evento.service;

import sast.evento.model.wxServiceDTO.AccessTokenResponse;
import sast.evento.model.wxServiceDTO.JsCodeSessionResponse;
import sast.evento.model.wxServiceDTO.WxSubscribeResponse;

// todo 待删除

public interface WxService {


    /**
     * 获取稳定token
     * @return
     */
    AccessTokenResponse getStableToken();

    /**
     * 发送微信订阅消息
     * @param eventId
     * @param access_token
     * @param openId
     * @return
     */
    WxSubscribeResponse seedSubscribeMessage(Integer eventId, String access_token, String openId);


}
