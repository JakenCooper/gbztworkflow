package com.gbzt.gbztworkflow.utils;

import java.io.*;

public class FileUtils {

    public static void main(String[] args) {
        File file1 = new File("D:\\232.txt");
        File file2 = new File("C:\\Users\\Administrator\\Desktop\\123456\\a");
        // file1.deleteOnExit();
        try {
            file2.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        copyFile(file1,file2); // 实现复制
        file1.deleteOnExit();// 删除file1
    }
    /**
     * 文件复制文件方法
     * @param oldFile 被复制的文件(旧文件)
     * @param newFile 复制后的文件(新文件)
     */
    public static void copyFile(File oldFile, File newFile) {

        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        byte[] bytes = new byte[1024];
        int temp = 0;
        try {
            inputStream = new FileInputStream(oldFile);
            fileOutputStream = new FileOutputStream(newFile);
            while ((temp = inputStream.read(bytes)) != -1) {
                fileOutputStream.write(bytes, 0, temp);
                fileOutputStream.flush();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * 先根遍历序递归删除文件夹
     *
     * @param dirFile 要被删除的文件或者目录
     * @return 删除成功返回true, 否则返回false
     */
    public static boolean deleteFile(File dirFile) {
        // 如果dir对应的文件不存在，则退出
        if (!dirFile.exists()) {
            return false;
        }

        if (dirFile.isFile()) {
            return dirFile.delete();
        } else {
            for (File file : dirFile.listFiles()) {
                deleteFile(file);
            }
        }
        return dirFile.delete();
    }
    
}