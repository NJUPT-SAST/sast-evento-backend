package sast.evento.service;

import sast.evento.model.UserProFile;

public interface SastLinkServiceCacheAble {
    String linkLogin(String code);

    String wxLogin(String code);

    String logout(String userId, String code);

    UserProFile getUserProFile(String userId);

    UserProFile updateUserProFile(String userId, UserProFile userProFile);

}
