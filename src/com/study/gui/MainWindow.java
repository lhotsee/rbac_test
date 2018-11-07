package com.study.gui;

import com.study.tool.Conn_db;
import com.sun.deploy.util.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

// gitTest

public class MainWindow extends JFrame {
    String user_id;    // 当前窗口的用户ID
    int role_id;       // 用户角色ID
    ArrayList<Integer> permit = new ArrayList<>();  // 用户所有权限ID集合
    Font textFont = new Font("微软雅黑",Font.PLAIN, 14);

    JPanel JPfunction;    // 下方功能性面板
    JPanel JPmain;        // 中间主要显示区
    JPanel JPtop;         // 顶部切换对象区
    JComboBox<String> JCtasks = new JComboBox<>();    // 作业下拉列表框
    JComboBox<String> JCstudents = new JComboBox<>(); // 学生下拉列表框
    JLabel JLrole;      // 当前角色
    JTextField JTFrole;
    JTextArea JTdetails;  // 作业评语，成绩等展示区域
    JButton JBsubmit;     // 提交作业按钮
    JButton JBthisHomework; // 查看作业情况按钮
    JTextField JTFcomment;  // 输入作业评语文本框
    JButton JBcomment;  //修改评按钮
    JTextField JTFgrade;    // 输入成绩文本框；
    JButton JBgrades;   //修改成绩按钮
    JButton JBcollect;  //统计所有成绩

