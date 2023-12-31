package sast.evento.service;


import fun.feellmoose.exception.SastLinkException;

import java.util.Map;

public interface LoginService {
    Map<String, Object> linkLogin(String code, Integer type, Boolean updateUser) throws SastLinkException;

    Map<String, Object> wxLogin(String code);

    void logout(String userId) throws SastLinkException;

    void checkLoginState(String userId, String token);

    Map<String, Object> bindStudentOnWechat(String userId, String studentId, Boolean force);

    Map<String, Object> getLoginTicket(String ticket);

    void checkLoginTicket(String ticket,String userId);

    void bindPassword(String studentId, String password);

    Map<String, Object> loginByPassword(String studentId, String password);


}
