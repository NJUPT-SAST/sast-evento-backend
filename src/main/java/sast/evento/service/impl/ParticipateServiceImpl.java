package sast.evento.service.impl;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import sast.evento.common.enums.ErrorEnum;
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
    public String subscribe(Integer userId, Integer eventId, Boolean isSubscribe) {
        if (userId == null || eventId == null || isSubscribe == null) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR);
        }

        Integer updateResult = participateMapper.subscribe(userId, eventId, isSubscribe);
        if (isSubscribe == true) {
            return updateResult != null && updateResult > 0 ? "订阅成功" : "订阅失败";
        } else {
            return updateResult != null && updateResult > 0 ? "取消订阅成功": "取消订阅失败";
        }
    }

    // 报名活动
    @Override
    public String register(Integer userId, Integer eventId) {
        if (userId == null || eventId == null) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR);
        }
        Integer insertResult = participateMapper.register(userId, eventId);
        return insertResult != null && insertResult > 0 ? "报名成功" : "报名失败";
    }
}
