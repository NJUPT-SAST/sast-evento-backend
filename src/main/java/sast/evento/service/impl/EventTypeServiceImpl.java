package sast.evento.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import sast.evento.common.enums.ErrorEnum;
import sast.evento.entitiy.EventType;
import sast.evento.exception.LocalRunTimeException;
import sast.evento.mapper.EventTypeMapper;
import sast.evento.service.EventTypeService;

import java.util.List;

@Service
public class EventTypeServiceImpl implements EventTypeService {
    @Resource
    private EventTypeMapper eventTypeMapper;
    @Override
    public Integer addEventType(EventType eventType) {
        if (eventType == null) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR);
        }
        if (eventType.getTypeName() == null) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR);
        }
        if (eventType.getAllowConflict() == null) {
            eventType.setAllowConflict(true);
        }
        boolean isSuccess = eventTypeMapper.insert(eventType) > 0;
        if (!isSuccess) {
            throw new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "add eventType failed");
        }
        return eventType.getId();
    }

    @Override
    public Boolean deleteEventType(Integer id) {
        if (id == null) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR);
        }
        boolean isSuccess = eventTypeMapper.deleteById(id) > 0;
        if (!isSuccess) {
            throw new LocalRunTimeException(ErrorEnum.COMMON_ERROR, "delete eventType failed");
        }
        return true;
    }

    @Override
    public List<EventType> getAllEventType() {
        return eventTypeMapper.selectList(null);
    }

    @Override
    @CacheEvict(value = "event")
    public Boolean editEventType(EventType eventType) {
        UpdateWrapper<EventType> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", eventType.getId());
        return eventTypeMapper.update(eventType, updateWrapper) > 0;
    }
}
