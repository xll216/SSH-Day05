package com.lanou.one2many;

import com.lanou.domain.Clazz;
import com.lanou.domain.Student;
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
public class MainTest {

    /*数据库连接的工厂对象*/
    protected SessionFactory sessionFactory;

    /*数据库连接对象 真正用于数据库的Crud操作的*/
    protected Session session;

    /*数据库事物对象*/
    protected Transaction transaction;

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
    public void save() {

        Student student = new Student(
                "王文", "123");

        Clazz clazz = new Clazz(
                "J1005", "Java班");

        //维护关系的一方学生 绑定班级
        student.setClazz(clazz);

        /*到这student和clazz均是临时状态*/

        //保存学生对象
        session.save(student);

    }

    @Test
    public void query() {
        //获取id为1的学生对象
        Student student = session.get(
                Student.class, 1);

        System.out.println(student);

        //获取学生所在的班级对象
        /*
        * 在级联关系中，默认情况是懒加载(按需加载)，即获
        * 取学生对象时不会直接调用外健表的查询，只有当需
        * 要时才会执行关联表的查询
        * */
        System.out.println(student.getClazz());

    }

    @Test
    public void test1() {
        /*1,保存班级级联保存学生*/
        Clazz clazz = new Clazz(
                "J1106", "Java班级");

        Student student = new Student(
                "朱海伦", "234");

        //学生绑定班级
        student.setClazz(clazz);

        /*在单向的一对多的关系中，如果在n的一方维护关系
        * 则1的一方没办法完成级联的保存*/
        session.save(clazz);//保存班级

        //需要多调用一次保存 对学生对象的保存
        session.save(student);

    }
}
