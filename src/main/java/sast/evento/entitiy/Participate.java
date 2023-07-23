package sast.evento.entitiy;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Boolean isRegistration;

    private Boolean isParticipate;

    private Boolean isManage;

    private Integer eventId;

}
