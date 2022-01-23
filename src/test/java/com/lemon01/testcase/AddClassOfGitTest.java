package com.lemon01.testcase;

import org.testng.annotations.Test;

public class AddClassOfGitTest {
    public static void main(String[] args) {
        String wsx = concatString("efg",12);
        System.out.println(wsx);
        System.out.println(wsx.getClass());
    }




    public static String concatString(String a,Object b){
        String newStr = a+b;
        return newStr;
    }
}
