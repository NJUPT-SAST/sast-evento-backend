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
    // 参与活动 / 取消参与 (因为只有在线下扫码才能参与，且不可撤销，因此默认状态是未参与
    // 和小程序端协商了下，哪怕表里没有记录（用户没有注册该活动），也可以参与，会直接在表里插入一条记录
    String participate(String userId, Integer eventId, Boolean isParticipate);
    // 获取个人的活动的状态
    Participate getParticipation(String userId, Integer eventId);

}
