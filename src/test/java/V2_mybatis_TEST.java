import com.edison.dao.BlogMapper;
import com.edison.dao.extend.BlogExtMapper;
import com.edison.entity.Blog;
import com.edison.entity.extend.BlogAndAuthor;
import com.edison.entity.extend.BlogAndComment;
import com.edison.entity.extend.Qry;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**用于测试动态sql中的foreach，其他的如if、where、set等直接参考笔记就可以了*/
    @Test
    public void B_Foreach(){
        SqlSession session = sqlSessionFactory.openSession();
        try {
            System.out.println("测试foreach动态sql");
            List<Integer> bidList = new ArrayList<>();
            bidList.add(1);
            bidList.add(2);
            bidList.add(3);
            BlogExtMapper extMapper = session.getMapper(BlogExtMapper.class);
            List<Blog> blogs = extMapper.selectByBidList(bidList);
            System.out.println("查询到记录条数：" + blogs.size());

            //foreach的map形式
            List<Integer> authorIdList = new ArrayList<>();
            authorIdList.add(1001);
            authorIdList.add(1008);

            Map<String, List<Integer>> map = new HashMap<>(4);
            map.put("bids",bidList);
            map.put("authorIds",authorIdList);

            blogs = extMapper.selectByBidMap(map);
            System.out.println("查询到记录条数：" + blogs.size());

            //foreach：多个不同类型List,多个list
            List<String> nameList=new ArrayList<>();
            nameList.add("新增消息");
            nameList.add("MyBatis源码分析");
            Qry qry=new Qry();
            qry.setIntList(bidList);
            qry.setStringList(nameList);
            blogs=extMapper.selectByBidNmMap(qry);
            System.out.println("查询到记录条数：" + blogs.size());

        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            session.close();
        }
    }

    /**测试mybatis的批量插入*/
    @Test
    public void C_insertBatch(){
        //制定执行器类型为BATCH,其他的还有SIMPLE\REUSER,关闭自动提交
//        SqlSession session = sqlSessionFactory.openSession(ExecutorType.SIMPLE,false);
//        SqlSession session = sqlSessionFactory.openSession(ExecutorType.REUSE,false);
        SqlSession session = sqlSessionFactory.openSession(ExecutorType.BATCH,false);

        try {
            List<Blog> blogs=new ArrayList<>(16);
            blogs.add(new Blog(5,"a",1008));
            blogs.add(new Blog(6,"ab",1008));
            blogs.add(new Blog(7,"a",1001));
            blogs.add(new Blog(8,"ad",1001));

            BlogMapper mapper = session.getMapper(BlogMapper.class);
            int i=1;
            for(Blog blog: blogs) {
                mapper.insert(blog);
                if((i++)%2==0 ){
                    Thread.sleep(5000);
                    session.flushStatements();//批量提交一部分sql
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            session.commit();
            session.close();
        }
    }

    /**嵌套查询、关联查询：1对1或1对多*/
    /**
     * 一对一，一篇文章对应一个作者：查询到blog后，马上用blog.author_id去查询author信息；
     * 如果参数设置了延迟加载lazyLoadingEnabled，则会在使用author时再次发送sql
     * 嵌套查询，会有N+1的问题:sql语句不是一次性发送的
     */
    @Test
    public void testSelectBlogWithAuthorQuery() throws IOException {
        SqlSession session = sqlSessionFactory.openSession();
        BlogExtMapper mapper = session.getMapper(BlogExtMapper.class);

        BlogAndAuthor blog = mapper.selectBlogWithAuthorQuery(1);
        System.out.println("-----------:"+blog.getClass());
        // 如果开启了延迟加载，会在使用的时候才发出SQL
        // equals,clone,hashCode,toString也会触发延迟加载
        // System.out.println("-----------调用toString方法:"+blog);
        System.out.println("-----------getAuthor:"+blog.getAuthor().toString());
        // 如果 aggressiveLazyLoading = true ，也会触发加载，否则不会
        //System.out.println("-----------getName:"+blog.getName());
    }

    /**
     * 一对多关联查询：一篇文章对应多条评论
     * @throws IOException
     */
    @Test
    public void testSelectBlogWithComment() throws IOException {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            BlogExtMapper mapper = session.getMapper(BlogExtMapper.class);
            BlogAndComment blog = mapper.selectBlogWithCommentById(1);
            System.out.println(blog);
        } finally {
            session.close();
        }
    }

    /**
     * 一对一，一篇文章对应一个作者
     * 嵌套结果，不存在N+1问题:
     */
    @Test
    public void testSelectBlogWithAuthorResult() throws IOException {
        SqlSession session = sqlSessionFactory.openSession();
        BlogExtMapper mapper = session.getMapper(BlogExtMapper.class);

        BlogAndAuthor blog = mapper.selectBlogWithAuthorResult(2);
        System.out.println("-----------:"+blog);
    }


//    /**测试逻辑分页*/
//    @Test
//    public void
}
