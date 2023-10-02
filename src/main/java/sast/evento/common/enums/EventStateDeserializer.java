package sast.evento.common.enums;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.boot.jackson.JsonComponent;
import sast.evento.exception.LocalRunTimeException;

import java.io.IOException;

@JsonComponent
public class EventStateDeserializer extends JsonDeserializer<EventState> {

    public EventStateDeserializer() {
    }

    @Override
    public EventState deserialize(JsonParser p, DeserializationContext ctxt) {
        try {
            return EventState.getEventState(Integer.parseInt(p.getText()));
        } catch (IOException e) {
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "error get eventState");
        }
    }

}