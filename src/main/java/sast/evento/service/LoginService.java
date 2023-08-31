package sast.evento.service;

import sast.sastlink.sdk.exception.SastLinkException;
import sast.sastlink.sdk.model.UserInfo;

import java.util.Map;

public interface LoginService {
    Map<String, Object> linkLogin(String code) throws SastLinkException;

    Map<String, Object> wxLogin(String email, String password, String code_challenge, String code_challenge_method, String openId) throws SastLinkException;

    Map<String, String> wxRegister(String email) throws SastLinkException;

    boolean checkCaptcha(String token, String captcha, String password) throws SastLinkException;

    void logout(String userId) throws SastLinkException;

    UserInfo getUserInfo(String userId) throws SastLinkException;

    void checkLoginState(String userId, String token);


}
