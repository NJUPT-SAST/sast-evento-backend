package sast.evento.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/7/14 14:30
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class TraceLog {
    private final static String commonFormat = """      
                        
            ===========capture===========
            description：%s
            uri：%s
            method：%s
            spend_time：%s
            ===========release===========""";
    private final static String errorFormat = """
                        
            ===========capture===========
            description：%s
            uri：%s
            method：%s
            spend_time：%s
            stack_trace：%s
            ===========release===========""";
    private String description;
    private String uri;
    private String method;
    private Long startTime;
    private Long finishTime;
    private String stackTrace;

    public String toLogFormat(Boolean requestStatus) {
        return requestStatus ?
                String.format(commonFormat, description, uri, method, (finishTime - startTime) + "ms") :
                String.format(errorFormat, description, uri, method, (finishTime - startTime) + "ms", stackTrace);
    }
}
