import com.edison.dao.BlogMapper;
import com.edison.dao.extend.BlogExtMapper;
import com.edison.entity.Blog;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class V2_mybatis_TEST {
    public static void main(String[] args) {
        String resource = "mybatis-config.xml";
        InputStream inputStream = null;
        try {
            inputStream = Resources.getResourceAsStream(resource);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        SqlSession session = sqlSessionFactory.openSession();
        try {
            //使用mybatis已有的sql
            BlogMapper mapper = session.getMapper(BlogMapper.class);
            Blog blog = mapper.selectByPrimaryKey(1);
            if (blog == null) {
                System.out.println("没有该用户");
            } else {
                System.out.println(blog.getBid() + "-" + blog.getAuthorId() + "-" + blog.getName());
            }

            //使用扩展ext的sql-自定义
            BlogExtMapper extMapper=session.getMapper(BlogExtMapper.class);
            List<Blog> blogs=extMapper.selectByAuthorId(1001);
            for(Blog blog1:blogs){
                System.out.println(blog1.getBid() + "-" + blog1.getAuthorId() + "-" + blog1.getName());
            }
        }finally {
            session.close();
        }
    }
}
