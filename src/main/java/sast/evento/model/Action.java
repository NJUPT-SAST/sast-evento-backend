package sast.evento.model;

import lombok.AllArgsConstructor;
import lombok.Data;

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

    private String group;//分组->父节点，默认default,方便前端选择

    private Boolean isVisible;

    private Boolean isPublic;


}
