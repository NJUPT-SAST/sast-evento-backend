package sast.evento.service;

import sast.evento.model.wxServiceDTO.AccessTokenResponse;
import sast.evento.model.wxServiceDTO.WxSubscribeResponse;


public interface WxService {
    AccessTokenResponse getStableToken();

    WxSubscribeResponse seedSubscribeMessage(Integer eventId, String access_token, String openId);


}
