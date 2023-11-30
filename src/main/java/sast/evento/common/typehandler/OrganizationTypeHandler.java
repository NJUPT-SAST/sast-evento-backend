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
        preparedStatement.setInt(i, Organization.getByStr(s).getId());
    }

    @Override
    public String getNullableResult(ResultSet resultSet, String s) throws SQLException {
        return Organization.getById(resultSet.getInt(s)).getOrg();
    }

    @Override
    public String getNullableResult(ResultSet resultSet, int i) throws SQLException {
        return Organization.getById(resultSet.getInt(i)).getOrg();
    }

    @Override
    public String getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        return Organization.getById(callableStatement.getInt(i)).getOrg();
    }
}
