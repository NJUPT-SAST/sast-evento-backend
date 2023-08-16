package sast.evento.service;

/**
 * @projectName: sast-evento-backend
 * @author: mio
 * @date: 2023/8/16 23:17
 */
public interface ParticipateService {

    // 订阅活动 / 取消订阅
    String subscribe(Integer userId, Integer eventId, Boolean isSubscribe);

    // 报名活动
    String register(Integer userId, Integer eventId);

}
