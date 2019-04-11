package com.littleboy.sqlLiteConnect;

import com.littleboy.tools.FolderScanner;

import java.io.File;
import java.io.IOException;
import java.sql.*;

public class SqlDBTools {
    private static Connection mConnection = null;
    /**
     * 创建数据库连接
     * @return
     */
    public static Connection connect() {
        try {
            // 检测数据库是否连接
            if (mConnection == null || mConnection.isClosed()) {
                // 数据库文件路径
                String url = "jdbc:sqlite:" + System.getProperty("user.dir") + "/printer.db";
                Class.forName("org.sqlite.JDBC");
                // 通过数据库文件来连接数据库
                mConnection = DriverManager.getConnection(url);
            }
        } catch (ClassNotFoundException | SQLException e) {
            // 如果没有数据库文件则创建
            File file = new File(FolderScanner.getDbFolder());
            try {
                boolean createFile = file.createNewFile();

                if (createFile) {
                    SqlDBTools.connect();
//                    System.err.println("连接数据库失败! ");
                }
            } catch (IOException ioe) {
                System.err.println("创建数据库文件失败! ");
            }
        }
        return mConnection;
    }

    /**
     * 检查数据库中需要的表是否存在
     */
    public static boolean checkDatabase() {
        StringBuffer sb = new StringBuffer();
        // 检测是否有print_file这个表
        sb.append("select count(*) as haveTable from sqlite_master where type='table' and name = 'print_file'");

        ResultSet rs = executeSqlGetResultSet(sb.toString());

        sb = new StringBuffer();
        // 检测是否有user_info这个表
        sb.append("select count(*) as haveTable from sqlite_master where type='table' and name = 'user_info'");
        try {
            if (rs != null) {
                while (rs.next()) {
                    int haveTable = rs.getInt("haveTable");
                    if (haveTable <= 0) {
                        // 如果没有print_file表, 则会创建该表
                        createFileTable();
                        return false;
                    }
                }
            }
            rs = executeSqlGetResultSet(sb.toString());

            if (rs != null) {
                while (rs.next()) {
                    int haveTable = rs.getInt("haveTable");
                    if (haveTable <= 0) {
                        // 如果没有user_info表, 则会创建该表
                        createUserInfo();
                        return false;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("创建数据库失败! ");
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 创建文件表
     */
    private static void createFileTable() {
        StringBuffer sb = new StringBuffer();
        sb.append("CREATE TABLE print_file (");
        sb.append("md5 TEXT, name TEXT, uploadTime datetime, size INTEGER)");

        executeSql(sb.toString());
    }

    /**
     * 创建用户信息表
     */
    private static void createUserInfo() {
        StringBuffer sb = new StringBuffer();
        sb.append("CREATE TABLE user_info (");
        sb.append("id INTEGER, name TEXT, fileDir TEXT, printer TEXT)");

        executeSql(sb.toString());
    }

    /**
     * 执行sql并返回ResultSet
     * @param sql
     * @return
     */
    public static ResultSet executeSqlGetResultSet(String sql) {
        try {
            connect();
            Statement stmt = mConnection.createStatement();
            return stmt.executeQuery(sql);

        } catch (SQLException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 执行sql
     * @param sql
     * @return
     */
    public static void executeSql(String sql) {
        try {
            connect();
            Statement stmt = mConnection.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
