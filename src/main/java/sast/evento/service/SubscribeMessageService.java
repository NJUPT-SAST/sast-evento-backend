package sast.evento.service;

import java.util.Date;

public interface SubscribeMessageService {
    /* 开启任务 */
    void open();

    /* 关闭任务 */
    void close();

    /* 查看任务是否关闭 */
    Boolean isClose();

    /* 添加定时读取并发送活动提醒任务 */
    void addWxSubScribeJob(Integer eventId, Date startTime);

    /* 更新任务时间 */
    void updateWxSubScribeJob(Integer eventId, Date startTime);

    /* 删除任务 */
    void removeWxSubScribeJob(Integer eventId);

}