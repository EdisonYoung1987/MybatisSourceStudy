package com.edison.dao.extend;

import com.edison.dao.BlogMapper;
import com.edison.entity.Blog;
import com.edison.entity.extend.Qry;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface BlogExtMapper extends BlogMapper {
    /**根据authorId返回所有blog*/
    List<Blog> selectByAuthorId(Integer authorId);

    /**传入bean作为参数*/
    Blog selectByBean(Blog blog);

    /**foreach传入bid的list进行查询*/
    List<Blog> selectByBidList(List<Integer> bidList);

    /**foreach,map形式*/
    List<Blog> selectByBidMap(Map<String,List<Integer>> map);

    /**foreach,多个list形式，且list类型不一致*/
    List<Blog> selectByBidNmMap(Qry qry);
}