    /* 构造函数 */
    public MainWindow( String user_id ) throws SQLException, ClassNotFoundException {
        this.user_id = user_id;
        // 初始化JPanel
        JPtop = new JPanel();
        JPmain = new JPanel();
        JPfunction = new JPanel();
        // 顶部两个下拉列表框和切换按钮
        JCtasks.addItem("---选择作业---");
        JCtasks.setFont(textFont);
        JCstudents.addItem("---选择学生---");
        JCstudents.setFont(textFont);
        JCstudents.addItemListener(new JCstuHandler());

        // 显示角色信息
        JLrole = new JLabel("       您当前角色为：");
        JLrole.setFont(new Font("微软雅黑",Font.BOLD,14));
        JTFrole = new JTextField(5);

        /* 填充作业、学生列表； 显示角色信息 */
        complete(user_id);

        JTdetails = new JTextArea(20,45);
        JTdetails.setFont(new Font("微软雅黑", Font.BOLD,14));
        JTdetails.setLineWrap(true);
        // 提交评语
        JTFcomment = new JTextField(20);
        JTFcomment.setFont(textFont);
        JBcomment = new JButton("提交评语");
        JBcomment.addActionListener(new JBcommentHandler());

        // 提交成绩
        JTFgrade = new JTextField(5);
        JBgrades = new JButton("提交成绩");
        JBgrades.addActionListener(new JBgradeHandler());
        //  提交作业
        JBsubmit = new JButton("提交作业");
        JBsubmit.addActionListener(new JBsubmitHandler());
        // 查看作业
        JBthisHomework = new JButton("查看作业");
        JBthisHomework.addActionListener(new JBthisHomeworkHandler());
        // 统计成绩
        JBcollect = new JButton("统计成绩");
        JBcollect.addActionListener(new JBcollect());
        /* 加入各个组件 */
        JPtop.setLayout(new FlowLayout());
        JPtop.add(JCtasks);
        JPtop.add(JCstudents);
        JPtop.add(JLrole);
        JPtop.add(JTFrole);
        JScrollPane jScrollPane = new JScrollPane(JTdetails);
        jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        JPmain.add(jScrollPane);

        JPfunction.setLayout(new GridLayout(2,1));
        JPanel modify = new JPanel();
        modify.add(JTFcomment);
        modify.add(JBcomment);
        modify.add(JTFgrade);
        modify.add(JBgrades);

        JPanel buttons = new JPanel();
        buttons.add(JBsubmit);
        buttons.add(JBthisHomework);
        buttons.add(JBcollect);

        JPfunction.add(modify);
        JPfunction.add(buttons);

        {
            JTdetails.setText("你好哇");
            JTdetails.append("你好哈！");
            permit.forEach(System.out::println);
        }

        // 设置Frame布局管理器
        this.setLayout(new BorderLayout());
        // 加入到Frame
        this.add(BorderLayout.NORTH, JPtop);
        this.add(BorderLayout.CENTER,JPmain);
        this.add(BorderLayout.SOUTH,JPfunction);

        //设置窗体
        this.setTitle("学生作业系统");//窗体标签
        this.setSize(600, 600);//窗体大小
        this.setLocationRelativeTo(null);//在屏幕中间显示(居中显示)
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//退出关闭JFrame
        this.setVisible(true);//显示窗体
        //显示窗体
        this.setVisible(true);
        this.setResizable(true);
    }
    /* 填充作业、学生列表； 显示角色信息  获取当前用户权限 */
    private void complete( String user_id ) throws ClassNotFoundException, SQLException {
        Connection connection = Conn_db.connect();  // 连接数据库，根据user_id 查询相关信息
        /* 获取目前用户的role_id */
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT user_role_id FROM user WHERE user_id = ? ");
        preparedStatement.setString(1, user_id);    // 给？赋值，防止SQL注入

        ResultSet resultSet = preparedStatement.executeQuery();     // ResultSet结果集
        while(resultSet.next())
        {
            role_id = resultSet.getInt("user_role_id");
        }
        // 取得了role_id
        switch (role_id){
            case 0: JTFrole.setText("  学生"); break;
            case 1: JTFrole.setText("  教师"); break;
            case 2: JTFrole.setText("  教务员");
        }

        /* 获取目前角色所有权限ID */
        preparedStatement = connection.prepareStatement("SELECT permit_id FROM role_permit WHERE role_id = ? ");
        // ?占位符，防止SQL注入
        String roleID = role_id + "";
        preparedStatement.setString(1, roleID );
        resultSet = preparedStatement.executeQuery();
        while( resultSet.next() )
        {
            permit.add(resultSet.getInt("permit_id"));
        }

        StringBuffer task = new StringBuffer();
        String str = null;
        preparedStatement = connection.prepareStatement("SELECT * FROM homework_todo");
        resultSet = preparedStatement.executeQuery();
        while(resultSet.next())
        {
            task.append(resultSet.getString("homework_subject"));
            task.append( "*" );   //在每条数据后面做标记，便于拆分
        }
        str = task.toString();
        String[] tasks = StringUtils.splitString(str,"*");
        for( int i = 0; i < tasks.length; i++ )
        {
            JCtasks.addItem(tasks[i]);
        }

        /* 从数据库填充学生表 */
        StringBuffer stu = new StringBuffer();
        String string;
        preparedStatement = connection.prepareStatement(" SELECT * FROM user WHERE user_role_id = 0 ");
        resultSet = preparedStatement.executeQuery();
        while(resultSet.next())
        {
            String student = resultSet.getString("user_name");
            System.out.println(student);
            stu.append( student );
            stu.append( "*" );   //在每条数据后面做标记，便于拆分
        }
        string = stu.toString();
        String[] stus = StringUtils.splitString( string,"*");
        for( int i = 0; i < stus.length; i++ )
        {
            JCstudents.addItem(stus[i]);
        }

        // 关闭数据库连接
        resultSet.close();
        preparedStatement.close();
        connection.close();

    }

