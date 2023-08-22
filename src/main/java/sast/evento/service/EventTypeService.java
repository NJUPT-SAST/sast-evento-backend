package sast.evento.service;

import sast.evento.entitiy.EventType;

import java.util.List;

public interface EventTypeService {
    Boolean addEventType(EventType eventType);
    Boolean deleteEventType(Integer id);
    List<EventType> getAllEventType();
    Boolean editEventType(EventType eventType);
}
