package sast.evento.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import sast.evento.entitiy.User;
import sast.evento.mapper.UserMapper;
import sast.evento.service.UserService;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/7/25 15:24
 */
@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserMapper userMapper;

    @Override
    public Boolean checkUserState(String userId) {
        return getUserById(userId) != null;
    }

    @Override
    public User getUserById(String userId) {
        return userMapper.selectById(userId);
    }

    @Override
    public User getUserByStudentId(String studentId) {
        return userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getStudentId,studentId));
    }

    @Override
    public Integer updateUser(User user) {
        return userMapper.updateById(user);
    }

    @Override
    public Integer addUser(User user) {
        return userMapper.insert(user);
    }

    @Override
    public Integer deleteUserById(String userId) {
        return userMapper.deleteById(userId);
    }
}
