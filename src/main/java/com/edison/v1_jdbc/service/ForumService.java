package com.edison.v1_jdbc.service;

import java.util.List;

import com.edison.testJunit.oth.database.entity.Forum;
import com.edison.testJunit.oth.database.pureJdbc.dao.ForumDao;


public class ForumService {
	public void queryForumByName(String name){
		List<Forum> forumList=null;
		ForumDao forumDao=new ForumDao();
		try{
			forumList=forumDao.selectList(name);
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("查询论坛失败");
		}
		
		if(forumList!=null){
			for(Forum forum:forumList){
				System.out.println(forum.toString());
			}
		}
	}
}
