import com.edison.v1_jdbc.service.AuthorService;

/**用来测试纯jdbc查询*/
public class V1_jdbc_TEST {
    public static void main(String[] args) {
        AuthorService service=new AuthorService();
        service.queryForumByName("青山");
    }
}
