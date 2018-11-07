package com.study.gui;
/**
 * 功能：学生z作业管理系统
 */
import com.study.listener.Login_Listener;

import javax.swing.*;
import java.awt.*;

public class Login extends JFrame  {
    //定义组件
    JPanel jp1, jp2, jp3;//面板
    JLabel jlb1, jlb2;//标签

    JButton bt1, bt2;//按钮
    JTextField jtf;//用户名文本框
    JPasswordField jpf;//密码

    Login_Listener log;

    public static void main(String[] args) {
        Login ms = new Login();
    }

    //构造函数
    public Login() {
        //创建面板
        jp1 = new JPanel();
        jp2 = new JPanel();
        jp3 = new JPanel();
        //创建标签
        jlb1 = new JLabel("用户名");
        jlb2 = new JLabel("密  码");
        //创建按钮
        bt1 = new JButton("登录");
        bt2 = new JButton("重置");

        //创建文本框
        jtf = new JTextField(10);
        //创建密码框
        jpf = new JPasswordField(10);

        log = new Login_Listener();
        log.setUser_id(jtf);
        log.setUser_pwd(jpf);
        log.setOkbt(bt1);
        log.setExbt(bt2);
        log.setLogin(this);

        // 为button注册监听器
        bt1.addActionListener(log);
        bt2.addActionListener(log);

        //加入各个组件
        jp1.add(jlb1);
        jp1.add(jtf);

        jp2.add(jlb2);
        jp2.add(jpf);

        jp3.add(bt1);
        jp3.add(bt2);

        //设置布局管理
        this.setLayout(new GridLayout(3, 1));//网格式布局
        //加入到JFrame
        this.add(jp1);
        this.add(jp2);
        this.add(jp3);

        //设置窗体
        this.setTitle("学生作业系统");//窗体标签
        this.setSize(400, 300);//窗体大小
        this.setLocationRelativeTo(null);//在屏幕中间显示(居中显示)
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//退出关闭JFrame
        this.setVisible(true);//显示窗体
        //显示窗体
        this.setVisible(true);
        this.setResizable(true);
    }

}
