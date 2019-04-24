package com.edison.v1_jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


import com.edison.v1_jdbc.util.ConnectionUtil;

/**
 * 感觉一个sql就是一段代码，非常麻烦，sql语句还代码交织在一起<br>
 * 如果一个表，有几十条语句去检索不同结果集，处理起来就非常麻烦<br>
 * 如果表结构发生改变，那么语句也要改，很麻烦*/
public class ForumDao {
	public List<Forum> selectList(String forumName){ //用Forum名称模糊查询
//		String sql="SELECT * FROM FORUM WHERE FORUMNAME LIKE '%"+forumName+"%'";
		String sql="SELECT * FROM FORUM WHERE FORUMNAME LIKE ?"; //使用占位符可以防止sql注入
		ResultSet rs=null;
		List<Forum> forumList=null;
		PreparedStatement statement=null;
		
		Connection con=ConnectionUtil.getConnection();
		if(con==null){
			System.out.println("获取数据库连接为null");
			return null;
		}
		
		try{
			statement=con.prepareStatement(sql);
			statement.setString(1, "%"+forumName+"%"); //like语句只能把%放这，不然会报错：无效的列索引
			rs=statement.executeQuery();
			forumList=new ArrayList<Forum>();
			
			while(rs.next()){
				Forum forum=new Forum();
				forum.setForumId(rs.getInt(1));
				forum.setForumName(rs.getString("FORuMNAME")); //不知道该大写还是小写 好像不影响
				forum.setForumDesc(rs.getString(3));
				forumList.add(forum);
			}
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}finally{
			ConnectionUtil.closeAll(statement, rs);
		}
		return forumList;
	}
}
