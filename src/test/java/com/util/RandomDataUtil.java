package com.util;

import com.github.javafaker.Faker;

import java.util.Locale;

public class RandomDataUtil {
    public static void main(String[] args){
        // 使用Faker库，生成随机数据
        //Faker faker = new Faker();
        //System.out.println(faker.address().city());    // 随机城市
        //System.out.println(faker.address().country());    // 随机国家
        //System.out.println(faker.phoneNumber().phoneNumber());  // 随机手机号码（国外号码）

        //Faker faker = new Faker(Locale.CHINA);   // 表示实例化数据指定地区为中国
        //System.out.println(faker.phoneNumber().cellPhone());    // 随机生成11位的手机号码

        //System.out.println(faker.address().city());   //随机生成国内城市
        //System.out.println(faker.address().fullAddress());   // 随机生成国内地址全名
        //System.out.println(faker.name().fullName());    // 随机生成国内人名全称
        getUnregisterPhone();
    }


    /**
     * 定义方法--获取未被注册的手机号码
     * @return  未注册的手机号码
     */
    public static String getUnregisterPhone(){
        // 1. 随机生成手机号码
        Faker faker = new Faker(Locale.CHINA);
        String randomPhone = faker.phoneNumber().cellPhone();
        // 2. 查询数据库，判别随机生成的手机号码是否被注册过
        String sql= "SELECT COUNT(*) FROM tz_user WHERE user_mobile='"+randomPhone+";'";
        // 3. 循环遍历
        while(true){
            long count = (long)JDBCUtils.querySingleData(sql);    // ；为1--表达号码被注册过
            if (count == 0){
                // count计数，如果为0-表示未被注册过。符合要求，退出循环。
                break;
            }else if(count == 1){
                // count计数，如果为1-表示被注册过. 重新生成随机号码，sql语句查库
                randomPhone = faker.phoneNumber().cellPhone();
                sql= "SELECT COUNT(*) FROM tz_user WHERE user_mobile='"+randomPhone+";'";
            }
        }
        return randomPhone;
    }

    /**
     * 定义方法--获取未被注册过的用户名
     * @return   未注册的用户名
     */
    public static String getUnregisterName(){
        Faker faker = new Faker();
        String randomName = faker.name().lastName();
        String sql = "SELECT COUNT(*) from tz_user WHERE user_name='"+randomName+"';";
        while (true){
            long count = (long)JDBCUtils.querySingleData(sql);
            if(count == 0){
                // count计数，如果为0-表示未被注册过。符合要求，退出循环。
                break;
            }else if(count == 1){
                randomName = faker.name().lastName();
                sql = "SELECT COUNT(*) from tz_user WHERE user_name='"+randomName+"';";
            }
        }
        return randomName;
    }
}
