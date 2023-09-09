package sast.evento.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import sast.evento.model.EventModel;
import sast.evento.model.PageModel;

import java.util.Date;
import java.util.List;

/**
 * @projectName: sast-evento-backend
 * @author: mio
 * @date: 2023/8/8 17:46
 */
@Mapper
@Repository
public interface EventModelMapper {
    // 获取活动详情
    EventModel getById(@Param("eventId") Integer eventId);

    // 正在进行的活动列表
    List<EventModel> getConducting();

    // 查看用户历史活动列表（参加过已结束）
    List<EventModel> getHistory(@Param("userId") String userId);

    // 最新的活动列表（按开始时间正序排列未开始的活动）
    List<EventModel> getNewest();

    // 获取活动列表(分页）
//    List<EventModel> getEvents(@Param("index") Integer index, @Param("size") Integer size);
    PageModel<EventModel> getEvents(@Param("index") Integer index, @Param("size") Integer size);

    List<EventModel> getEventList();

    /**
     * @param monday     Date
     * @param nextMonday Date
     * @return 根据time选出活动列表
     * @author Aiden
     */
    List<EventModel> getEventByTime(@Param("monday") Date monday, @Param("next_monday") Date nextMonday);

    /**
     * @param departmentId List<Integer>
     * @return 根据departmentId选出活动列表
     * @author Aiden
     */
    List<EventModel> getEventByDepartmentId(@Param("department_id") List<Integer> departmentId);

    /**
     * @param departmentId List<Integer>
     * @param monday       Date
     * @param nextMonday   Date
     * @return 根据departmentId和time选出活动列表
     * @author Aiden
     */
    List<EventModel> getEventByDepartmentIdAndTime(@Param("department_id") List<Integer> departmentId,
                                                   @Param("monday") Date monday,
                                                   @Param("next_monday") Date nextMonday);

    /**
     * @param typeId List<Integer>
     * @return 根据typeId选出活动列表
     * @author Aiden
     */
    List<EventModel> getEventByTypeId(@Param("type_id") List<Integer> typeId);

    /**
     * @param typeId     List<Integer>
     * @param monday     Date
     * @param nextMonday Date
     * @return 根据typeId和time选出活动列表
     * @author Aiden
     */
    List<EventModel> getEventByTypeIdAndTime(@Param("type_id") List<Integer> typeId,
                                             @Param("monday") Date monday,
                                             @Param("next_monday") Date nextMonday);

    /**
     * @param typeId       List<Integer>
     * @param departmentId List<Integer>
     * @return 根据typeId和departmentId选出活动列表
     * @author Aiden
     */
    List<EventModel> getEventByTypeIdAndDepartmentId(@Param("type_id") List<Integer> typeId,
                                                     @Param("department_id") List<Integer> departmentId);

    /**
     * @param typeId       List<Integer>
     * @param departmentId List<Integer>
     * @param monday       Date
     * @param nextMonday   Date
     * @return 根据三个筛选条件选出活动列表
     * @author Aiden
     */
    List<EventModel> postForEventsByAll(@Param("type_id") List<Integer> typeId,
                                        @Param("department_id") List<Integer> departmentId,
                                        @Param("monday") Date monday,
                                        @Param("next_monday") Date nextMonday);


    // 获取已订阅的活动列表
    List<EventModel> getSubscribed(@Param("userId") String userId);

    // 获取已报名的活动列表
    List<EventModel> getRegistered(@Param("userId") String userId);

}