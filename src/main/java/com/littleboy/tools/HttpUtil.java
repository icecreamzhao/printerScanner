package com.littleboy.tools;

import com.littleboy.sqlLiteConnect.PrinterDao;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

public class HttpUtil {
    /**
     * 向指定 URL 发送POST方法的请求
     */
    public static File sendPost(String url) throws IOException {
        try {
            String downloadDir = new PrinterDao().getFilePath();
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置允许输入流输入数据到本地
            conn.setDoInput(true);
            // 设置允许输出流输出到服务器
            conn.setDoOutput(true);
            // 获取内容长度
            int fileLength = conn.getContentLength();
            // 获取文件url径名称
            String filePathName = conn.getURL().getFile();
            // 获取文件名称
            String fileName = filePathName.substring(filePathName.lastIndexOf(File.separatorChar) + 1);

            // 定义文件下载的目录与名称
            String path = downloadDir + File.separatorChar + fileName;

            // 实例化文件对象
            File file = new File(path);

            // 判断文件路径是否存在
            if (!file.getParentFile().exists()) {
                // 如果文件不存在就创建文件
                file.getParentFile().mkdirs();
            }

            // 从连接对象中获取输入字节流
            InputStream inputStream = conn.getInputStream();

            // 实例化输入流缓冲区，将输入字节流传入
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

            // 实例化输出流对象，将文件对象传入
            OutputStream outputStream = new FileOutputStream(file);

            // 定义整形变量用来接收读取到的文件大小
            int size;
            // 定义整形变量用来累计当前读取到的文件长度
            int len = 0;
            // 定义字节数组对象，用来从输入缓冲区中装载数据块
            byte[] buf = new byte[1024];
            // 从输入缓冲区中一次读取1024个字节的文件内容到buf对象中，并将读取大小赋值给size变量，当读取完毕后size=-1，结束循环读取
            while ((size = bufferedInputStream.read(buf)) != -1) {
                // 累加每次读取到的文件大小
                len += size;
                // 向输出流中写出数据
                outputStream.write(buf, 0, size);
                // 打印当前文件下载的百分比
                System.out.println("下载进度：" + len * 100 / fileLength + "%\n");
            }
            // 关闭输出流
            outputStream.close();
            // 关闭输入缓冲区
            bufferedInputStream.close();
            // 关闭输入流
            inputStream.close();

            // 返回文件对象
            return file;
    } catch (IOException e) {
        e.printStackTrace();
    }
        return null;
    }
}
