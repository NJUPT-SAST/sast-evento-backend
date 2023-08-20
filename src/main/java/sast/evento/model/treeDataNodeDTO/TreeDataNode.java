package sast.evento.model.treeDataNodeDTO;

import java.util.List;

public interface TreeDataNode {
     void addChildren(TreeDataNode node);
     void addChildren(List<TreeDataNode> nodes);

}
