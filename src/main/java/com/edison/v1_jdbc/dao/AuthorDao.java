package com.edison.v1_jdbc.dao;

import com.edison.entity.Author;
import com.edison.v1_jdbc.util.ConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * 感觉一个sql就是一段代码，非常麻烦，sql语句还代码交织在一起<br>
 * 如果一个表，有几十条语句去检索不同结果集，处理起来就非常麻烦<br>
 * 如果表结构发生改变，那么语句也要改，很麻烦*/
public class AuthorDao {
	public List<Author> selectList(String forumName){ //用Author名称模糊查询
//		String sql="SELECT * FROM FORUM WHERE FORUMNAME LIKE '%"+forumName+"%'";
		String sql="SELECT * FROM AUTHOR WHERE AUTHOR_NAME LIKE ?"; //使用占位符可以防止sql注入
		ResultSet rs=null;
		List<Author> forumList=null;
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
			forumList=new ArrayList<Author>();
			
			while(rs.next()){
				Author forum=new Author();
				forum.setAuthorId(rs.getInt(1));
				forum.setAuthorName(rs.getString("AUTHOR_NAME")); //不知道该大写还是小写 好像不影响
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
