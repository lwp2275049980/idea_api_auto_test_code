package com.lemon01.testcase;

import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class MockTest {
    @Test
    public void test_moco(){
        String inputParams = "{\"phone\":\"13323234545\",\"pwd\":\"123456\"}";
        Map headersMap = new HashMap();
        headersMap.put("X-Lemonban-Media-Type","Lemonban.v1");
        headersMap.put("Content-Type","application/json;charset=UTF-8");
        given().
                log().all().
                headers(headersMap).
                body(inputParams).
        when().
               post("http://127.0.0.1:9999/pay").
        then().
               log().all();
    }
}
