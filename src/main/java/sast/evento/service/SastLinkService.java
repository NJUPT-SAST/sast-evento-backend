package sast.evento.service;

import sast.evento.model.UserProFile;

public interface SastLinkService {
    String login(String userName,String password);
    UserProFile getUserProFile(String userId);
}
