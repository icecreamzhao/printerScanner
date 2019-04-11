package com.littleboy.tools;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

import java.io.File;

public class WordToPdf {
    /**
     * 把word转换成pdf
     * @param sFilePath 目标文件
     */
    public static String wordToPDF(String sFilePath) {
        System.out.println("启动 Word...");
        long start = System.currentTimeMillis();
        ActiveXComponent app = null;
        Dispatch doc = null;
        try {
            app = new ActiveXComponent("Word.Application");
            app.setProperty("Visible", new Variant(false));
            Dispatch docs = app.getProperty("Documents").toDispatch();
            doc = Dispatch.call(docs, "Open", sFilePath).toDispatch();
            System.out.println("打开文档:" + sFilePath);
            String toFilePath = sFilePath.substring(0, sFilePath.lastIndexOf("\\"))
                    + sFilePath.substring(sFilePath.lastIndexOf("\\"), sFilePath.lastIndexOf(".") ) + ".pdf";
            System.out.println("转换文档到 PDF:" + toFilePath);
            File tofile = new File(toFilePath);
            if (tofile.exists()) {
                tofile.delete();
            }
            Dispatch.call(doc, "SaveAs", toFilePath, // FileName
                    17);//17是pdf格式
            long end = System.currentTimeMillis();
            System.out.println("转换完成..用时：" + (end - start) + "ms.");
            return tofile.getAbsolutePath();
        } catch (Exception e) {
            System.out.println("========Error:文档转换失败：" + e.getMessage());
            e.printStackTrace();
        } finally {
            Dispatch.call(doc, "Close", false);
            System.out.println("关闭文档");
            if (app != null)
                app.invoke("Quit", new Variant[]{});
        }
        // 如果没有这句话,winword.exe进程将不会关闭
        ComThread.Release();
        return null;
    }
}
