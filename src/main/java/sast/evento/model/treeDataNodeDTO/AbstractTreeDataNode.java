package sast.evento.model.treeDataNodeDTO;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/8/17 14:40
 */
public class AbstractTreeDataNode implements TreeDataNode {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public List<TreeDataNode> children;

    @Override
    public void addChildren(TreeDataNode node) {
        if (!this.getClass().isInstance(node)) throw new IllegalArgumentException("error treeDataNode children type");
        if (children == null) children = new ArrayList<>();
        children.add(node);
    }

    @Override
    public void addChildren(List<TreeDataNode> nodes) {
        if (nodes.isEmpty()) return;
        if (!this.getClass().isInstance(nodes.stream().findAny().get()))
            throw new IllegalArgumentException("error treeDataNode children type");
        if (children == null) {
            children = nodes;
        } else {
            children.addAll(nodes);
        }
    }
}
