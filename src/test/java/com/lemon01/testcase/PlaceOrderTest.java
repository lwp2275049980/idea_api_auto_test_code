package com.lemon01.testcase;

import com.ApiDefinition.ApiCall;
import com.service.BusinessFlow;
import com.util.Environment;
import com.util.JDBCUtils;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PlaceOrderTest {
    @Test
    public void test_place_order_success(){
        // 调用业务逻辑层（登录-商品搜索--商品信息）
        Response prodInfoRes = BusinessFlow.login_search_info();
        int skuId = prodInfoRes.jsonPath().get("skuList[0].skuId");
        Environment.saveToEnvironment("skuId",skuId);

        // 1.确认订单
        String confirmDatas = "{\"addrId\":0,\"orderItem\":{\"prodId\":#prodId#,\"skuId\":#skuId#," +
                "\"prodCount\":1,\"shopId\":1},\"couponIds\":[],\"isScorePay\":0,\"userChangeCoupon\":0," +
                "\"userUseScore\":0,\"uuid\":\"762d9c76-c0a0-4992-8175-ae027eba017f\"}";
        Response confirmRes = ApiCall.confirmOrder(confirmDatas,"#token#");

        // 2.提交订单
        String submitData = "{\"orderShopParam\":[{\"remarks\":\"\",\"shopId\":1}],\"uuid\":\"762d9c76-c0a0-4992-8175-ae027eba017f\"}";
        Response submitRes = ApiCall.submitOrder(submitData,"#token#");
            // 获取订单号
        String orderNumbers = submitRes.jsonPath().get("orderNumbers");
        Environment.saveToEnvironment("orderNumbers",orderNumbers);

        // 3.下单支付
        String placeOrderData = "{\"payType\":3,\"orderNumbers\":\"#orderNumbers#\"}";
        Response placeOrdeRes = ApiCall.placeOrder(placeOrderData,"#token#");

        // 4. 模拟回调接口--模拟真实的支付流程（返回支付成功 success，此时数据库订单状态-status应改为 2-待发货）
        String mockPayData = "{\"payNo\":#orderNumbers#,\"bizPayNo\":\"XXXX\",\"isPaySuccess\":true}";
        Response mockPayRes = ApiCall.mockPay(mockPayData,"#token#");

        // 1.响应断言
                   // 提取纯文本响应体数据  res.body().asString()
        String actual = mockPayRes.body().asString();
        Assert.assertEquals(actual,"success");

        System.out.println("==TestNG:有多个断言时，前一个断言失败，后面的断言就不执行==");

        //2.数据库断言
        String sql = "SELECT status FROM tz_order WHERE order_number='#orderNumbers#';";
        Object actualDB = JDBCUtils.querySingleData(sql);
        System.out.println(actualDB.getClass());
        Assert.assertEquals(actualDB,2);
        // 2022年1月12日 这里有bug，模拟支付成功返回success，数据库中status应改为 2-待发货，但数据库未更新仍显示 1-待付款，所以断言失败。
    }
}
