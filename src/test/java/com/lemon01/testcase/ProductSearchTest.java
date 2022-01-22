package com.lemon01.testcase;

import com.ApiDefinition.ApiCall;
import com.common.BaseTest;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
// 2021年12月27日
public class ProductSearchTest extends BaseTest {
    // 3，断言
    @Test
    public void test_searchproduct_success(){
        // 1,准备测试数据
        String data = "冰箱";
        // 2，发起搜索接口请求
        String inputParams = "prodName="+data+"&categoryId=&sort=0&orderBy=0&current=1&isAllProdType=true&st=0&size=12";
        Response res = ApiCall.searchProduct(inputParams);
        // 3,断言
           // 3-1 响应状态码断言
        int statuscode = res.getStatusCode();
        Assert.assertEquals(statuscode,200);
           // 3-2 响应体数据或字段断言
        String prodName = res.jsonPath().get("records[0].prodName");
        Assert.assertTrue(prodName.contains(data));
    }
}
