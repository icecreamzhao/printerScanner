package com.littleboy.tools;

import com.littleboy.model.Printer;
import com.littleboy.sqlLiteConnect.PrinterDao;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FolderScanner {
    // 要扫描的文件夹
    private static String folderPath;
    // 数据库所在路径
    private static String dbFolder = System.getProperty("user.dir") + "/printer.db";

    public static PrinterDao sPrinterDao = new PrinterDao();

    /**
     * 扫描数据库中设置好的路径
     */
    public void scannerFile() {
        // 获取数据库中设置好的路径, 如果没有设置会返回null
        folderPath = sPrinterDao.getFilePath();
        // 如果设置了路径
        if (folderPath != null && !folderPath.equals("")) {
            // 获取到该路径的文件实例
            File file = new File(folderPath);
            if (file.exists()) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        // 获取数据库中设置的路径下的所有符合条件的文件
                        List<File> files = getFile(file);
                        for (File file : files) {
                            try {
                                // 获取数据库中设置的打印机的名称
                                String printerName = sPrinterDao.getPrinterName();
                                // 获取word转换pdf的路径
                                String path;
                                if (file.getName().lastIndexOf(".pdf") <= 0) {
                                    path = WordToPdf.wordToPDF(file.getAbsolutePath());
                                } else {
                                    path = file.getAbsolutePath();
                                }
                                System.out.println("path: " + path);

                                // 打印pdf文件
                                if (path != null) {
                                    File printFile = new File(path);

                                    PrintPdf.PDFprint(printFile, printerName);
                                    // 将打印的文件存到数据库中
                                    Printer printer = new Printer();
                                    printer.setDate(new Date());
                                    printer.setMd5(getMD5(file));
                                    printer.setName(file.getName());
                                    printer.setSize(file.length());

                                    sPrinterDao.insertFile(printer);
                                    file.delete();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
                ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
                // 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
                service.scheduleAtFixedRate(runnable, 1, 1, TimeUnit.SECONDS);
            }
        }
    }

    /**
     * 获取所有符合条件的文件
     * @param file 用户设置好的路径
     * @return 所有符合条件的文件集合
     */
    private List<File> getFile(File file) {
        List<File> files = new ArrayList<>();
        if (file.exists()) {
            if (file.isDirectory()) {
                // 获取该路径下所有文件
                files = getFiles(file);
            }
        }
        return files;
    }

    private String getMD5(File file) {
        BigInteger bi = null;
        try {
            byte[] buffer = new byte[8192];
            int len = 0;
            MessageDigest md = MessageDigest.getInstance("MD5");
            FileInputStream fis = new FileInputStream(file);
            while ((len = fis.read(buffer)) != -1) {
                md.update(buffer, 0, len);
            }
            fis.close();
            byte[] b = md.digest();
            bi = new BigInteger(1, b);
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
        return bi.toString(16);
    }

    /**
     * 根据路径获取到该路径下所有文件
     * @param file
     * @return
     */
    public List<File> getFiles(File file) {
        List<File> allfilelist = new ArrayList<>();
        return getAllFile(file, allfilelist);
    }

    /**
     * 获取到当前文件夹下的所有文件
     * @param file
     * @param allfilelist
     * @return
     */
    private List<File> getAllFile(File file, List<File> allfilelist) {
        if (file.exists()) {
            //判断文件是否是文件夹，如果是，开始递归
            if (file.isDirectory()) {
                // 将不相关的文件过滤掉
                File f[] = file.listFiles(new MyFileFilter());
                for (File file2 : f) {
                    getAllFile(file2, allfilelist);
                }
            } else {
                if (file.isFile()) {
                    allfilelist.add(file);
                }
            }
        }
        return allfilelist;
    }

        public static String getFolderPath() {
        return folderPath;
    }

    public static void setFolderPath(String folderPath) {
        FolderScanner.folderPath = folderPath;
    }

    public static String getDbFolder() {
        return dbFolder;
    }

    public static void setDbFolder(String dbFolder) {
        FolderScanner.dbFolder = dbFolder;
    }
}
