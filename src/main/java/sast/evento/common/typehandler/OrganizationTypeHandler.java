package sast.evento.common.typehandler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import sast.sastlink.sdk.enums.Organization;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrganizationTypeHandler extends BaseTypeHandler<String> {

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, String s, JdbcType jdbcType) throws SQLException {
        Organization org = Organization.getByStr(s);
        Integer res = org == null ? null : org.getId();
        preparedStatement.setObject(i, res, 4);
    }

    @Override
    public String getNullableResult(ResultSet resultSet, String s) throws SQLException {
        Object o = resultSet.getObject(s);
        return o == null ? null : Organization.getById((Integer) o).getOrg();
    }

    @Override
    public String getNullableResult(ResultSet resultSet, int i) throws SQLException {
        Object o = resultSet.getObject(i);
        return o == null ? null : Organization.getById((Integer) o).getOrg();
    }

    @Override
    public String getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        Object o = callableStatement.getObject(i);
        return o == null ? null : Organization.getById((Integer) o).getOrg();
    }
}
