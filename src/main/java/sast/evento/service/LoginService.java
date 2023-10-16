package sast.evento.service;

import sast.evento.entitiy.User;
import sast.sastlink.sdk.exception.SastLinkException;
import sast.sastlink.sdk.model.UserInfo;

import java.util.Map;

public interface LoginService {
    Map<String, Object> linkLogin(String code,Integer type) throws SastLinkException;

    Map<String, Object> wxLogin(String code) ;

    void logout(String userId) throws SastLinkException;

    void checkLoginState(String userId, String token);

    void bindStudent(String userId,String studentId);

    Map<String,Object> getKeyForLogin(String studentId);

    Map<String,Object> bindPassword(String studentId,String password);

    Map<String,Object> loginByPassword(String studentId,String password);


}
