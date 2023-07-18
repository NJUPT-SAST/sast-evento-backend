package sast.evento.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.lang.Nullable;
import sast.evento.dataobject.Action;

import java.util.ArrayList;
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
    private Date version;//最后一次更改日期

    private List<Statement> statements;
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Statement {

        private List<Action> actions;

        private String resource;
        @Nullable
        @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
        private Date conditions;
    }

    public static Permission getDefault(){
        Permission permission = new Permission();
        permission.setVersion(new Date());
        List<Permission.Statement> statements = new ArrayList<>();
        Permission.Statement statement = new Permission.Statement();
        statement.setResource("all");
        statements.add(statement);
        permission.setStatements(statements);
        return permission;
    }

}
