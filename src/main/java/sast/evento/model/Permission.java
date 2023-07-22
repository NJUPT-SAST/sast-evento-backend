package sast.evento.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

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
    private Date version;

    private List<Statement> statements;
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Statement {

        private List<String> methodNames;//解决存全部的长度问题和资源浪费问题

        private String resource;
        @Nullable
        @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
        private Date conditions;//管理端：查看所有有权管理活动的用户
    }

    public static Permission getDefault(){
        Permission permission = new Permission();
        permission.setVersion(new Date());
        List<Permission.Statement> statements = new ArrayList<>();
        Permission.Statement statement = new Permission.Statement();
        statement.setResource("common");
        statements.add(statement);
        permission.setStatements(statements);
        return permission;
    }

}
