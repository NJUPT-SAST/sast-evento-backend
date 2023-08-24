package sast.evento.service;

import sast.sastlink.sdk.model.UserInfo;

import java.util.Map;

public interface LoginService {
    Map<String, Object> linkLogin(String code);

    Map<String, Object> wxLogin(String email, String password, String code_challenge, String code_challenge_method);

    Map<String, String> wxRegister(String email);

    boolean checkCaptcha(String token, String captcha, String password);

    void logout(String userId);

    UserInfo getUserInfo(String userId);


}
