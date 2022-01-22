package com.lemon01.testcase;

import com.ApiDefinition.ApiCall;
import com.lemon.encryption.MD5Util;
import com.lemon.encryption.RSAManager;
import com.util.Environment;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.ResourceBundle;

public class EncryptTest {
    @Test
    public void test_md5(){
        String data = "123456";
        // 原始用户密码。如何加密成为密文？   找开发沟通加密方式，找他们要加密jar包，把jar包引入测试工程中
        String encryptData = MD5Util.stringMD5(data);
        System.out.println(encryptData);
        ApiCall.erpLogin("loginame=admin&password="+encryptData);
    }


    @Test
    public void test_rsa(){
        //前程贷项目没有在登录接口里限制加密处理，而是在其他的接口（充值）
        String data01 = "{\"mobile_phone\":\"13329334510\",\"pwd\":\"12345678\"}";
        Response res = ApiCall.futureloanLogin(data01);
        String token = res.jsonPath().get("data.token_info.token");
        int memberId = res.jsonPath().get("data.id");
        // 充值接口请求
        String sign = getSign(token);
        long timestamp = System.currentTimeMillis()/1000;
        Environment.saveToEnvironment("member_id",memberId);
        Environment.saveToEnvironment("timestamp",timestamp);
        Environment.saveToEnvironment("sign",sign);
        String data02 = "{\n" +
                "    \"member_id\":#member_id#,\n" +
                "    \"amount\":10000.0,\n" +
                "    \"timestamp\":#timestamp#,\n" +
                "    \"sign\":\"#sign#\"\n" +
                "}";
        ApiCall.futureloanRecharge(data02,token);
    }


    /**
     * 返回加密后的sign值
     * @param token
     * @return
     */
    public static String getSign(String token){
        // 获取秒级时间戳
        long timestamp = System.currentTimeMillis()/1000;
        //String token = "eyJhbGciOiJIUzUxMiJ9.eyJtZW1iZXJfaWQiOjgxMDUyOCwiZXhwIjoxNjQyNDEwNDE1fQ.imuVvIsrfAkY_sqi3qcUJj7Lt05ZpWdRDniexxcVo1a-6NCIoLpBMxLZ0TpsvY3plQimR2BWL8EVhxEmWvNerQ";
        // 获取token值的前50位
        String subStr = token.substring(0,50);
        // 拼接时间戳
        String newStr = subStr + timestamp;
        // 使用导入的加密jar包来进行rsa加密
        String sign = null;
        try {
            sign = RSAManager.encryptWithBase64(newStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sign;
    }
}
