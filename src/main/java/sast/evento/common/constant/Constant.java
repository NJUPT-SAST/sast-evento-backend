package sast.evento.common.constant;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/7/31 21:53
 */
public class Constant {
    public static final String wxAccessTokenURL = "https://api.weixin.qq.com/cgi-bin/token?appid={appid}&secret={secret}&grant_type=client_credential";
    public static final String wxStableTokenURL = "https://api.weixin.qq.com/cgi-bin/stable_token";
    public static final String wxSubscribeURL = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token={access_token}";
    public static final String jsCode2Session = "https://api.weixin.qq.com/sns/jscode2session?appid={appid}&secret={secret}&js_code={code}&grant_type=authorization_code";


}
