package sast.evento.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import sast.evento.entitiy.User;
import sast.evento.mapper.UserMapper;
import sast.evento.service.PermissionService;
import sast.evento.service.UserService;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/7/25 15:24
 */
@Service
public class UserServiceImpl implements UserService {
    /* 因为sastLink原因存储信息暂时未确定，但是一定会存studentId和openId */
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
        return userMapper.selectOne(Wrappers.lambdaQuery(User.class)
                .eq(User::getStudentId,studentId)
                .last("limit 1"));
    }

    @Override
    public Integer updateUser(User user) {
        return userMapper.updateById(user);
    }


}
