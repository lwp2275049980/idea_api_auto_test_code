package com.lemon.day2;

import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class RestAssuredTest {
     @Test
    public void loginMallTest(){
         String jsonData="{\"principal\":\"waiwai\",\"credentials\":\"lemon123456\",\"appType\":3,\"loginType\":0}";
         given().
                 header("Content-Type","application/json").
                 body(jsonData).
         when().
                 post("http://mall.lemonban.com:8107/login").
         then().
                log().all();         // 打印到控制台
     }
}
