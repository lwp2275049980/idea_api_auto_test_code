package com.util;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class JDBCUtils {
    /**  连接mysql数据库
     * @return  Connect 连接对象
     */
    public static Connection getConnection() {
              //定义数据库连接
            //Oracle：jdbc:oracle:thin:@localhost:1521:DBName
            //SqlServer：jdbc:microsoft:sqlserver://localhost:1433; DatabaseName=DBName
            //MySql：jdbc:mysql://localhost:3306/DBName(数据库名)
                      // jdbc协议对应的访问地址     jdbc:mysql:// 这一段是固定的  端口号如果不是默认的，就指定端口号  后面是字符编码
            String url="jdbc:mysql://mall.lemonban.com/yami_shops?useUnicode=true&characterEncoding=utf-8";
            String user="lemon";
            String password="lemon123";
            //定义数据库连接对象
            Connection conn = null;
            try {
                conn = DriverManager.getConnection(url, user,password);
            }catch (Exception e) {
                e.printStackTrace();
            }
            // 返回连接对象
            return conn;
    }

    public static void main(String[] args) throws SQLException {
        // 0. 创建数据库连接
        Connection connection = getConnection();
        // 1.生成QueryRunner对象
        QueryRunner queryRunner = new QueryRunner();
        // 2. 调用query方法来实现查询操作
        /* 2-1 多条数据集  使用: new MapListHandler()
        //   query(连接对象，查询语句，实现类)       因为SELECT * from tz_sms_log查询得到的是多条数据集，所以用MapListHandler<>
        List<Map<String,Object>> datas = queryRunner.query(connection,"SELECT * FROM tz_sms_log;", new MapListHandler());
        System.out.println(datas);    // 输出获取到的整张表的数据
        System.out.println(datas.get(0));   // 输出获取到的第一条数据
        System.out.println(datas.get(0).get("mobile_code"));   // 输出获取到的第一条数据中的”mobile_code“字段的字段值
         */

        /* 2-2 一条结果集  使用: new MapHandler()
        Map<String ,Object> datas = queryRunner.query(connection,"SELECT * FROM tz_sms_log where id = 8;",new MapHandler());
        System.out.println(datas);       // 输出获取到的第一条数据
         */

        /* 2-3 单个数据  使用：new ScalarHandler<>()    具体看被查询语句的返回值类型定义变量接收
        String code = queryRunner.query(connection,"SELECT mobile_code from tz_sms_log WHERE id =(SELECT MAX(id) FROM tz_sms_log);",new ScalarHandler<>());
        System.out.println(code);
         */

        //System.out.println(querySingleData("SELECT mobile_code from tz_sms_log WHERE id =(SELECT MAX(id) FROM tz_sms_log);"));    // 单个数据  仅返回值
        //System.out.println(queryOneData("SELECT mobile_code from tz_sms_log WHERE id =(SELECT MAX(id) FROM tz_sms_log);"));   // Map结构，所以返回{mobile_code=912482}  返回键与值
    }


    /**
     * 查询单个数据
     * @param sql  被执行的sql语句
     * @return  返回sql语句查询到的单个数据值
     */
    public static Object querySingleData(String sql){
        // 参数化替换
        sql = Environment.replaceParams(sql);
        Connection connection = getConnection();
        QueryRunner queryRunner = new QueryRunner();
        Object data = null;
        try {
            data = queryRunner.query(connection,sql,new ScalarHandler<>());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // 这里返回的是单个数据
        return data;
    }

    /**
     * 查询结果中的一条数据
     * @param sql
     * @return
     */
    public static  Map<String,Object> queryOneData(String sql){
        sql = Environment.replaceParams(sql);
        Connection connection = getConnection();
        QueryRunner queryRunner = new QueryRunner();
        Map<String,Object> data = null;
        try {
            data = queryRunner.query(connection,sql,new MapHandler());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // 这里返回的是一条数据
        return data;
    }
}
