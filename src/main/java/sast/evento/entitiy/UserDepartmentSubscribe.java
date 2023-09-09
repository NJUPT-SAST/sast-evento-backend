package sast.evento.entitiy;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "user_department_subscribe")
public class UserDepartmentSubscribe {
    @TableId(type = IdType.AUTO)
    @JsonIgnore
    private Integer id;

    private String userId;

    private Integer departmentId;

    @JsonIgnore
    @TableField(exist = false)
    private String email;

    @JsonIgnore
    @TableField(exist = false)
    private String openId;

}
