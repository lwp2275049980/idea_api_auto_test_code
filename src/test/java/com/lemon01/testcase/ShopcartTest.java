package com.lemon01.testcase;

import com.ApiDefinition.ApiCall;
import com.common.BaseTest;
import com.pojo.CaseData;
import com.service.BusinessFlow;
import com.util.Environment;
import com.util.ExcelUtil;
import com.util.JDBCUtils;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import sun.security.krb5.internal.APOptions;

import java.util.List;
import java.util.Map;

import static com.ApiDefinition.ApiCall.addShopCart;
import static io.restassured.RestAssured.given;
// 2021年12月27日   --作业
public class ShopcartTest extends BaseTest {
    @Test
    public void test_add_shopcart_success() {
        // 添加购物车用例  【业务流程用例】
        /*
        // 1，准备测试数据
        String loginData = "{\"principal\":\"waiwai\",\"credentials\":\"lemon123456\",\"appType\":3,\"loginType\":0}";
        // 2，发起接口请求
        // 2-1 登录
        Response loginRes = ApiCall.login(loginData);
        String token = loginRes.jsonPath().get("access_token");
        // 2-2 搜索商品
        String searchData = "prodName=&categoryId=&sort=0&orderBy=0&current=1&isAllProdType=true&st=0&size=12";
        Response searchRes = ApiCall.searchProduct(searchData);
           // 提取商品Id
        int prodId = searchRes.jsonPath().get("records[0].prodId");
        // 2-3 商品信息
        Response infoRes = ApiCall.productInfo(prodId);
        */
        Response infoRes = BusinessFlow.login_search_info();
        int skuId = infoRes.jsonPath().get("skuList[0].skuId");
        Environment.saveToEnvironment("skuId",skuId);
        // 2-4 添加购物车
        String shopCartData = "{\"basketId\":0,\"count\":1,\"prodId\":#prodId#,\"shopId\":1,\"skuId\":#skuId#}";     //定义为全局变量后，通过类名点的方式调用
            // 手动完成参数替换  --> 转移到接口定义层 做替换
            //shopCartData= Environment.replaceParams(shopCartData);
        Response shopCartRes = addShopCart(shopCartData,"#token#");
        // 3，响应断言
        int statuscode = shopCartRes.getStatusCode();
        Assert.assertEquals(statuscode,200);
        System.out.println("======================");

        // 4，数据库断言
        String assertSql = "SELECT * from tz_basket where user_id=(SELECT user_id FROM tz_user WHERE user_name='lemontester');";
        Map<String,Object> dbData = JDBCUtils.queryOneData(assertSql);     // 获取一条数据  用Map<String,Object>接收结果集
         // 根据购物车商品数量断言
        int basket_count = (int)dbData.get("basket_count");
        Assert.assertEquals(basket_count,1);
        System.out.println("========根据购物车商品数量断言（一条数据）==============");

        String assertSql2 = "SELECT count(*) from tz_basket where user_id=(SELECT user_id FROM tz_user WHERE user_name='lemontester');";
        // 根据购物车记录的条数断言
        long count = (long)JDBCUtils.querySingleData(assertSql2);
        Assert.assertEquals(count,1);
        System.out.println("=========根据购物车记录的条数断言=========");
    }

    //购物车-单接口测试
    @Test(dataProvider = "getShopCartDatas")
    public void test_shopcart(CaseData caseData){
        String loginData = "{\"principal\":\"waiwai\",\"credentials\":\"lemon123456\",\"appType\":3,\"loginType\":0}";
        Response loginRes = ApiCall.login(loginData);
        String token = loginRes.jsonPath().get("access_token");
        String inputParams = caseData.getInputParams();
        Response addShopCartRes = ApiCall.addShopCart(inputParams,token);

        // 做断言
        assertResponse(caseData.getAssertResponse(),addShopCartRes);

        // 数据库断言
        //caseData.getAssertDB();
        assertDB(caseData.getAssertDB());
    }

    @DataProvider
    public static Object[] getShopCartDatas(){
        List<CaseData> datas = ExcelUtil.readExcel(2);
        return datas.toArray();
    }
}
