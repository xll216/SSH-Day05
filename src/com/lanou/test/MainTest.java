package com.lanou.test;

import com.lanou.domain.User;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;

/**
 * Created by 蓝鸥科技有限公司  www.lanou3g.com.
 */
public class MainTest {
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

    @Test
    public void add() {
        /*创建一个新对象*/
        User user = new User(
                "李四", "123");

        /*保存一个实体类对象，是以插入insert的方式调用
        * 即要求该对象的主键id不能有值，否则会报错*/
//        session.save(user);

        /*
        * save方法插入时不考虑id，只将非主键的值执行insert
        *
        * saveOrUpdate方法先去检查当前对象的主键id是否
        *     存在，如果存在执行非主键的update更新操作；
        *     如果当前实体类对象的主键id不存在，则执行的
        *     是save操作，即insert插入.
        * */
//        user.setId(7);
        session.saveOrUpdate(user);
    }

    @Test
    public void delete() {
        User user = new User(
                "李四", "123");

        /*删除某个对象，如果传入的实体类对象没有设置主键id
        * 则不进行任何操作*/
        user.setId(5);//设置要删除的id
        session.delete(user);

        /*
        * session自带的delete方法只根据id进行删除，不
        * 考虑其他删除条件
        * */

    }

    @Test
    public void update() {
        User user = new User(
                "王五", "123");

        user.setId(2);//update必须要设置更新的id

        /*
        * update更新方法要求主键id有值，指明要更新的对象*/
        session.update(user);

    }

    @Test
    public void query() {
        /*
        * 1，根据主键id查询单个对象
        * 2，根据sql执行查询
        * */

        /*根据id查询单个对象
        * get：第一个参数给的是要查询的实体类类名，
        *      第二个参数是主键id*/
        User user = session.get(
                User.class, 1);
        System.out.println(user);

        /*以实体类名*/
        System.out.println("实体类名：" +
                User.class.getName());

        User user1 = (User) session.get(
                "com.lanou.domain.User",
                1);

        System.out.println(user1);

        /*根据sql语句查询*/
        /*创建一个query查询对象，createQuery中的参数
        * 是从from开始，不需要加入前面的select
        *
        * sql语句中涉及的类名和属性名都指的是实体类中的，不是
        * 数据库中的，hibernate内部会自动进行转换
        *
        * */
//        Query query = session.createQuery(
//                "from User where name=? and password=?");
//
//        /*设置sql语句中问号所对应的值*/
//        query.setString(0,"李四");//第一个问号的替换值
//        query.setString(1,"123");//第二个问号的替换值


        /*给条件语句中的条件设置别名，根据别名设置对应的参数*/
        Query query = session.createQuery(
                "from User where name=:a and password=:b");
        query.setString("a", "李四");
        query.setString("b", "123");

        List<User> users = query.list();//返回一个集合

        System.out.println(users);

    }

    @Test
    public void query2() {

        Query query = session.createQuery(
                "from User where name=?");

        /*设置参数 即sql语句中的问号对应的值
        *setString内部实际上还是调用的setParameter
        * 只不过是直接给定参数的类型；而setParameter会
        * 根据属性类型自动匹配
        * */
        query.setParameter(0, "李四");//推荐使用的策略

        /*返回一个迭代器，默认返回的只是符合条件的对象主键id，
        * 当进行结果遍历的时候需要进行二次查询（根据id）*/
        Iterator<User> iterator = query.iterate();

        /*遍历迭代器*/
        while (iterator.hasNext()) {
            User user = iterator.next();
            System.out.println(user);
        }


    }

    @Test
    public void query3() {
        Query query = session.createQuery(
                "from User ");

        /*分页处理*/
        int start = 3;//起始页 第1页 第2页。。。
        int pageSize = 2;//每页大小

        /*设置返回结果的最大条数 用它控制每页的数目*/
        query.setMaxResults(pageSize);

        /*设置返回结果集的偏移量*/
        query.setFirstResult((start - 1) * pageSize);

        /*符合条件的结果集*/
        List<User> users = query.list();
        System.out.println(users);


    }


}
