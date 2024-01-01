package sast.evento.common.typehandler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Aiden
 * @date 2023/12/31 14:50
 */
public class FeedbackScoreTypeHandler extends BaseTypeHandler<Double> {
    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, Double aDouble, JdbcType jdbcType) throws SQLException {
        preparedStatement.setDouble(i, aDouble * 10);
    }

    @Override
    public Double getNullableResult(ResultSet resultSet, String s) throws SQLException {
        return resultSet.getDouble(s) / 10;
    }

    @Override
    public Double getNullableResult(ResultSet resultSet, int i) throws SQLException {
        return resultSet.getDouble(i) / 10;
    }

    @Override
    public Double getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        return callableStatement.getDouble(i) / 10;
    }
}
