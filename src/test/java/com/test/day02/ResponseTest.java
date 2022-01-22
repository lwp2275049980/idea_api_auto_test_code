package com.test.day02;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;

public class ResponseTest {
    @Test
    public void test01(){
        String jsonData = "{\"principal\":\"waiwai\",\"credentials\":\"lemon123456\",\"appType\":3,\"loginType\":0}";
        Response res =
        given().
                header("Content-Type","application/json").
                body(jsonData).
        when().
                post("http://mall.lemonban.com:8107/login").
        then().
                log().all().        // 打印输出所有响应数据
               //log().body();          // 只打印输出响应体
               extract().response();     // 解析响应数据，作进一步处理
        // 获取响应头数据或字段值
        // 获取响应状态码
        //System.out.println(res.getStatusCode());
        // 获取接口响应时间
        //System.out.println(res.time());
        // 获取所有的响应头信息(加s-复数)
        //System.out.println(res.getHeaders());        // 重写了toString()能够打印输出
        // 获取响应头某个字段值
        //System.out.println(res.getHeader("Content-Type"));
        //System.out.println(res.getHeader("Set-cookie"));



        // 获取响应体数据或字段值
            // 比如，获取响应体中的【nickName】值
        System.out.println((String) res.jsonPath().get("nickName"));
        System.out.println((Integer) res.jsonPath().get("expires_in"));
    }

    @Test
    public void test02(){
        // 提取json
        Response res =
                given().
                when().
                        get("http://mall.lemonban.com:8107/prod/prodListByTagId?tagId=2&size=12").
                then().
                        log().all().        // 打印输出所有响应数据
                        //log().body();          // 只打印输出响应体
                extract().response();     // 解析响应数据，作进一步处理
        // 获取响应头数据或字段值
               // 下标为0，表示json数组的第一个元素，1，2，3.。。以此类推
               // 下标为-1，表示json数组的最后一个元素，-2表示倒数第二个，以此类推
        System.out.println((String) res.jsonPath().get("records[0].shopName"));
    }
    @Test
    public void test03(){
        // 提取XML
        Response res =
                given().
                when().
                        get("http://httpbin.org/xml").
               then().
                        log().all().        // 打印输出所有响应数据
                        //log().body();          // 只打印输出响应体
                        extract().response();     // 解析响应数据，作进一步处理
        System.out.println((String) res.xmlPath().get("slideshow.slide[1].item[0].em"));
    }

    @Test
    public void test04(){
        // 提取HTML
        Response res =
                given().
                when().
                        get("http://www.baidu.com/").
                then().
                        log().all().        // 打印输出所有响应数据
                        //log().body();          // 只打印输出响应体
                        extract().response();     // 解析响应数据，作进一步处理
        System.out.println((String) res.htmlPath().get("html.head.title"));
        System.out.println((String) res.htmlPath().get("html.head.meta[2].@name"));     // 获取标签里面的某个属性值，@属性名
    }


    // 接口响应断言
    // 关注对业务比较重要的数据或字段作断言，并不是针对所有数据或字段做断言
    @Test
    public void test05() {
        String jsonData = "{\"principal\":\"waiwai\",\"credentials\":\"lemon123456\",\"appType\":3,\"loginType\":0}";
        Response res =
                given().
                        header("Content-Type", "application/json").
                        body(jsonData).
                when().
                        post("http://mall.lemonban.com:8107/login").
                then().
                        //log().all().        // 打印输出所有响应数据
                        log().body().          // 只打印输出响应体
                        extract().response();     // 解析响应数据，作进一步处理
        // 响应状态码断言
        int statusCode = res.getStatusCode();
        Assert.assertEquals(statusCode,2000);

        System.out.println("前一句断言失败，后面的断言就不执行【判定用例失败】");

           // 响应体数据或字段断言
        String nickName = res.jsonPath().get("nickName");
        Assert.assertEquals(nickName,"waiwai");       // Assert.assertEquals(实际值，期望值)
    }
}
