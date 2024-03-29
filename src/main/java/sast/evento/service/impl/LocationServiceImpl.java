package sast.evento.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import sast.evento.common.enums.ErrorEnum;
import sast.evento.entitiy.Location;
import sast.evento.exception.LocalRunTimeException;
import sast.evento.mapper.EventMapper;
import sast.evento.mapper.LocationMapper;
import sast.evento.model.treeDataNodeDTO.SemiTreeDataNode;
import sast.evento.model.treeDataNodeDTO.TreeDataNode;
import sast.evento.service.LocationService;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LocationServiceImpl implements LocationService {
    @Resource
    private LocationMapper locationMapper;

    @Resource
    private EventMapper eventMapper;

    @Override
    public Integer addLocation(Location location) {
        if (location == null || location.getLocationName() == null || location.getParentId() == null) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR);
        }
        checkParentId(location);
        boolean isSuccess = locationMapper.insert(location) > 0;
        if (!isSuccess) {
            throw new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "add location failed");
        }
        return location.getId();
    }

    @Override
    public Boolean deleteLocation(Integer id) {
        if (id.equals(0)) {
            throw new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "can not delete root location");
        }
        // check if the location has children
        if (locationMapper.selectCount(Wrappers.lambdaQuery(Location.class)
                .eq(Location::getParentId, id)) > 0) {
            throw new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "can not delete a location with children");
        }
        // check if the location is used in events
        if (eventMapper.selectCount(Wrappers.lambdaQuery(sast.evento.entitiy.Event.class)
                .eq(sast.evento.entitiy.Event::getLocationId, id)) > 0) {
            throw new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "can not delete a location used in events");
        }

        boolean isSuccess = locationMapper.deleteById(id) > 0;
        if (!isSuccess) {
            throw new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "delete location failed");
        }
        return true;
    }

    @Override
    @CacheEvict(value = "event")
    public Boolean updateLocation(Location location) {
        if (location == null) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR);
        }
        checkParentId(location);
        UpdateWrapper<Location> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", location.getId());
        if (!(locationMapper.update(location, updateWrapper) > 0)) {
            throw new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "delete failed");
        }
        return true;
    }

    @Override
    public void updateLocationName(Integer id, String locationName) {
        locationMapper.updateLocationName(id, locationName);
    }

    @Override
    public List<TreeDataNode> getLocations() {
        List<Location> locations = locationMapper.selectList(null);
        return toTreeData(locations);
//        HashMap<Integer, List<TreeDataNode>> nodeMap = locations.stream()
//                .collect(HashMap::new, (map, location) -> {
//                    if (!map.containsKey(location.getParentId())) {
//                        map.put(location.getParentId(), new ArrayList<>());
//                    } else {
//                        map.get(location.getParentId()).add(new AntDesignTreeDataNode(location.getLocationName(), location.getLocationName(), location.getId().toString()));
//                    }
//                }, HashMap::putAll);
//        return nodeMap.keySet().stream()
//                .collect(
//                        ArrayList::new,
//                        (treeDataNodes, s) -> {
//                            String locationName = locations.get(s).getLocationName();
//                            TreeDataNode node = new AntDesignTreeDataNode(locationName, locationName, s.toString());
//                            node.addChildren(nodeMap.get(s));
//                            treeDataNodes.add(node);
//                        },
//                        List::addAll);
    }

    private void checkParentId(Location location) {
        Integer parentId = location.getParentId();
        if (parentId != 0 && locationMapper.selectById(parentId) == null) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "parent_id do not exist");
        }
    }

    private List<TreeDataNode> toTreeData(List<Location> locations) {
        Location root = locations.stream()
                .filter(location -> location.getId().equals(0))
                .findAny()
                .orElseThrow(() -> new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "no root location"));
        Map<Integer, TreeDataNode> head = Map.of(0, getTreeData(root.getLocationName(), root.getId()));
        locations.remove(root);
        Map<Integer, TreeDataNode> fathers = head;
        Map<Integer, TreeDataNode> children = new HashMap<>();
        while (!locations.isEmpty()) {
            for (Map.Entry<Integer, TreeDataNode> father : fathers.entrySet()) {
                List<Location> del = new ArrayList<>();
                for (Location location : locations) {
                    if (father.getKey().equals(location.getParentId())) {
                        TreeDataNode child = getTreeData(location.getLocationName(), location.getId());
                        children.put(location.getId(), child);
                        father.getValue().addChildren(child);
                        del.add(location);
                    }
                }
                locations.removeAll(del);
            }
            fathers = children;
            children = new HashMap<>();
        }
        return List.of(head.get(0));
    }

    private TreeDataNode getTreeData(String name, Integer id) {
        return new SemiTreeDataNode(name, String.valueOf(id), String.valueOf(id));
    }

    @Override
    public Map<Integer,String> getLocationStrMap(){
        List<Location> locations = locationMapper.selectList(null);
        Map<Integer,String> res = new HashMap<>();
        Map<Integer,Location> Id2LocationMap = locations.stream()
                .collect(Collectors.toMap(Location::getId, location -> location));
        Location root = locations.stream()
                .filter(location -> location.getId().equals(0))
                .findAny()
                .orElseThrow(() -> new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "no root location"));
        locations.remove(root);
        res.put(0,"");
        while (!locations.isEmpty()){
            List<Location> del = new ArrayList<>();
            for (Location needUpdate: locations) {
                if(needUpdate.getParentId().equals(0)){
                    del.add(needUpdate);
                    res.put(needUpdate.getId(),needUpdate.getLocationName());
                }else {
                    Location parent = Id2LocationMap.get(needUpdate.getParentId());
                    needUpdate.setParentId(parent.getParentId());
                    needUpdate.setLocationName(parent.getLocationName()+" "+needUpdate.getLocationName());
                }
            }
            locations.removeAll(del);
        }
        return res;
    }
}
