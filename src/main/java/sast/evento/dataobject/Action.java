package sast.evento.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.lang.NonNull;

import java.util.Objects;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/7/14 17:44
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "action")
@Accessors(chain = true)
public class Action {
    @TableId(value = "id", type = IdType.AUTO)
    @NonNull
    private Integer id;

    private String actionName;

    private String method;

    private String url;

    private Boolean isVisible;

    private Boolean isPublic;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Action action = (Action) o;
        return id.equals(action.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


}
