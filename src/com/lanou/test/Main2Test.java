package com.lanou.test;

import com.lanou.domain.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by 蓝鸥科技有限公司  www.lanou3g.com.
 */
public class Main2Test {

    /*数据库连接的工厂对象*/
    private SessionFactory sessionFactory;

    /*数据库连接对象 真正用于数据库的Crud操作的*/
    private Session session;

    /*数据库事物对象*/
    private Transaction transaction;

    @Before
    public void init() {
        /*1.构建配置对象*/
        Configuration config = new Configuration();

        /*默认加载src下的配置文件Hibernate.cfg.xml*/
        config.configure();

        /*2.初始化工厂对象*/
        sessionFactory = config.buildSessionFactory();

        /*3.得到一个连接对象*/
        session = sessionFactory.openSession();

        /*4.开启一个事物对象*/
        transaction = session.beginTransaction();
    }

    @After
    public void destroy() {
        /*关闭数据库连接*/

        /*6.提交事物*/
        transaction.commit();
        /*7.关闭连接*/
        session.close();
    }


    /**
     * 临时状态
     * new--->>临时状态--->save/saveOrUpdate-->持久化状态
     **/
    @Test
    public void save() {

        User user = new User(
                "王铭", "123");//临时状态
        System.out.println(user);

        session.save(user);//持久化状态

        System.out.println(user);

        //重新设置持久化对象的某个属性
        /*
        * 对于持久化对象来说，当更改对象的某个属性值时，
        * 不用手动调用update方法进行更新，系统在commit提交
        * 时会自动执行update更新的操作
        * */
        user.setName("宋士文");

        System.out.println(user);

    }

    /**
     * get/load/find/iterator--->持久化对象--->
     * delete--->临时对象
     **/
    @Test
    public void delete() {
        User user = session.get(
                User.class, 1);//持久化对象
        System.out.println(user);

        //删除某个对象
        session.delete(user);//user变成临时状态

        System.out.println(user);
        user.setName("王文");//临时状态对象修改不影响数据库

    }

    /**
     * get/find/save...--->持久化状态--->
     * clear/evict/close--->游离状态
     **/
    @Test
    public void clear() {
        User user = session.get(
                User.class, 3);//持久化状态


        session.clear();//清除方法

        //user变成游离状态
        System.out.println(user);

        /*处理游离状态的对象，对其属性的修改不会提交到数据库中
        * 临时状态与游离状态的区别在于数据库还有没有备份
        * a，从持久化对象通过delete到临时状态，数据库没
        *    有此对象了，是真正的删除
        * b，从持久化对象通过clear、evict、close到游离
        *    状态，数据库还有该对象
        * */
        user.setName("王文");

        //从游离状态变成持久化状态
        session.update(user);

        /*对持久化对象的更新，即在事务commit时更新对象*/
        user.setPassword("324234");


    }
}
