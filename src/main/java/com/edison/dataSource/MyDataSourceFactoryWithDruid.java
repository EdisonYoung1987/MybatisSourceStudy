package com.edison.dataSource;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.datasource.DataSourceFactory;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;

public class MyDataSourceFactoryWithDruid implements DataSourceFactory {
    private  DataSource dataSource;
    private  Properties properties;

    @Override
    public void setProperties(Properties props) {
        this.properties=props;
    }

    @Override
    public DataSource getDataSource() {
        DruidDataSource dds=new DruidDataSource();
        dds.setUrl(properties.getProperty("url"));
        dds.setUsername(properties.getProperty("username"));
        dds.setPassword(properties.getProperty("password"));
        dds.setDriverClassName(properties.getProperty("driver"));
        try {
            dds.init();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return dds;
    }
}
