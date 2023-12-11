package sast.evento.service;

import sast.evento.entitiy.User;

public interface UserService {

    Boolean checkUserState(String userId);

    User getUserById(String userId);

    User getUserByStudentId(String studentId);

    Integer updateUser(User user);

}
