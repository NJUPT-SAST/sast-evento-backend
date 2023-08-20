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
 * @date: 2023/7/24 20:29
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "department")
public class Department {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String departmentName;

}
