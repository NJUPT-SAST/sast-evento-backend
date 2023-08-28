package sast.evento.service;

import sast.evento.entitiy.Location;
import sast.evento.model.treeDataNodeDTO.TreeDataNode;

import java.util.List;

public interface LocationService {
    Integer addLocation(Location location);
    Boolean deleteLocation(Integer id);
    Boolean updateLocation(Location location);
    List<TreeDataNode> getLocations();
}
