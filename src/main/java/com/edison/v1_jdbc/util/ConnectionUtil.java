package com.edison.v1_jdbc.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;


/**
 * 1.打开关闭都需要用户自己去调用<br>
 * 2.异常控制也比较多，麻烦*/
public class ConnectionUtil {
	private static ThreadLocal<Connection> threadLocal=new ThreadLocal<Connection>();
	private static String driver=null;
	private static String url;
	private static String user;
	private static String password;
	
	//静态代码，ioc容器加载bean时就会执行
	public static void init() throws Throwable{
		
		Properties property=new Properties();
		property.load(ClassLoader.getSystemResourceAsStream("dataSource.properties"));
		driver=property.getProperty("driver");
		url=property.getProperty("url");
		user=property.getProperty("user");	
		password=property.getProperty("password");	
		Class.forName(driver);
		System.out.println("初始化init()完成");
		System.out.println("driver="+driver);
		System.out.println("url="+url);
		System.out.println("userName="+user);
		System.out.println("password="+password);
	}
	
	public static Connection getConnection(){
		if(driver==null){//进行初始化
			try{
				init();
			}catch(Throwable e){
				e.printStackTrace();
				return null;
			}
			
		}
		Connection con=threadLocal.get();
		System.out.println("获取连接开始");
		if(con==null){
			try{
				System.out.println("连接数据库开始...");
				con=DriverManager.getConnection(url, user, password);
				System.out.println("连接数据库成功");
			}catch(Exception e){
				System.out.println("getConnection fail");
				e.printStackTrace();
				return null;
			}
			threadLocal.set(con);
		}
		return con;
	}
	
	public static void closeConnection(){
		 Connection con=threadLocal.get();
	        if(con!=null){
	            try {
	                con.close();
	                System.out.println("关闭数据库连接");
	                threadLocal.set(null); //这个别忘了
	            }catch (Exception e){
	                e.printStackTrace();
	            }
	        }
	}
	
	public static void closeStatement(Statement statement){
		if(statement!=null){
			try{
                statement.close();
                System.out.println("关闭statement");
            }catch(Exception e){
                e.printStackTrace();
            }
		}
	}
	
	public static void closeResultSet(ResultSet rs){
		if(rs!=null){
			try{
                rs.close();
                System.out.println("关闭resultSet");
            }catch(Exception e){
                e.printStackTrace();
            }
		}
	}
	
	public static void closeAll(Statement statement,ResultSet rs){
		closeResultSet(rs);
		closeStatement(statement);
		closeConnection();
	}
}
