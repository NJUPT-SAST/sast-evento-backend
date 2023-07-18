package sast.evento.dataobject;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    private Integer id;

    private String locationName;

    private Integer parentId;


}
