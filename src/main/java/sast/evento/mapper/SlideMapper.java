package sast.evento.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import sast.evento.entitiy.Slide;

@Mapper
@Repository
public interface SlideMapper extends BaseMapper<Slide> {
    void updateSlide(@Param("event_id") Integer eventId,
                     @Param("id") Integer slideId,
                     @Param("url") String url,
                     @Param("link") String link,
                     @Param("title") String title);
}
