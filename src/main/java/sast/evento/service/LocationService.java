package sast.evento.service;

import sast.evento.entitiy.Location;
import sast.evento.model.treeDataNodeDTO.TreeDataNode;

import java.util.List;
import java.util.Map;

public interface LocationService {
    Integer addLocation(Location location);
    Boolean deleteLocation(Integer id);
    Boolean updateLocation(Location location);
    void updateLocationName(Integer id,String locationName);
    List<TreeDataNode> getLocations();
    Map<Integer,String> getLocationStrMap();
}
