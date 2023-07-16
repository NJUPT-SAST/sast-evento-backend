package sast.evento.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.util.Date;
import java.util.List;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/7/14 12:32
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Permission {
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    private Date Version;//最后一次更改日期

    private List<Statement> Statements;
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Statement {

        private List<Integer> ActionIds;

        private String Resource;
        @Nullable
        @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
        private Date Conditions;
    }




}
