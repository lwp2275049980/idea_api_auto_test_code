package com.ApiDefinition;

import com.common.GlobalConfig;
import com.util.Environment;
import io.qameta.allure.Allure;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.awt.geom.RectangularShape;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

@Test
public class ApiCall {
    /**
     * 接口请求通用方法封装
     * @param method    请求方法（get/post/put/delete）
     * @param url       接口请求地址
     * @param headersMap  请求头，存放在Map结构中.get请求一般没有请求头，put一个Content-Type占位；post若有多个请求头，就put多次。
     * @param inputParams  请求参数
     * @return  接口响应结果
     */
    public static Response request(String method, String url, Map headersMap,String inputParams){
        //把所有的接口日志（请求+响应）重定向到本地指定文件中（信息集中，不方便查看）
//        PrintStream fileOutPutStream = null;
//        try {
//            fileOutPutStream = new PrintStream(new File("log/test_all.log"));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        RestAssured.filters(new RequestLoggingFilter(fileOutPutStream),new ResponseLoggingFilter(fileOutPutStream));
        String logFilePath = null;
        if(!GlobalConfig.IS_DUBUG) {
            //每个接口请求的日志单独保存到本地的每一个文件中  【加上如下代码--日志信息不会输出到控制台，而是重定向后保存到代码指定的文件中】
            PrintStream fileOutStream = null;
            // 设置日志文件的地址
            String logFileDir = "target/log/";
            // 单独运行 【mvn clean】 会清除整个target目录   终端提示：[INFO] Deleting D:\java35\target
            File file = new File(logFileDir);
            if (!file.exists()) {
                file.mkdirs();  // target/log/test   创建层级目录
                // file.mkdir();  // target/log  当前目录下创建一个新目录
            }
            logFilePath = logFileDir + "test_" + "_" + System.currentTimeMillis() + ".log";
            try {
                fileOutStream = new PrintStream(new File(logFilePath));     // 因为文件单独保存，文件名不能写死，必须是个变量。文件名加时间戳
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            RestAssured.config = RestAssured.config().logConfig(LogConfig.logConfig().defaultStream(fileOutStream));
        }
        // 参数化替换
        // 1，接口入参做参数化替换
        inputParams = Environment.replaceParams(inputParams);
        // 2，请求头参数化替换
        headersMap = Environment.replaceParams(headersMap);
        // 3,接口地址参数化替换
        url = Environment.replaceParams(url);
        // 指定项目baseUrl
        RestAssured.baseURI = GlobalConfig.url;
        Response res = null;    // res变量初始化
        if("get".equalsIgnoreCase(method)){
            res = given().log().all().headers(headersMap).when().get(url+"?"+inputParams).then().log().all().extract().response();
        }else if ("post".equalsIgnoreCase(method)){
            res = given().log().all().headers(headersMap).body(inputParams).when().post(url).then().log().all().extract().response();
        }else if("put".equalsIgnoreCase(method)){
            res = given().log().all().headers(headersMap).body(inputParams).when().put(url).then().log().all().extract().response();
        }else if("delete".equalsIgnoreCase(method)){
            // TODO
        }else{
            System.out.println("接口请求方法非法，请检查你的请求方法");
        }

        // 如果为true--调试模式--日志信息输出到控制台，  false--非调试模式--日志信息添加到Allure报表当中
        if(!GlobalConfig.IS_DUBUG) {
            try {
                   // 添加日志信息到Allure报表中  含义： Allure.添加附件（附近名称,文件路径）
                Allure.addAttachment("接口的请求/响应消息", new FileInputStream(logFilePath));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    /** 2021年12月27日
     * 登录接口请求定义
     * @param inputParams  传入的接口入参
     * {"principal":"waiwai","credentials":"lemon123456","appType":3,"loginType":0}
     * @return  返回响应数据
     */
    public static Response login(String inputParams){
        Map headMap = new HashMap<>();
        headMap.put("Content-Type", "application/json");
        return request("post","/login",headMap,inputParams);
    }

    /** 2021年12月27日
     * 搜索商品接口请求定义
     * @param inputParams   接口请求入参
     * prodName=&categoryId=&sort=0&orderBy=0&current=1&isAllProdType=true&st=0&size=12
     * @return   商品搜索接口的响应信息
     */
    public static Response searchProduct(String inputParams) {
        inputParams = Environment.replaceParams(inputParams);
        Map headMap = new HashMap<>();
        headMap.put("Content-Type", "application/json");
        return request("get", "/search/searchProdPage", headMap,inputParams);
    }

    /** 2021年12月27日
     * 商品信息接口请求当定义
     * @param prodId   商品Id
     * @return   响应结果
     */
    public static Response productInfo(int prodId){
        Map headMap = new HashMap<>();
        headMap.put("Content-Type", "application/json");
        return request("get","/prod/prodInfo",headMap,"prodId="+prodId);
    }

    /**
     * 添加购物车接口请求
     * {"basketId":0,"count":1,"prodId":"83","shopId":1,"skuId":415}
     * @param inputParams   接口请求入参
     * @param token   鉴权token值，从登录接口获取
     * @return   响应数据
     */
    public static Response addShopCart(String inputParams,String token){
        Map headMap = new HashMap<>();
        headMap.put("Content-Type", "application/json");
        headMap.put("Authorization","bearer"+token);
        return request("post","/p/shopCart/changeItem",headMap,inputParams);
    }

    /**
     * 注册验证码发送接口请求
     * @param inputParams  请求参数
     * {"mobile":"17366229999"}
     * @return   响应数据
     */
    public static Response sendRegieterSms(String inputParams){
        Map headMap = new HashMap<>();
        headMap.put("Content-Type", "application/json");
        return request("put","/user/sendRegisterSms",headMap,inputParams);
    }

    /**
     * 校验注册验证码接口请求
     * @param inputParams  请求参数
     * {"mobile":"17366229999","validCode":"149900"}
     * @return
     */
    public static Response checkRegisterSms(String inputParams){
        Map headMap = new HashMap<>();
        headMap.put("Content-Type", "application/json");
        return request("put","/user/checkRegisterSms",headMap,inputParams);
    }

    /**
     * 注册接口请求
     * @param inputParams  请求参数
     * {
     * "appType":3,
     * "checkRegisterSmsFlag":"d2924abd80c94dbe92530d8d76df91b3",
     * "mobile":"17366229999",
     * "userName":"lemon01",
     * "password":"123456",
     * "registerOrBind":1,
     * "validateType":1
     * }
     * @return   响应数据
     */
    public static Response register(String inputParams){
        Map headMap = new HashMap<>();
        headMap.put("Content-Type", "application/json");
        return request("put","/user/registerOrBindUser",headMap,inputParams);

    }

    /**
     * 确认订单接口定义
     * @param inputParams  请求参数
     * @param token
     * {
     *  "addrId":0,"orderItem":{"prodId":412,"skuId":766,"prodCount":1,"shopId":1},
     *  "couponIds":[],"isScorePay":0,"userChangeCoupon":0,"userUseScore":0,
     *  "uuid":"762d9c76-c0a0-4992-8175-ae027eba017f"
     * }
     * @return
     */
    public static Response confirmOrder(String inputParams,String token){
        Map headMap = new HashMap<>();
        headMap.put("Content-Type", "application/json");
        headMap.put("Authorization","bearer"+token);
        return request("post","/p/order/confirm ",headMap,inputParams);
    }

    /**
     * 提交订单接口定义
     * @param inputParams 请求参数
     * {"orderShopParam":[{"remarks":"","shopId":1}],"uuid":"762d9c76-c0a0-4992-8175-ae027eba017f"}
     * @param token
     * @return
     */
    public static Response submitOrder(String inputParams,String token){
        Map headMap = new HashMap<>();
        headMap.put("Content-Type", "application/json");
        headMap.put("Authorization","bearer"+token);
        return request("post","/p/order/submit",headMap,inputParams);
    }

    /**
     * 支付下单接口定义
     * @param inputParams  请求参数
     * {"payType":3,"orderNumbers":"1481577895238569984"}
     * @param token
     * @return
     */
    public static Response placeOrder(String inputParams,String token){
        Map headMap = new HashMap<>();
        headMap.put("Content-Type", "application/json");
        headMap.put("Authorization","bearer"+token);
        return request("post","/p/order/pay",headMap,inputParams);
    }

    /**
     * 模拟支付回调接口定义
     * @param inputParams
     * {"payNo":1481577895238569984,"bizPayNo":"XXXX","isPaySuccess":true}
     * @param token
     * @return
     */
    public static Response mockPay(String inputParams,String token){
        Map headMap = new HashMap<>();
        headMap.put("Content-Type", "application/json");
        headMap.put("Authorization","bearer"+token);
        return request("post","/notice/pay/3",headMap,inputParams);
    }


    /**
     * erp项目登录请求
     * @param inputParams
     * @return
     */
    public static Response erpLogin(String inputParams){
        Map headMap = new HashMap();
        headMap.put("Content-Type","application/x-www-form-urlencoded");
        return request("post","/user/login",headMap,inputParams);
    }

    /**
     * 前程贷项目登录请求
     * @param inputParams
     * @return
     */
    public static Response futureloanLogin(String inputParams){
        Map headMap = new HashMap();
        headMap.put("X-Lemonban-Media-Type","lemonban.v3");
        headMap.put("Content-Type","application/json");
        return request("post","/futureloan/member/login",headMap,inputParams);
    }

    /**
     * 前程贷项目的充值接口
     * @param inputParams
     * @param token
     * @return
     */
    public static Response futureloanRecharge(String inputParams,String token){
        Map headMap = new HashMap();
        headMap.put("X-Lemonban-Media-Type","lemonban.v3");
        headMap.put("Content-Type","application/json");
        headMap.put("Authorization","Bearer "+token);
        return request("post","/futureloan/member/recharge",headMap,inputParams);
    }
}