    /*
     *监听查看作业按钮
     * 1. 学生查看，
     * 2. 教师和教务员查看，必须选择学生
     */
    private class JBthisHomeworkHandler implements ActionListener{
        String[] taskDetail = {"","","","","",};    //  五项作业信息
        @Override
        public void actionPerformed(ActionEvent e) {
            if( permit.contains(10))    // 含有切换学生的权限，必为教师或教务员
            {
                // 必须选择特定学生
                if( JCstudents.getSelectedIndex() == 0 )
                {
                    JOptionPane.showMessageDialog(null,"请选择指定学生" );
                }
                else    // 已选择指定学生
                {
                    String homeworkSubject = (String) JCtasks.getSelectedItem();    // 作业框所选定的作业
                    String stuName = (String) JCstudents.getSelectedItem();     // 学生框所选定的学生
                    String stuID = null;
                    try {
                        Connection connection = Conn_db.connect();
                        // 查询所选择学生的用户ID
                        PreparedStatement preparedStatement = connection.prepareStatement("SELECT user_id FROM user WHERE user_name = ? ");
                        preparedStatement.setString(1,stuName);
                        ResultSet resultSet = preparedStatement.executeQuery();
                        while(resultSet.next())
                        {
                            stuID = ""+resultSet.getInt(1);   // 获取到了待查询学生的用户ID
                        }
                        // 根据学生ID和选择的作业，查询该名学生指定作业信息
                        preparedStatement = connection.prepareStatement
                                ("SELECT homework_done_detail, homework_comment, homework_grade, homework_done_time " +
                                        "FROM homework_done WHERE homework_stu = ? AND homework_id in " +
                                        " (SELECT homework_id FROM homework_todo WHERE homework_subject = ? )" );
                        // 占位符赋值
                        preparedStatement.setString(1,""+stuID);
                        preparedStatement.setString(2,""+homeworkSubject);
                        ResultSet rs = preparedStatement.executeQuery();
                        collectHomeworkDetail(homeworkSubject, rs);
                        // 关闭数据库
                        rs.close();
                        preparedStatement.close();
                        connection.close();
                    } catch (ClassNotFoundException | SQLException e1) {
                        e1.printStackTrace();
                    }
                    // 修改外部类中TextArea中的内容,TextArea处展示当前用户选中的作业详情
                    JTdetails.setText("");  // 清空欢迎信息
                    JTdetails.append("作业详情：\n");
                    JTdetails.append("------------------------------------");
                    JTdetails.append("------------------------------------\n");
                    for( int i = 0; i < taskDetail.length; i++ )
                    {
                        JTdetails.append("\n" + taskDetail[i] + "\n\n");
                        JTdetails.append("------------------------------------");
                        JTdetails.append("------------------------------------\n");
                    }
                }
            }
            else     //  当前用户无权限10，即为学生
            {
                String homeworkSubject = (String) JCtasks.getSelectedItem();
                try {
                    // 连接数据库
                    Connection connection = Conn_db.connect();
                    PreparedStatement preparedStatement = connection.prepareStatement
                            ("SELECT homework_done_detail, homework_comment, homework_grade, homework_done_time " +
                                    "FROM homework_done WHERE homework_stu = ? AND homework_id in " +
                                    " (SELECT homework_id FROM homework_todo WHERE homework_subject = ? )" );
                    // 占位符赋值
                    preparedStatement.setString(1,""+user_id);
                    preparedStatement.setString(2,""+homeworkSubject);
                    ResultSet rs = preparedStatement.executeQuery();
                    collectHomeworkDetail(homeworkSubject, rs);
                    // 关闭数据库
                    rs.close();
                    preparedStatement.close();
                    connection.close();
                } catch (ClassNotFoundException | SQLException e1) {
                    e1.printStackTrace();
                }

                // 修改外部类中TextArea中的内容,TextArea处展示当前用户选中的作业详情
                JTdetails.setText("");  // 清空欢迎信息
                JTdetails.append("作业详情：\n");
                JTdetails.append("------------------------------------");
                JTdetails.append("------------------------------------\n");
                for( int i = 0; i < taskDetail.length; i++ )
                {
                    JTdetails.append("\n" + taskDetail[i] + "\n\n");
                    JTdetails.append("------------------------------------");
                    JTdetails.append("------------------------------------\n");
                }

            }
        }
        private void collectHomeworkDetail(String homeworkSubject, ResultSet rs) throws SQLException {
            if ( rs.next() )
            {
                taskDetail[0] = "作业名： " + homeworkSubject;       // 作业名
                taskDetail[1] = "提交内容：  " + rs.getString(1);    // 作业解答内容
                taskDetail[2] = "作业评语：  " + rs.getString(2);    // 作业评语
                taskDetail[3] = "作业分数：  " + rs.getInt(3);    // 作业分数
                taskDetail[4] = "提交时间：  " + rs.getString(4);    // 作业提交时间
            }
            else {
                taskDetail[0] = "作业名： " + homeworkSubject;       // 作业名
                taskDetail[1] = "提交内容：  ";    // 作业解答内容
                taskDetail[2] = "作业评语：  ";    // 作业评语
                taskDetail[3] = "作业分数：  ";    // 作业分数
                taskDetail[4] = "提交时间：  ";    // 作业提交时间

            }
        }
    }

