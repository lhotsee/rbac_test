package com.study.listener;

import com.study.gui.MainWindow;
import com.study.tool.Conn_db;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login_Listener implements ActionListener {
    JFrame login;
    String user_id;
    String user_pwd;

    public void setLogin(JFrame login) {
        this.login = login;
    }

    JTextField JTFuser_id, JTFuser_pwd;
    JButton JBlogin, JBclear;
    ResultSet rs;

    public void setUser_pwd(JTextField JTFuser_pwd) {
        this.JTFuser_pwd = JTFuser_pwd;
    }

    public void setOkbt(JButton JBlogIn) {
        this.JBlogin = JBlogIn;
    }

    public void setExbt(JButton JBclear) {
        this.JBclear = JBclear;
    }

    public void setUser_id(JTextField JTFuser_id) {
        this.JTFuser_id = JTFuser_id;
    }

    @Override
    public void actionPerformed( ActionEvent e ){
        if(e.getSource() == JBlogin )  // 点击了登录button
        {
            if( JTFuser_id.getText().equals(""))
            {
                JOptionPane.showMessageDialog(null, "请输入用户名");
            }
            else if( JTFuser_pwd.getText().equals(""))
            {
                JOptionPane.showMessageDialog(null, "请输入密码");
            }
            else{
                String id = JTFuser_id.getText();
                String pwd = JTFuser_pwd.getText();
                /* 连接数据库，登录校验 */
                try {
                    // 加载Conn_db类。执行connection方法
                    boolean authority = checkInDB(id, pwd);
                    if(authority)
                    {
                        // 登录成功，跳转页面
                        login.dispose();
                        MainWindow mainWindow = new MainWindow(this.user_id);
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(null,"用户ID或密码不正确");
                        // 输入框清零
                        clear();
                    }
                } catch (ClassNotFoundException | SQLException e1) {
                    e1.printStackTrace();
                }
            }
        }
        else if( e.getSource() == JBclear)    // 触发重置按钮
        {
            clear();
        }
    }

    /* 清空用户名文本框和密码框 */
    private void clear(){
        JTFuser_id.setText("");
        JTFuser_pwd.setText("");
    }
    /* 登录校验 */
    private boolean checkInDB(String id, String pwd) throws SQLException, ClassNotFoundException {
        boolean authority = false;
        String sql = "SELECT * FROM user";
        Connection connection = Conn_db.connect();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        rs = preparedStatement.executeQuery();
        while( rs.next() )
        {
            user_id = rs.getString("user_id");
            user_pwd = rs.getString("user_pwd");
            if( id.equals(user_id) && pwd.equals(user_pwd) )
            {
                authority = true;
                break;
            }
        }
        rs.close();
        preparedStatement.close();
        connection.close();
        return  authority;
    }
}
