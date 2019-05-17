package com.littleboy.sqlLiteConnect;

import com.littleboy.model.Printer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PrinterDao {

    /**
     * 获取文件路径
     * @return
     */
    public String getFilePath() {
        String sql = "select fileDir from user_info where id = 1";

        if (SqlDBTools.checkDatabase()) {
            ResultSet rs = SqlDBTools.executeSqlGetResultSet(sql);
            try {
                if (rs.next()) {
                    return rs.getString("fileDir");
                }
            } catch (SQLException e) {
                System.err.println("数据库异常! "+ e.getMessage());
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取打印机名字
     * @return
     */
    public String getPrinterName() {
        String sql = "select printer from user_info where id = 1";

        if (SqlDBTools.checkDatabase()) {
            ResultSet rs = SqlDBTools.executeSqlGetResultSet(sql);
            try {
                if (rs.next()) {
                    return rs.getString("printer");
                }
            } catch (SQLException e) {
                System.err.println("数据库异常! "+ e.getMessage());
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取用户信息
     * @return
     */
    public String[] getUserInfo() {
        String sql = "select name, fileDir from user_info where id = 1";
        String[] results = new String[2];

        if (SqlDBTools.checkDatabase()) {
            ResultSet rs = SqlDBTools.executeSqlGetResultSet(sql);
            try {
                if (rs.next()) {
                    results[0] = rs.getString("name");
                    results[1] = rs.getString("fileDir");
                    return results;
                }
            } catch (SQLException e) {
                System.err.println("数据库异常! "+ e.getMessage());
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 插入文件信息
     * @param printer
     */
    public void insertFile(Printer printer) {
        String sql = "insert into print_file(md5, name, uploadTime, size) values('"
                + printer.getMd5() + "', '"
                + printer.getName() + "', '"
                + printer.getDate() + "', "
                + printer.getSize() + ")";

        if (SqlDBTools.checkDatabase()) {
            SqlDBTools.executeSql(sql);
        }
    }

    /**
     * 获取所有文件的md5
     * @return
     */
    public String[] getAllFileMD5() {
        String sql = "select md5 from print_file";
        String selectCount = "select count(md5) as count from print_file";
        if (SqlDBTools.checkDatabase()) {
            ResultSet rs = SqlDBTools.executeSqlGetResultSet(sql);
            ResultSet rs1 = SqlDBTools.executeSqlGetResultSet(selectCount);

            int count = 0;
            if (rs1 != null) {
                try {
                    if (rs1.next()) {
                        count = rs1.getInt("count");
                    }
                } catch (SQLException e) {
                    System.err.println("数据库异常! " + e.getMessage());
                    e.printStackTrace();
                }
            }
            try {
                if (rs != null) {
                    if (count > 0) {
                        String[] allMd5 = new String[count];
                        while (rs.next()) {
                            allMd5[rs.getRow() - 1] = rs.getString("md5");
                        }
                        return allMd5;
                    }
                }
            } catch (SQLException e) {
                System.err.println("数据库异常! " + e.getMessage());
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 设置用户信息
     * @param filePath 设置的打印文件存放路径
     * @param printer 打印机名称
     */
    public void setUserInfo(String filePath, String printer, String code) {
        String name = UUID.randomUUID().toString().substring(15);
        String sql = "insert into user_info(id, name, fileDir, printer, code) values(1, ' " + name + "', '" + filePath + "', '" + printer + "', '" + code + "')";

        if (SqlDBTools.checkDatabase()) {
            SqlDBTools.executeSql(sql);
        }
    }

    /**
     * 更新路径
     * @param filePath
     */
    public void updateUserInfo(String filePath, String printer) {
        String sql = "update user_info set fileDir='" + filePath + "', printer = '" + printer + "' where id = 1";

        if (SqlDBTools.checkDatabase()) {
            SqlDBTools.executeSql(sql);
        }
    }
}
