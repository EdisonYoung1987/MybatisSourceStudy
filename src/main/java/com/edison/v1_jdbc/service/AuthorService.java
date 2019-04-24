package com.edison.v1_jdbc.service;

import com.edison.entity.Author;
import com.edison.v1_jdbc.dao.AuthorDao;

import java.util.List;


public class AuthorService {
	public void queryForumByName(String name){
		List<Author> forumList=null;
		AuthorDao forumDao=new AuthorDao();
		try{
			forumList=forumDao.selectList(name);
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("查询论坛失败");
		}
		
		if(forumList!=null){
			for(Author forum:forumList){
				System.out.println(forum.getAuthorId()+"--"+forum.getAuthorName());
			}
		}
	}
}
