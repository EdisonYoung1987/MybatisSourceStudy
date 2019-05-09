package com.edison.interceptor;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;

import java.lang.reflect.Method;
import java.sql.Statement;
import java.util.List;
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
//        Object result=method.invoke(target,args); //执行原方法
        Object result=invocation.proceed();//保证插件链的层次调用
        long endTime=System.currentTimeMillis();

        //获取sql
        BoundSql boundSql=((StatementHandler)target).getBoundSql();
        String sql=boundSql.getSql();//这个还是?,不是具体值

        Object parameterObject = boundSql.getParameterObject(); //sql语句的parameterType
        List<ParameterMapping> parameterMappingList = boundSql.getParameterMappings();//每个#{}或${}的映射关系？？

        sql=sql.replaceAll("\n"," ").replaceAll("  *"," ");

        //处理sql中的占位符
        sql=setParameter(sql,parameterObject,parameterMappingList);

        String info=sql+",耗时："+(endTime-startTime)+"毫秒!";
        if((endTime-startTime)>=sqlTime){
            System.err.println("慢sql:"+info);
        }else
            System.err.println("执行sql:"+info);

        return result;
    }

    /**设置sql的占位符为具体值*/
    private String setParameter(String sql, Object parameterObject, List<ParameterMapping> parameterMappingList) {
        if(parameterObject==null || parameterMappingList==null||parameterMappingList.isEmpty()){//无参的情形
            return sql;
        }
        //TODO 还有foreach等场景
        // 通用场景，比如传的是一个自定义的对象或者八种基本数据类型之一或者String
        Class<?> clzaa=parameterObject.getClass();
        if(clzaa==Integer.class){ //TODO 实际上还有其他7种没有加
            return  sql.replaceAll("\\?",parameterObject.toString());
        }
        if(clzaa.isPrimitive()){//8种基本类型+void
            if(clzaa==Void.TYPE){
                return sql;
            }
            return  sql.replaceAll("\\?",parameterObject.toString());
        }
        if(parameterObject instanceof  String){
            return  sql.replaceAll("\\?","'"+parameterObject.toString()+"'");
        }
        for(ParameterMapping pm:parameterMappingList){
            String property=pm.getProperty();//#{xxx} 里面的xxx
            String value="?";
            try {
                Method method = parameterObject.getClass().getMethod("get" + property.substring(0, 1).toUpperCase()
                        + property.substring(1), new Class[]{});
                method.invoke(parameterObject);
            }catch (Exception e){
                e.printStackTrace();
            }
            sql.replaceFirst("\\?",value);
        }
         return sql;
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
