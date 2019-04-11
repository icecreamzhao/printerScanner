package com.littleboy.tools;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPrintable;
import org.apache.pdfbox.printing.Scaling;

import javax.print.PrintService;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.standard.Sides;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PrintPdf {
    // 排除的打印机
    private static final String[] EXPORTPRINTER = new String[] {"Send To OneNote 2016", "OneNote", "Microsoft XPS Document Writer", "Microsoft Print to PDF", "Fax"};

    //这里传入的文件为word转化生成的pdf文件
    public static void PDFprint(File file, String printerName) throws Exception {
        PDDocument document = null;
        try {
            document = PDDocument.load(file);
            PrinterJob printJob = PrinterJob.getPrinterJob();
            printJob.setJobName(file.getName());
            if (printerName != null) {
                // 查找并设置打印机
                //获得本台电脑连接的所有打印机
                PrintService[] printServices = PrinterJob.lookupPrintServices();
                if (printServices == null || printServices.length == 0) {
                    System.out.print("打印失败，未找到可用打印机，请检查。");
                    return;
                }
                PrintService printService = null;
                //匹配指定打印机
                for (int i = 0; i < printServices.length; i++) {
                    if (printServices[i].getName().contains(printerName)) {
                        printService = printServices[i];
                        break;
                    }
                }
                if (printService != null) {
                    printJob.setPrintService(printService);
                } else {
                    System.out.print("打印失败，未找到名称为" + printerName + "的打印机，请检查。");
                    return;
                }
            }
            //设置纸张及缩放
            PDFPrintable pdfPrintable = new PDFPrintable(document, Scaling.ACTUAL_SIZE);
            //设置多页打印
            Book book = new Book();
            PageFormat pageFormat = new PageFormat();
            //设置打印方向
            pageFormat.setOrientation(PageFormat.PORTRAIT);//纵向
            pageFormat.setPaper(getPaper());//设置纸张
            book.append(pdfPrintable, pageFormat, document.getNumberOfPages());
            printJob.setPageable(book);
            printJob.setCopies(1);//设置打印份数
            //添加打印属性
            HashPrintRequestAttributeSet pars = new HashPrintRequestAttributeSet();
            pars.add(Sides.DUPLEX); //设置单双页
            printJob.print(pars);
        } finally {
            if (document != null) {
                try {
                    document.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取打印店中所有的打印机
     * @return
     */
    private static PrintService[] getPrinter() {
        //获得本台电脑连接的所有打印机
        PrintService[] printServices = PrinterJob.lookupPrintServices();

        // 环境中没有打印机
        if (printServices == null || printServices.length <= 0) {
            return null;
        } else {
            return printServices;
        }
    }

    /**
     * 获取所有打印机的名字
     * @return
     */
    public static List<String> getPrinterName() {
        PrintService[] printServices = getPrinter();
        List<String> printerNames = new ArrayList<>();
        if (printServices != null) {
            String exportPrinterTemp = "";
            for (PrintService printService : printServices) {
                for (String exportPrinter : EXPORTPRINTER) {
                    if (printService.getName().equals(exportPrinter)) {
                        exportPrinterTemp = exportPrinter;
                    }
                }
                if (!exportPrinterTemp.equals(printService.getName())) {
                    printerNames.add(printService.getName());
                }
            }
        }
        if (printerNames.size() > 0) {
            return printerNames;
        } else {
            return null;
        }
    }

    public static Paper getPaper() {
        Paper paper = new Paper();
        // 默认为A4纸张，对应像素宽和高分别为 595, 842
        int width = 595;
        int height = 842;
        // 设置边距，单位是像素，10mm边距，对应 28px
        int marginLeft = 10;
        int marginRight = 0;
        int marginTop = 10;
        int marginBottom = 0;
        paper.setSize(width, height);
        // 下面一行代码，解决了打印内容为空的问题
        paper.setImageableArea(marginLeft, marginRight, width - (marginLeft + marginRight), height - (marginTop + marginBottom));
        return paper;
    }
}
