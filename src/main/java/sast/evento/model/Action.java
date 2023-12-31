package sast.evento.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import sast.evento.common.enums.ActionState;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/7/14 17:44
 */
@Data
@AllArgsConstructor
public class Action {

    private final String methodName;//作为获取action唯一标准

    private final String group;

    private final ActionState actionState;

    private final String description;

}
