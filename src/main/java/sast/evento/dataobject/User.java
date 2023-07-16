package sast.evento.dataobject;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/7/13 16:53
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "user")
public class User {
    @TableId(value = "id")
    private String userId;

    private String permission;//json

}
