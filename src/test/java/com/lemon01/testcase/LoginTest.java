package com.lemon01.testcase;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.ApiDefinition.ApiCall;
import com.alibaba.fastjson.JSONObject;
import com.common.BaseTest;
import com.pojo.CaseData;
import com.util.ExcelUtil;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static io.restassured.RestAssured.given;

public class LoginTest extends BaseTest {
    @Test
    public void test_login_success() {
        // 第一步：准备测试数据
        String jsonData = "{\"principal\":\"waiwai\",\"credentials\":\"lemon123456\",\"appType\":3,\"loginType\":0}";
        // 第二步：直接调用登录的接口请求
        Response res = ApiCall.login(jsonData);
        // 第三步：断言
        int statuscode = res.getStatusCode();
        Assert.assertEquals(statuscode,200);
        String nickName = res.jsonPath().get("nickName");
        Assert.assertEquals(nickName,"waiwai");
        System.out.println("===================");
    }

    @DataProvider
    public Object[] getLoginDatas(){
        Object[] data =
                {
                 "{\"principal\":\"waiwai\",\"credentials\":\"lemon123456\",\"appType\":3,\"loginType\":0}",
                 "{\"principal\":\"\",\"credentials\":\"lemon123456\",\"appType\":3,\"loginType\":0}",
                 "{\"principal\":\"waiwai\",\"credentials\":\"\",\"appType\":3,\"loginType\":0}",
                 "{\"principal\":\"lemon1111\",\"credentials\":\"lemon123456\",\"appType\":3,\"loginType\":0}"
                };
        return data;
    }

    // 通过代码容器（数组）传入测试数据
    @Test(dataProvider = "getLoginDatas",enabled = false)     // 禁止执行的，Allure报告中会显示  ？Unknown test_login_from_array
    public void test_login_from_array(String caseData){
        Response res = ApiCall.login(caseData);

    }

    // 通过读取外部文件（Excel）传入测试数据
    @Test(dataProvider = "getLoginDatasFromExcel")
    public void test_login_from_excel(CaseData caseData){
         // 发起接口请求，并接收响应数据
        Response res = ApiCall.login(caseData.getInputParams());
         // 3.断言
        String assertDatas = caseData.getAssertResponse();
        assertResponse(assertDatas,res);



//   以下不是废代码。抽取形成断言方法，便于调用
//        // {"statuscode":200,"nickName":"waiwai"}    json字符串转为java对象，使用fastjson
//        // 3-1 json字符串转为java对象（Map）     Map<键名类型,键值类型>
//        Map<String,Object> map = JSONObject.parseObject(assertDatas,Map.class);
//        // 3-2 遍历Map
//        Set<Entry<String,Object>>  datas = map.entrySet();         // entrySet() 获取所有的键值对
//        for(Entry<String,Object> keyValue:datas){
//            System.out.println(keyValue);     // 输出：statuscode=200  nickName=waiwai
//            String key = keyValue.getKey();         // statuscode   nickName
//            Object value = keyValue.getValue();     // 200         waiwai
//            // 3-3 断言
//           if("statuscode".equals(key)){
//                   // 状态码断言
//               int statuscode = res.getStatusCode();
//               System.out.println("断言响应状态码: 实际值--"+statuscode+",期望值--"+value);
//               Assert.assertEquals(statuscode,value);
//           }else{
//               Object actualValue = res.jsonPath().get(key);    // get(key) 取巧设计
//               System.out.println("断言响应体字段: 实际值--"+actualValue+",期望值--"+value);
//               Assert.assertEquals(actualValue,value);
//           }
//
//        }
    }

    @DataProvider
    public Object[] getLoginDatasFromExcel() throws Exception {
        /*
        抽取为工具类的方法 com.util.ExcelUtil.readExcel(int sheetNum)
        // 1. 读取Excel，使用EasyPOI。   首先在POM文件中导入EasyPOI坐标，然后刷新
        FileInputStream file = new FileInputStream("src/test/resources/caseData.xlsx");     // 推荐使用相对路径
        ImportParams importParams = new ImportParams();
        importParams.setStartSheetIndex(0);     // 从第一张表格开始读取
          // 构造映射类【@Excel注解（name=“列名”），私用属性，空参构造，set/get方法，重写toString方法)
          // 读取结果保存到集合中，并且集合的每个元素都是一个CaseData对象，对应一条数据
        List<CaseData> datas = ExcelImportUtil.importExcel(file, CaseData.class,importParams);
        // 集合转一维数组
        return datas.toArray();
         */
        return ExcelUtil.readExcel(0).toArray();
    }
}
