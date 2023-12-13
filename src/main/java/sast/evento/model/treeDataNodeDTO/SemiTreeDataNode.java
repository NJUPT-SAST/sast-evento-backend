package sast.evento.model.treeDataNodeDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/8/17 14:07
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SemiTreeDataNode extends AbstractTreeDataNode {

    private String label;

    private String value;

    private String key;

}
