package sast.evento.dataobject;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/7/18 21:56
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "participate")
public class Participate {

    private Integer id;

    private Boolean isRegistration;

    private Boolean isParticipate;

    private Integer eventId;

}
