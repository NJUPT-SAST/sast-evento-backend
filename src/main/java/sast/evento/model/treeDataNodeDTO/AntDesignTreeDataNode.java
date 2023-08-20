package sast.evento.model.treeDataNodeDTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/8/16 22:16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AntDesignTreeDataNode extends AbstractTreeDataNode {

    private String title;

    private String value;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String key;
}
