package sast.evento.service;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import sast.evento.model.UserProFile;

import java.util.HashMap;
import java.util.Map;

public interface SastLinkServiceCacheAble {
    String login(String userId,String code);
    String logout(String userId,String code);
    UserProFile getUserProFile(String userId);
    UserProFile updateUserProFile(String userId,UserProFile userProFile);

}
