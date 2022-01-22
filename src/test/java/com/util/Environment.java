package com.util;
// 2021年12月31日   ----直播课 【上】

import com.alibaba.fastjson.JSONObject;

import java.sql.SQLOutput;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//相当于postman的环境变量设计，设计一个Map结构的类似于postman的环境变量区域
public class Environment {
    // 公共的属性，皆可访问    泛型指定<String,Object>  值类型为Object（可以接收多种数据类型，小数，整数，Boolean等）
    // 加static，通过 类名.属性名  的方式访问
    // 集合类型，在使用前先要实例化  // Map本身是个接口，不能实例化。所以使用HashMap<String,Object>()    HashMap是Map接口的主要实现类
    public static Map<String,Object> envMap = new HashMap<String,Object>();

    /**
     * 向环境变量中存储对应的键值对
     * @param varName  变量名
     * @param varValue 变量值
     */
    public static void saveToEnvironment(String varName,Object varValue){
        Environment.envMap.put(varName,varValue);
    }

    /**
     * 从环境变量区域中取得对应的值
     * @param varName  变量名
     * @return   返回的是Object类型的变量值
     */
    public static Object getToEnvironment(String varName){
        return Environment.envMap.get(varName);
    }

    /**
     * 字符串类型数据参数化替换   通过正则表达式进行参数替换，形成方法，便于调用
     * @param inputParam  接口入参
     * @return   参数化替换之后的结果
     * 当接口请求发送之前进行替换，替换后发起请求
     */
    public static String replaceParams(String inputParam){
        // 原始字符串：{"basketId":0,"count":1,"prodId":#prodId#,"shopId":1,"skuId":#skuId#}
        // 如何把原始字符串里面的 #prodId# 和 #skuId#  匹配出来，并替换为实际值。
        // 识别 #xxxx#  使用正则表达式(搜索，匹配字符串)
        String regex ="#(.+?)#";     // 正则表达式       // () 括号表示分组
        //编译得到Pattern模式对象--正则表达式模式对象
        Pattern pattern = Pattern.compile(regex);
        //通过pattern的matcher方法去匹配原始字符串，得到匹配器
        Matcher matcher = pattern.matcher(inputParam);
        //因为不知道原始字符串中是否有多个符合正则表达式的字符串，所以采用while循环来找
        // find() 方法为true--表示找到可匹配的字符串
        while(matcher.find()){
            String wholeStr = matcher.group(0);    //group(0) --表示匹配到的整个字符串，即#prodId#，#skuId#
            String subStr = matcher.group(1);     //group(1) --分组的第一个结果，即#prodId#，#skuId#里面的 prodId，skuId
            // 替换#xxxx#   【原字符串不能被改变，但可以重新被赋值】
            inputParam = inputParam.replace(wholeStr,Environment.getToEnvironment(subStr)+"");
        }
        return inputParam;
    }


    /** 方法重载(方法名相同，但参数列表不同)
     * 字符串类型数据参数化替换   通过正则表达式进行参数替换，形成方法，便于调用
     * @param headersMap  请求头
     * @return   参数化替换之后的结果
     * 当接口请求发送之前进行替换，替换后发起请求
     */
    public static Map replaceParams(Map headersMap){
        // Map转字符串
        String datas = JSONObject.toJSONString(headersMap);
//        String regex ="#(.+?)#";     // 正则表达式       // () 括号表示分组
//        //编译得到Pattern模式对象--正则表达式模式对象
//        Pattern pattern = Pattern.compile(regex);
//        //通过pattern的matcher方法去匹配原始字符串，得到匹配器
//        Matcher matcher = pattern.matcher(datas);
//        //因为不知道原始字符串中是否有多个符合正则表达式的字符串，所以采用while循环来找
//        // find() 方法为true--表示找到可匹配的字符串
//        while(matcher.find()){
//            String wholeStr = matcher.group(0);    //group(0) --表示匹配到的整个字符串，即#prodId#，#skuId#
//            String subStr = matcher.group(1);     //group(1) --分组的第一个结果，即#prodId#，#skuId#里面的 prodId，skuId
//            // 替换#xxxx#   【原字符串不能被改变，但可以重新被赋值】
//            datas = datas.replace(wholeStr,Environment.getToEnvironment(subStr)+"");
//        }
        datas = replaceParams(datas);
        //字符串转Map
        Map map = JSONObject.parseObject(datas,Map.class);
        return map;
    }

    // 2021年12月31日  --直播课 【下】
    public static void main(String[] agrs){
        // 如何把字符串里面的 #prodId# 和 #skuId#  匹配出来，并替换为实际值
        String inputParam = "{\"basketId\":0,\"count\":1,\"prodId\":#prodId#,\"shopId\":1,\"skuId\":#skuId#}";
        // {"basketId":0,"count":1,"prodId":"#prodId#","shopId":1,"skuId":#skuId#}   如何识别双井号
        // 参数化应用   第一步，将对应的值保存到环境变量中
        Environment.saveToEnvironment("prodId",101);
        Environment.saveToEnvironment("skuId",203);        // 这里存入的是Integer类型，取出也是Integer类型
        System.out.println(replaceParams(inputParam));   //输出已替换后的结果：{"basketId":0,"count":1,"prodId":101,"shopId":1,"skuId":203}







        /*    以下代码不是废代码，通过正则表达式进行参数替换，形成方法，便于调用
        // 识别 #xxxx#  使用正则表达式(搜索，匹配字符串)
        String regex ="#(.+?)#";     // 正则表达式       // () 括号表示分组
        //编译得到Pattern模式对象--正则表达式模式对象
        Pattern pattern = Pattern.compile(regex);
        //通过pattern的matcher方法去匹配原始字符串，得到匹配器
        Matcher matcher = pattern.matcher(inputParam);
        //因为不知道原始字符串中是否有多个符合正则表达式的字符串，所以采用while循环来找
        // find() 方法为true--表示找到可匹配的字符串
        while(matcher.find()){
            String wholeStr = matcher.group(0);    //group(0) --表示匹配到的整个字符串，即#prodId#，#skuId#
            String subStr = matcher.group(1);     //group(1) --分组的第一个结果，即#prodId#，#skuId#里面的 prodId，skuId
            // 替换#xxxx#   【原字符串不能被改变，但可以重新被赋值】
            inputParam = inputParam.replace(wholeStr,Environment.getToEnvironment(subStr)+"");
            System.out.println(inputParam);
        }
         */
    }
}
