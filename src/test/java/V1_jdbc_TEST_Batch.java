import com.edison.v1_jdbc.util.ConnectionUtil;
import org.junit.*;
import org.junit.runners.MethodSorters;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**尝试批量操作：
 * 使用statement、PrepareStatement、PrepareStatement+batch进行对比
 * 设置手动提交--重复测试差异很大，暂时不做参考
 *              插入1万条数据      5万      插入10万条数据
 * statement               6秒     28秒          79秒
 * PrepareStatement        8秒     30秒          57秒
 * PrepareStatement+batch  4秒     16秒          30秒
 * */
@FixMethodOrder(MethodSorters.NAME_ASCENDING) //这个是决定测试时方法执行的顺序
public class V1_jdbc_TEST_Batch {
    private static int BATCHSIZE=10000;
    private static Connection con;

//    @Before //@Before和@After在每个@Test方法前后都会执行 而 @BeforeClass和@AfterClass则只执行一次
    @BeforeClass //要设置为static
    public static void  init() throws SQLException {
        System.out.println("测试前：");
        con=ConnectionUtil.getConnection();
        con.setAutoCommit(false);//设置手动提交,一定要设置手动提交，否则每次自动提交耗时很多的
    }
    //@After
    @AfterClass
    public static void done(){
        System.out.println("测试后：");
        ConnectionUtil.closeConnection();
    }

    /**Statement
     * 使用范围：当执行相似SQL(结构相同，具体值不同)语句的次数比较少
     * 优点：语法简单
     * 缺点：采用硬编码效率低，安全性较差。
     * 原理：硬编码，每次执行时相似SQL都会进行编译*/
    @Test
    public void testBatchWithStatement() throws SQLException {
        Statement st=con.createStatement();
        long startTime=System.currentTimeMillis();
        for(int i=0;i<BATCHSIZE;i++){
            String sql="insert into comment(content,bid) values('好"+i+"'"+",1)";
            st.execute(sql);
        }
        long endTime=System.currentTimeMillis();
        con.commit();
        System.out.println("使用statement插入BATCHSIZE0条数据耗时:"+(endTime-startTime)/1000+"秒");
        ConnectionUtil.closeStatement(st);
    }

    /**PrepareStatement
     * 适用重复执行的sql
     * 防止sql注入*/
    @Test
    public void testBatchWithPrepareStatement() throws SQLException {
        String sql="insert into comment(content,bid) values(?,?)";
        PreparedStatement pst=con.prepareStatement(sql);

        long startTime=System.currentTimeMillis();
        for(int i=0;i<BATCHSIZE;i++){
           pst.setString(1,"好"+i);
           pst.setInt(2,1);
           pst.execute();
        }
        con.commit();

        long endTime=System.currentTimeMillis();
        System.out.println("使用PrepareStatement插入BATCHSIZE0条数据耗时:"+(endTime-startTime)/1000+"秒");
        ConnectionUtil.closeStatement(pst);
    }

    /**PrepareStatement+batch
     * 执行批量操作*/
    @Test
    public void testBatchWithPrepareStatementBatch() throws SQLException {
        String sql="insert into comment(content,bid) values(?,?)";
        PreparedStatement pst=con.prepareStatement(sql);

        long startTime=System.currentTimeMillis();
        for(int i=0;i<BATCHSIZE;i++){
            pst.setString(1,"好"+i);
            pst.setInt(2,1);
            pst.addBatch(); //添加到批量

            if((i%1000==0 && i>0) || i==BATCHSIZE-1){//1000条作为一个提交点/回滚点
                pst.executeBatch();
                con.commit();
                pst.clearBatch();//清除已提交的
            }
        }
        con.commit();

        long endTime=System.currentTimeMillis();
        System.out.println("使用PrepareStatement+batch插入BATCHSIZE0条数据耗时:"+(endTime-startTime)/1000+"秒");
        ConnectionUtil.closeStatement(pst);
    }
}
