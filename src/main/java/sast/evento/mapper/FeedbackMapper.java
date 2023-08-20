package sast.evento.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import sast.evento.entitiy.Feedback;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface FeedbackMapper extends BaseMapper<Feedback> {
    /**
     * @param index 页数
     * @param size  每页显示的数量
     * @return Map<Integer, Integer>
     * @author Aiden
     */
    List<Map<String, Integer>> getFeedbackEvents(@Param("index") Integer index, @Param("size") Integer size);

}
