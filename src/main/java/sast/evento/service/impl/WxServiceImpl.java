package sast.evento.service.impl;


import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import sast.evento.common.constant.Constant;
import sast.evento.common.enums.ErrorEnum;
import sast.evento.exception.LocalRunTimeException;
import sast.evento.model.wxServiceDTO.AccessTokenRequest;
import sast.evento.model.wxServiceDTO.AccessTokenResponse;
import sast.evento.model.wxServiceDTO.WxSubscribeRequest;
import sast.evento.model.wxServiceDTO.WxSubscribeResponse;
import sast.evento.service.WxService;

import java.util.HashMap;
import java.util.Map;


/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/7/28 17:45
 */
@Service
public class WxServiceImpl implements WxService {
    /* 基础微信相关服务 */

    @Value("${wx.templateId}")
    public String template_id;
    @Value("${wx.secret}")
    public String secret;
    @Value("${wx.appid}")
    public String appid;
    @Value("${wx.TokenRetryTimes}")
    public int retryTimes;

    @Resource
    private RestTemplate restTemplate;
    /* 获取stable_token，无需手动刷新,有效时间最少为5分钟 */
    public AccessTokenResponse getStableToken() {
        AccessTokenRequest request = new AccessTokenRequest()
                .setAppid(appid)
                .setSecret(secret);
        int times = 0;
        AccessTokenResponse response = null;
        while (times < retryTimes) {
            response = restTemplate.postForObject(Constant.wxAccessTokenURL, request, AccessTokenResponse.class);
            if (response != null && !response.getAccess_token().isEmpty()) {
                return response;
            }
            times++;
        }
        throw new LocalRunTimeException(ErrorEnum.WX_SERVICE_ERROR, "response or access_token is empty");
    }

    /* 发送wx模板消息内容 */
    public WxSubscribeResponse seedSubscribeMessage(Integer eventId, String access_token, String openId) {
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("", "");//todo 与模板对接放入信息，类型为String

        WxSubscribeRequest wxSubscribeRequest = new WxSubscribeRequest()
                .setTemplate_id(template_id)
                .setPage(null)//点击跳转小程序页面: "index?foo=bar"
                .setData(WxSubscribeRequest.getData(dataMap))
                .setTouser(openId);
        Map<String, String> wxSubscribeVariables = new HashMap<>();
        wxSubscribeVariables.put("access_token", access_token);
        WxSubscribeResponse response = restTemplate.postForObject(Constant.wxSubscribeURL, wxSubscribeRequest, WxSubscribeResponse.class, wxSubscribeVariables);
        if (response == null || !response.getErrcode().equals("0")) {
            throw new LocalRunTimeException(ErrorEnum.WX_SUBSCRIBE_ERROR, "Seed message failed, return: " + response);
        }
        return response;
    }


}
