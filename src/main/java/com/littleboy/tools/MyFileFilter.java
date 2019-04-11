package com.littleboy.tools;

import java.io.File;
import java.io.FilenameFilter;

/**
 * 文件过滤器, 可通过后缀名来过滤文件
 */
public class MyFileFilter implements FilenameFilter {
    private static final String[] FILEENDNAME = new String[] {
            ".docx", ".pdf"
    };
    private static final String[] FILESTARTNAME = new String[] {
            "~$"
    };

    @Override
    public boolean accept(File dir, String name) {
        boolean startMatch = false;
        boolean endMatch = false;
        for (String startName : FILESTARTNAME) {
            if (!name.startsWith(startName)) {
                startMatch = true;
            }
        }

        for (String endName : FILEENDNAME) {
            if (name.endsWith(endName)) {
                endMatch = true;
            }
        }

        File file = new File(dir, name);

        if (file.isDirectory()) {
            startMatch = true;
            endMatch = true;
        }

        return startMatch && endMatch;
    }
}
