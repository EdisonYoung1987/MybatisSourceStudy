package com.edison.dao.extend;

import com.edison.dao.BlogMapper;
import com.edison.entity.Blog;
import com.edison.entity.extend.BlogAndAuthor;
import com.edison.entity.extend.BlogAndComment;
import com.edison.entity.extend.Qry;
import org.apache.ibatis.session.RowBounds;

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

    /**
     * 根据博客查询作者，一对一，嵌套结果
     * @param bid
     * @return
     */
    public BlogAndAuthor selectBlogWithAuthorResult(Integer bid);

    /**
     * 根据博客查询作者，一对一，嵌套查询，存在N+1问题
     * @param bid
     * @return
     */
    public BlogAndAuthor selectBlogWithAuthorQuery(Integer bid);

    /**根据blog的id查询blog及关联的评论*/
    public BlogAndComment selectBlogWithCommentById(Integer bid);

    /**逻辑分页RowBounds*/
    public List<Blog> selectByAuthorId(Integer authorId, RowBounds rb);

    public default void  printaa(){
        System.out.println("aa");
    }
}
