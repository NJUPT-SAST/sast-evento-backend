package sast.evento.service;

import sast.evento.entitiy.Participate;

/**
 * @projectName: sast-evento-backend
 * @author: mio
 * @date: 2023/8/16 23:17
 */
public interface ParticipateService {

    // 订阅活动 / 取消订阅
    String subscribe(String userId, Integer eventId, Boolean isSubscribe);

    // 报名活动 / 取消报名
    String register(String userId, Integer eventId, Boolean isRegister);

    // 获取个人的活动的状态
    Participate getParticipation(String userId, Integer eventId);

}
