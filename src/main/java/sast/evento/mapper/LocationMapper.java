package sast.evento.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import sast.evento.entitiy.Location;

@Mapper
@Repository
public interface LocationMapper extends BaseMapper<Location> {

    // 根据子地点 id 查询完整地点名
    // 结果为以空格分割的完整字符串，例：“中国 江苏省 南京市 栖霞区 南邮”
    String getLocationName(@Param("locationId") Integer locationId);

    void updateLocationName(@Param("id") Integer id, @Param("location_name") String locationName);
}
