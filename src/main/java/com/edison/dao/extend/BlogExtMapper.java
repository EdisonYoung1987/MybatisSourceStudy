package com.edison.dao.extend;

import com.edison.dao.BlogMapper;
import com.edison.entity.Blog;

import java.util.List;

public interface BlogExtMapper extends BlogMapper {
    /**根据authorId返回所有blog*/
    List<Blog> selectByAuthorId(Integer authorId);
}