    /*
    监听切换学生的事件
     */
    private class JCstuHandler implements ItemListener{

        @Override
        public void itemStateChanged(ItemEvent e) {
            if( !permit.contains(10) && JCstudents.getSelectedIndex() != 0 )
            {
                JCstudents.setSelectedIndex(0);
                JOptionPane.showMessageDialog(null, "抱歉，您无权限查看其他学生作业");
            }
        }
    }

    /*
    监听提交作业按钮
     */
    private class JBsubmitHandler implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            if( permit.contains(6) )
            {
                JOptionPane.showMessageDialog(null,"作业文件上传功能开发中请稍后----");
            }
            else    // 当前用户为教师或教务员，不需要提交作业
            {
                JOptionPane.showMessageDialog(null,"您不需提交作业");
                JBsubmit.setEnabled( false );
            }
        }
    }

    /*
    监听提交评语按钮
     */
    private class JBcommentHandler implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            if( permit.contains(7) )  // 当前用户具有提交评语的权限
            {
                // 是否选中指定作业和学生
                if( JCstudents.getSelectedIndex() == 0 )
                {
                    JOptionPane.showMessageDialog(null,"请选择一名学生");
                }
                else if( JCstudents.getSelectedIndex() == 0 )
                {
                    JOptionPane.showMessageDialog(null,"请选定作业");
                }
                else
                {
                    // 将评语TextField内容写入数据库
                    String comment = JTFcomment.getText();
                    String stuName = (String) JCstudents.getSelectedItem();
                    String homeworkSubject = (String) JCtasks.getSelectedItem();
                    // 连接数据库
                    Connection connection = null;
                    try {
                        connection = Conn_db.connect();
                        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE homework_done SET homework_comment = ? " +
                                "WHERE homework_stu = (SELECT user_id FROM user WHERE user_name = ?) " +
                                "AND homework_id = (SELECT homework_id FROM homework_todo WHERE homework_subject = ? )");
                        preparedStatement.setString(1,comment);
                        preparedStatement.setString(2,stuName);
                        preparedStatement.setString(3,homeworkSubject);
                        int result = preparedStatement.executeUpdate();
                        if(result == 1)
                        {
                            JOptionPane.showMessageDialog(null,"作业评语提交成功");
                        }
                        else{
                            JOptionPane.showMessageDialog(null,"作业评语提交失败，请联系开发人员");
                        }
                    } catch (ClassNotFoundException | SQLException e1) {
                        e1.printStackTrace();
                    }

                }

            }
            else {      // 无提交评语权限
                JOptionPane.showMessageDialog(null,"抱歉，您无修改作业评语权限，请联系管理员！");
            }
        }
    }

    /*
    监听提交成绩按钮，可以和评语监听器合并吗？
     */
    private class JBgradeHandler implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            if( permit.contains(8) )    // 拥有提交作业成绩的权限
            {
                // 是否选中指定作业和学生
                if( JCstudents.getSelectedIndex() == 0 )
                {
                    JOptionPane.showMessageDialog(null,"请选择一名学生");
                }
                else if( JCstudents.getSelectedIndex() == 0 )
                {
                    JOptionPane.showMessageDialog(null,"请选定作业");
                }
                else
                {
                    // 将成绩TextField内容写入数据库
                    int grade = Integer.parseInt(JTFgrade.getText());
                    String stuName = (String) JCstudents.getSelectedItem();
                    String homeworkSubject = (String) JCtasks.getSelectedItem();
                    // 连接数据库
                    Connection connection;
                    try {
                        connection = Conn_db.connect();
                        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE homework_done SET homework_grade = ? " +
                                "WHERE homework_stu = (SELECT user_id FROM user WHERE user_name = ?) " +
                                "AND homework_id = (SELECT homework_id FROM homework_todo WHERE homework_subject = ? )");
                        preparedStatement.setString(1, String.valueOf(grade));
                        preparedStatement.setString(2,stuName);
                        preparedStatement.setString(3,homeworkSubject);
                        int result = preparedStatement.executeUpdate();
                        if(result == 1)
                        {
                            JOptionPane.showMessageDialog(null,"作业成绩提交成功");
                        }
                        else{
                            JOptionPane.showMessageDialog(null,"作业成绩提交失败，请联系开发人员");
                        }
                        // 关闭数据库连接
                        preparedStatement.close();
                        connection.close();
                    } catch (ClassNotFoundException | SQLException e1) {
                        e1.printStackTrace();
                    }

                }
            }
            else {      // 无提交评语权限
                JOptionPane.showMessageDialog(null,"抱歉，您无修改作业成绩权限，请联系管理员！");
            }
        }
    }

    /*
    监听统计成绩按钮
     */
    private class JBcollect implements ActionListener{
        ArrayList<Integer> grades;
        @Override
        public void actionPerformed(ActionEvent e) {
            grades = new ArrayList<>();
            if( permit.contains(9) )    // 具有统计作业成绩的权限
            {
                if( JCtasks.getSelectedIndex() == 0 )   // 未选择作业
                {
                    JOptionPane.showMessageDialog(null,"请选择待统计成绩的作业");
                }
                else    // 已选择特定作业
                {
                    String homeworkSubject = (String) JCtasks.getSelectedItem();
                    // 连接数据库，打印该作业所有学生成绩
                    try {
                        Connection connection = Conn_db.connect();
                        PreparedStatement preparedStatement =  connection.prepareStatement("SELECT homework_grade FROM homework_done WHERE homework_id = " +
                                "(SELECT homework_id FROM homework_todo WHERE homework_subject = ? )");
                        preparedStatement.setString(1,homeworkSubject);
                        ResultSet resultSet = preparedStatement.executeQuery();
                        while (resultSet.next())
                        {
                            grades.add(resultSet.getInt("homework_grade"));
                        }
                        // 关闭数据库连接
                        resultSet.close();
                        preparedStatement.close();
                        connection.close();
                    }
                    catch ( SQLException | ClassNotFoundException e1 ){
                        e1.printStackTrace();
                    }

                    // 清空显示区并将选定作业的所有成绩打印输出
                    JTdetails.setText(homeworkSubject+"的成绩如下：\n");
                    JTdetails.append("----------------------------------------");
                    JTdetails.append("----------------------------------------\n");
                    for( int i = 0; i < grades.size(); i++ )
                    {
                        JTdetails.append("\t" + String.valueOf(grades.get(i))+"\n");
                        JTdetails.append("----------------------------------------");
                        JTdetails.append("----------------------------------------\n");
                    }
                }
            }
            else    // 无统计作业的权限
            {
                JOptionPane.showMessageDialog(null,"抱歉，您无统计成绩权限，请联系管理员");
            }
        }
    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        MainWindow mainWindow = new MainWindow( "" );

    }
}