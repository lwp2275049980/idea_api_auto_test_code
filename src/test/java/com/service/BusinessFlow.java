package com.service;

import com.ApiDefinition.ApiCall;
import com.util.Environment;
import io.restassured.response.Response;
import org.testng.annotations.Test;

// 2021年12月29日  直播课【上】
public class BusinessFlow {
//    // 定义prodId，token为全局变量
//    public static int prodId;
//    public static String token;
    /**
     * 登录-->搜索-->商品信息  场景组合接口调用
     * @return  商品信息接口的响应数据
     */
    public static Response login_search_info(){
        // 场景组合由多个接口请求组成
        // 1. 登录接口
        String loginData = "{\"principal\":\"waiwai\",\"credentials\":\"lemon123456\",\"appType\":3,\"loginType\":0}";
        Response loginRes = ApiCall.login(loginData);
           // 提取token
        String token = loginRes.jsonPath().get("access_token");
             // （token）保存到环境变量当中   【2021年12月31日   直播课【上】】
        Environment.saveToEnvironment("token",token);       //存入环境变量当中
        //Environment.envMap.put("token",token);
        // 2. 搜索接口
        String searchData = "prodName=&categoryId=&sort=0&orderBy=0&current=1&isAllProdType=true&st=0&size=12";
        Response searchRes = ApiCall.searchProduct(searchData);
        int prodId = searchRes.jsonPath().get("records[0].prodId");
          // （prodId）保存到环境变量当中   【2021年12月31日   直播课【上】】
        Environment.saveToEnvironment("prodId",prodId);      // 存入环境变量当中
        // Environment.envMap.put("prodId",prodId);        // 因为定义属性时加了static，所以这里可以通过【类名.属性名】的方式调用，因为是Map类型，所以使用put()方法存入数据
        // 3. 商品信息接口
        Response infoRes = ApiCall.productInfo(prodId);
        return infoRes;
    }
}
