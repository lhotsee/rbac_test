package com.study.tool;

import java.sql.*;
public class Conn_db {
    /* 定义数据库连接四大常量 */
    // mysql驱动包名
    private static final String Drive_Name = "com.mysql.cj.jdbc.Driver";
    // 数据库连接地址
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/rbac_test?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC&useSSL=false\n";
    // 用户名
    private static final String USER_NAME = "root";
    // 密码
    private static final String PASSWORD = "926400";

    static Connection connection = null;

    public static Connection connect() throws ClassNotFoundException {
        try {
            // 加载mysql的驱动类
            Class.forName( Drive_Name );
            // 获取数据库连接
            connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
            // 调试输出
            System.out.println("连接成功");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Conn_db conn_db = new Conn_db();
        conn_db.connect();
        String sql = "SELECT user_name FROM user";
        PreparedStatement prst = conn_db.connection.prepareStatement(sql);
        ResultSet resultSet = prst.executeQuery();
        while ( resultSet.next() )
        {
            System.out.println("用户名：" + resultSet.getString("user_name"));
        }
        resultSet.close();
        prst.close();
    }
}
