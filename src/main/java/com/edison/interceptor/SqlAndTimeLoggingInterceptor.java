package com.edison.interceptor;

import com.alibaba.druid.pool.DruidPooledPreparedStatement;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;

import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Properties;

/**打印最终执行的sql以及耗时，将StatementHandler作为拦截对象*/
@Intercepts(
        {
                @Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class}),
        }
)
public class SqlAndTimeLoggingInterceptor implements Interceptor{
    /**慢sql耗时-单位毫秒*/
    private long sqlTime=0L;

    /**当执行StatementHandler.query(x,x)时，这个方法会被调用：
     * @param invocation -包含被拦截对象、method、参数信息*/
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object target = invocation.getTarget(); //被代理对象
        Method method = invocation.getMethod(); //被代理方法
        Object[] args = invocation.getArgs();   //被代理参数
        Statement statement=(Statement) args[0];

        System.out.println("自定义插件进行拦截：");

        //获取执行时间
        long startTime=System.currentTimeMillis();
        Object result=method.invoke(target,args); //执行原方法
        long endTime=System.currentTimeMillis();

        //获取sql
        ParameterHandler parameterHandler=((StatementHandler)target).getParameterHandler();
        parameterHandler.setParameters((PreparedStatement) statement);

        String sql="";
        ((DruidPooledPreparedStatement) st).getParameterMetaData();
        System.out.println(st.getClass().getSimpleName());
        if(st instanceof DruidPooledPreparedStatement){

            sql=((DruidPooledPreparedStatement) st).getSql();
        }else {
            BoundSql boundSql=((StatementHandler)target).getParameterHandler().getBoundSql();//这个还是?,不是具体值
            sql=boundSql.getSql();
        }

        sql=sql.replaceAll("\n"," ").replaceAll("  *"," ");
        String info=sql+",耗时："+(endTime-startTime)+"毫秒!";
        if((endTime-startTime)>=sqlTime){
            System.err.println("慢sql:"+info);
        }else
            System.err.println("执行sql:"+info);

        return result;
    }

    @Override
    public Object plugin(Object target) { //这个都是调用插件的wrap包装方法
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        //目前假设只有一个耗时时长来过滤慢sql
        sqlTime=Long.parseLong(properties.getProperty("sqlTime"));
    }
}
