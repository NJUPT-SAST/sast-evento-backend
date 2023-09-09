package sast.evento.entitiy;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/7/18 21:55
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "location")
public class Location {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String locationName;

    private Integer parentId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Objects.equals(id, location.id) && Objects.equals(locationName, location.locationName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, locationName);
    }
}
