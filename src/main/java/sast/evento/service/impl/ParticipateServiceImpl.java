package sast.evento.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import sast.evento.common.enums.ErrorEnum;
import sast.evento.entitiy.Participate;
import sast.evento.exception.LocalRunTimeException;
import sast.evento.mapper.ParticipateMapper;
import sast.evento.service.ParticipateService;

/**
 * @projectName: sast-evento-backend
 * @author: mio
 * @date: 2023/8/16 23:17
 */
@Service
public class ParticipateServiceImpl implements ParticipateService {
    @Resource
    private ParticipateMapper participateMapper;

    // 订阅活动 / 取消订阅
    @Override
    public String subscribe(String userId, Integer eventId, Boolean isSubscribe) {
        if (userId == null || eventId == null || isSubscribe == null) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR);
        }

        QueryWrapper<Participate> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("event_id", eventId);
        Participate participate = participateMapper.selectOne(queryWrapper);

        if (participate != null) {
            UpdateWrapper<Participate> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("user_id", userId);
            updateWrapper.eq("event_id", eventId);
            updateWrapper.set("is_subscribe", isSubscribe);

            int updateResult = participateMapper.update(null, updateWrapper);
            if (isSubscribe) {
                return updateResult > 0 ? "订阅成功" : "订阅失败";
            } else {
                return updateResult > 0 ? "取消订阅成功" : "取消订阅失败";
            }
        } else {
            participate = new Participate();
            participate.setUserId(userId);
            participate.setEventId(eventId);
            participate.setIsRegistration(false);
            participate.setIsParticipate(false);
            participate.setIsSubscribe(isSubscribe);

            int insertResult = participateMapper.insert(participate);
            if (isSubscribe) {
                return insertResult > 0 ? "订阅成功" : "订阅失败";
            } else {
                return insertResult > 0 ? "取消订阅成功" : "取消订阅失败";
            }
        }
    }

    // 报名活动 / 取消报名
    @Override
    public String register(String userId, Integer eventId, Boolean isRegister) {
        if (userId == null || eventId == null || isRegister == null) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR);
        }

        QueryWrapper<Participate> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("event_id", eventId);
        Participate participate = participateMapper.selectOne(queryWrapper);

        if (participate != null) {
            UpdateWrapper<Participate> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("user_id", userId);
            updateWrapper.eq("event_id", eventId);
            updateWrapper.set("is_registration", isRegister);

            int updateResult = participateMapper.update(null, updateWrapper);
            if (isRegister) {
                return updateResult > 0 ? "报名成功" : "报名失败";
            } else {
                return updateResult > 0 ? "取消报名成功" : "取消报名失败";
            }
        } else {
            participate = new Participate();
            participate.setUserId(userId);
            participate.setEventId(eventId);
            participate.setIsRegistration(isRegister);
            participate.setIsParticipate(false);
            participate.setIsSubscribe(false);

            int insertResult = participateMapper.insert(participate);
            if (isRegister) {
                return insertResult > 0 ? "报名成功" : "报名失败";
            } else {
                return insertResult > 0 ? "取消报名成功" : "取消报名失败";
            }
        }
    }
    // 签到成功 || 签到失败
    @Override
    public String participate (String userId, Integer eventId, Boolean isParticipate){
        if (userId == null || eventId == null || isParticipate == null) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR);
        }

        QueryWrapper<Participate> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("event_id", eventId);
        Participate participate = participateMapper.selectOne(queryWrapper);
        // 若无结果，则表示用户没有报名、没有订阅、更没有签到，直接插入一条记录
        if (participate == null) {
            participate = new Participate();
            participate.setUserId(userId);
            participate.setEventId(eventId);
            participate.setIsRegistration(true);
            participate.setIsParticipate(true);
            participate.setIsSubscribe(false);

            int insertResult = participateMapper.insert(participate);
            if (isParticipate) {
                return insertResult > 0 ? "签到成功" : "签到失败";
            } else {
                return insertResult > 0 ? "取消签到成功" : "取消签到失败";
            }
            // 若有结果，则表示用户已经报名、订阅、签到，直接更新记录
        } else{
            UpdateWrapper<Participate> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("user_id", userId);
            updateWrapper.eq("event_id", eventId);
            updateWrapper.set("is_participate", isParticipate);

            int updateResult = participateMapper.update(null, updateWrapper);
            if (isParticipate) {
                return updateResult > 0 ? "签到成功" : "签到失败";
            } else {
                return updateResult > 0 ? "取消签到成功" : "取消签到失败";
            }
        }

    }
    // 获取个人的活动的状态
    // 若无结果，则表示用户没有报名、没有订阅、更没有签到。
    @Override
    public Participate getParticipation(String userId, Integer eventId) {
        if (userId == null || eventId == null) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR);
        }
        Participate userparticipate = participateMapper.selectOne(new LambdaQueryWrapper<Participate>()
                .eq(Participate::getUserId, userId)
                .and(wrapper -> wrapper.eq(Participate::getEventId, eventId)));
        //id不会被json序列化，所以随便传了个
        return userparticipate != null ? userparticipate : new Participate(1, false, false, false, userId, eventId);
    }

    @Override
    public void deleteAllParticipateOfEvent(Integer eventId) {
        participateMapper.delete(Wrappers.lambdaQuery(Participate.class)
                .eq(Participate::getEventId, eventId));
    }
}