package com.edison.handler;

import org.apache.ibatis.executor.result.ResultMapException;
import org.apache.ibatis.type.*;

import java.sql.ResultSet;
import java.sql.SQLException;

/**处理返回的String类型*/
//@MappedTypes({String.class}) 无效
@MappedJdbcTypes({JdbcType.VARCHAR,JdbcType.NVARCHAR}) //加了这个，就不需要在mapper的字段上面再一个个加属性了
public class MyStringTypeHandler extends StringTypeHandler {
    @Override
    public String getResult(ResultSet rs, String columnName)
            throws SQLException
    {
        String result;
        try
        {
            result = getNullableResult(rs, columnName);
        } catch (Exception e) {
            throw new ResultMapException("Error attempting to get column '" + columnName + "' from result set.  Cause: " + e, e);
        }

        if (result==null)
            return "空值";
        if(rs.wasNull()){
            return "空值2";
        }
        return result;
    }
    @Override
    public String getResult(ResultSet rs, int columnIndex) throws SQLException
    {
        String result;
        try
        {
            result = getNullableResult(rs, columnIndex);
        } catch (Exception e) {
            throw new ResultMapException("Error attempting to get column #" + columnIndex + " from result set.  Cause: " + e, e);
        }

        if (result==null)
            return "空值";
        if(rs.wasNull()){
            return "空值2";
        }
        return result;
    }
}
