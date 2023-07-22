package sast.evento.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Objects;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/7/14 17:44
 */
@Data
@AllArgsConstructor
public class Action {

    private String description;

    private final String methodName;//作为分类的唯一标准

    private final String method;

    private final String url;

    private Boolean isVisible;

    private Boolean isPublic;

}
