package sast.evento.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import sast.evento.entitiy.ImageDO;

import java.util.List;

@Mapper
@Repository
public interface ImageDOMapper extends BaseMapper<ImageDO> {
    List<String> selectDirs();
}
