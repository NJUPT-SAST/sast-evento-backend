package sast.evento.service;

import sast.evento.entitiy.User;

public interface UserService {

    Boolean checkUserState(String userId);

    User getUserById(String userId);

    User getUserByStudentId(String studentId);

    Integer updateUser(User user);

    Integer addUser(User user);//增加默认权限喵

    Integer deleteUserById(String userId);//同时删除权限喵


}
