import com.edison.dao.BlogMapper;
import com.edison.dao.extend.BlogExtMapper;
import com.edison.entity.Blog;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@FixMethodOrder(MethodSorters.NAME_ASCENDING) //按方法名称顺序执行
public class V2_mybatis_TEST {
    private static SqlSessionFactory sqlSessionFactory;

    /**初始化，加载mybatis资源*/
    @BeforeClass
    public static void init() throws IOException {
        String resource = "mybatis-config.xml";
        InputStream inputStream = null;
        try {
            inputStream = Resources.getResourceAsStream(resource);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        inputStream.close();
    }

    /**mybatis的基础使用*/
    @Test
    public void A_baseUse() {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            //使用mybatis已有的sql
            System.out.println("使用自带的mapper中的sql");
            BlogMapper mapper = session.getMapper(BlogMapper.class);
            Blog blog = mapper.selectByPrimaryKey(1);
            if (blog == null) {
                System.out.println("没有该用户");
            } else {
                System.out.println(blog.getBid() + "-" + blog.getAuthorId() + "-" + blog.getName());
            }

            //使用扩展ext的sql-自定义
            System.out.println("使用扩展mapper中的sql");
            BlogExtMapper extMapper=session.getMapper(BlogExtMapper.class);
            List<Blog> blogs=extMapper.selectByAuthorId(1001);
            for(Blog blog1:blogs){
                System.out.println(blog1.getBid() + "-" + blog1.getAuthorId() + "-" + blog1.getName());
            }

            //使用扩展ext的sql-自定义-以bean作为参数
            System.out.println("使用扩展mapper中的sql2：用bean作为参数");
            Blog qryBlog=new Blog();
            qryBlog.setBid(3);
            blog=extMapper.selectByBean(qryBlog);
            System.out.println(blog.getBid() + "-" + blog.getAuthorId() + "-" + blog.getName());

        }finally {
            session.close();
        }
    }

    /**测试逻辑分页*/
    @Test
    public void
}
