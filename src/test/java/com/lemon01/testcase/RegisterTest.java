package com.lemon01.testcase;

import com.ApiDefinition.ApiCall;
import com.common.BaseTest;
import com.util.Environment;
import com.util.JDBCUtils;
import com.util.RandomDataUtil;
import io.restassured.response.Response;
import org.apache.poi.xssf.usermodel.XSSFPivotTable;
import org.testng.Assert;
import org.testng.annotations.Test;

import static com.util.RandomDataUtil.getUnregisterName;
import static com.util.RandomDataUtil.getUnregisterPhone;

public class RegisterTest extends BaseTest {
    @Test
    public void test_register_success() {
        //自动化测试要求，代码能够重复多次运行。但是每次注册，手机号码不能重复，即手机号码没有被注册过！！
        //思路一：生成随机的手机号码
        //思路二：查数据库看是否被注册过
        //思路三：随机生成手机号码-->查库手机号码是否被注册过-->有被注册过-->重新生成随机号码-->查库-->没有被注册-->可用

        String randomPhone = RandomDataUtil.getUnregisterPhone();   // 准备随机用户名
        String randomName = RandomDataUtil.getUnregisterName();     // 准备随机用户名
        // 将随机生成的手机号码，用户名 保存到环境变量中
        Environment.saveToEnvironment("randomPhone", randomPhone);
        Environment.saveToEnvironment("randomName", randomName);

        // 1. 发起 发送验证码接口请求
        // 1-1. 准备测试数据
        String data01 = "{\"mobile\":\"#randomPhone#\"}";
        ApiCall.sendRegieterSms(data01);

        // 2. 校验验证码接口
        // 2-1. 关键问题：每次验证码不用，验证码该怎么获取？  查询数据库表tz_sms_log   ---> 通过java代码查询数据库
        String sql = "SELECT mobile_code from tz_sms_log WHERE id =(SELECT MAX(id) FROM tz_sms_log);";
        String code = (String) JDBCUtils.querySingleData(sql);
        // 将验证码保存到环境变量中
        Environment.saveToEnvironment("code", code);
        String data02 = "{\"mobile\":\"#randomPhone#\",\"validCode\":\"#code#\"}";
        Response checkRes = ApiCall.checkRegisterSms(data02);
        // 如何拿到接口响应中的纯文本类型的数据      // 解释：获取响应体数据并字符串化
        String checkSms = checkRes.body().asString();
        // 将验证码校验字符串保存到环境变量中
        Environment.saveToEnvironment("checkSms", checkSms);

        // 3. 注册接口请求 [手机号码未被注册，用户名未被注册]
        String data03 = "{\"appType\":3,\"checkRegisterSmsFlag\":\"#checkSms#\"," +
                "\"mobile\":\"#randomPhone#\",\"userName\":\"#randomName#\"," +
                "\"password\":\"123456\",\"registerOrBind\":1,\"validateType\":1}";
        Response registerRes = ApiCall.register(data03);

        // 4. 响应断言
        Assert.assertEquals(registerRes.getStatusCode(), 200);
        Assert.assertEquals(registerRes.jsonPath().get("nickName"), randomName);
        System.out.println("===响应状态码断言====响应体数据断言【昵称】=====");

        // 5.数据库断言
           // 5-1 查询数据库是否有记录
        //String assertSql ="select count(*) from tz_user where user_mobile='"+randomPhone+"';";
        String assertSql ="SELECT COUNT(*) from tz_user where user_mobile='#randomPhone#';";
        long actual = (long) JDBCUtils.querySingleData(assertSql);    // 发起查询，并将查询结果保存
           // 5-2 断言   有记录-即实际值
        Assert.assertEquals(actual,1);
        System.out.println("======数据库断言【查询数据库中是否有记录】=========");

    }
}
