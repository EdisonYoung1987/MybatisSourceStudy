package com.edison.factory;

import com.edison.entity.Blog;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;

import java.util.List;

/**自定义的一个对象创建工厂，mybatis查询结果要创建为对象时，
 * 通过自定义方式，可以实现在创建某个类的对象时，增加自己的操作*/
public class MyObjectFactory extends DefaultObjectFactory {
    @Override
    public <T> T create(Class<T> type, List<Class<?>> constructorArgTypes, List<Object> constructorArgs) {
        T obj= super.create(type, constructorArgTypes, constructorArgs);
        if(type.isAssignableFrom(Blog.class)){
//            System.out.println("Blog(xxx...)被创建:"+((Blog)obj).getBid());
            ((Blog)obj).setAuthorId(9999); //会被覆盖
            //如果查询结果为null并且没有设置对该字段的typeHandler，则会生效
            ((Blog)obj).setName("Object Factory处理过的名字");
        }
        return obj;

    }
}
