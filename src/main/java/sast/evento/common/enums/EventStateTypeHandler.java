package sast.evento.common.enums;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/7/24 20:53
 */
public class EventStateTypeHandler extends BaseTypeHandler<EventState> {
    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, EventState eventState, JdbcType jdbcType) throws SQLException {
        preparedStatement.setInt(i,eventState.getState());
    }

    @Override
    public EventState getNullableResult(ResultSet resultSet, String s) throws SQLException {
        return EventState.getEventState(resultSet.getInt(s));
    }

    @Override
    public EventState getNullableResult(ResultSet resultSet, int i) throws SQLException {
        return EventState.getEventState(resultSet.getInt(i));
    }

    @Override
    public EventState getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        return EventState.getEventState(callableStatement.getInt(i));
    }
}
