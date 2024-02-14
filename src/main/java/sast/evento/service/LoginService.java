package sast.evento.service;


import fun.feellmoose.exception.SastLinkException;

import java.util.Map;

public interface LoginService {
    /**
     * sast-link登录
     * @param code
     * @param type
     * @param updateUser
     * @return
     * @throws SastLinkException
     */
    Map<String, Object> linkLogin(String code, Integer type, Boolean updateUser) throws SastLinkException;

    /**
     * 登出
     * @param userId
     * @throws SastLinkException
     */
    void logout(String userId) throws SastLinkException;

    /**
     * 检查登录状态
     * @param userId
     * @param token
     */
    void checkLoginState(String userId, String token);

    /**
     * 获取授权给新设备登录的ticket
     * @param ticket
     * @return
     */
    Map<String, Object> getLoginTicket(String ticket);

    /**
     * 新设备获取ticket后使用学号登录
     * @param ticket
     * @param userId
     */
    void checkLoginTicket(String ticket,String userId);

    /**
     * 绑定密码
     * @param studentId
     * @param password
     */
    void bindPassword(String studentId, String password);

    /**
     * 密码登录
     * @param studentId
     * @param password
     * @return
     */
    Map<String, Object> loginByPassword(String studentId, String password);


}
