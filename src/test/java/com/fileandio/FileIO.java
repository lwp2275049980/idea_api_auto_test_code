package com.fileandio;

import java.io.File;
// 2022年1月18日
public class FileIO {
    public static void main(String[] args) {
        // File基本操作
        File file = new File("D://test/log.txt");
        boolean exists = file.exists();       //判断文件或文件夹是否存在
        System.out.println(exists);
        String name = file.getName();
        System.out.println(name);
        // 创建文件夹
        if(!file.exists()){
            file.mkdirs();     //创建层级目录
            //file.mkdir();   //只能创建当前目录或文件，即不能创建D://test/log.txt。要用mkdirs()
        }else{
            System.out.println("文件已存在");
        }
    }
}
