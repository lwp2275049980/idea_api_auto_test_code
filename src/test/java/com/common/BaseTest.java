package com.common;

import com.alibaba.fastjson.JSONObject;
import com.util.JDBCUtils;
import io.restassured.response.Response;
import org.testng.Assert;

import java.util.Map;
import java.util.Set;
// 2021年12月31日  --直播课 【上】
public class BaseTest {
    /**
     * 通用的响应断言方法 [仅限于单接口测试断言]
     * @param assertDatas   Excel中的断言数据（Json格式设计）
     * @param res  接口响应结果数据
     */
    public void assertResponse(String assertDatas, Response res) {
        // 判空处理。当从Excel文件中读取响应数据为空时，表示不需要断言
        if (null != assertDatas) {
            // {"statuscode":200,"nickName":"waiwai"}    json字符串转为java对象，使用fastjson
            // 3-1 json字符串转为java对象（Map）     Map<键名类型,键值类型>
            Map<String, Object> map = JSONObject.parseObject(assertDatas, Map.class);
            // 3-2 遍历Map
            Set<Map.Entry<String, Object>> datas = map.entrySet();         // entrySet() 获取所有的键值对
            for (Map.Entry<String, Object> keyValue : datas) {
                System.out.println(keyValue);     // 输出：statuscode=200  nickName=waiwai
                String key = keyValue.getKey();         // statuscode   nickName
                Object value = keyValue.getValue();     // 200         waiwai
                // 3-3 断言
                if ("statuscode".equals(key)) {
                    // 状态码断言
                    int statuscode = res.getStatusCode();
                    System.out.println("断言响应状态码: 实际值--" + statuscode + ",期望值--" + value);
                    Assert.assertEquals(statuscode, value);
                } else {
                    Object actualValue = res.jsonPath().get(key);    // get(key) 取巧设计
                    System.out.println("断言响应体字段: 实际值--" + actualValue + ",期望值--" + value);
                    Assert.assertEquals(actualValue, value);
                }
            }
        }
    }

    /**
     * 通用的数据库断言方法   【仅限于单接口测试断言】
     * @param assertDB
     * 解决断言中实际值与期望值的数据类型不同，两个值作字符串化 toString()
     */
    public void assertDB(String assertDB){
        // 把原始的断言数据（json）转化为Map
        Map<String,Object> map = JSONObject.parseObject(assertDB,Map.class);
        Set<Map.Entry<String,Object>> datas = map.entrySet();
        for(Map.Entry<String,Object> kayValue:datas){
            // map中的key对应的是查询sql语句
            Object actualValue = JDBCUtils.querySingleData(kayValue.getKey());
            System.out.println("实际值类型： "+actualValue.toString());
            System.out.println("期望值类型: "+kayValue.getValue().toString());
            // map中的value对应的是期望值
            Assert.assertEquals(actualValue.toString(),kayValue.getValue().toString());
            // 上一句断言中的实际值与期望值 类型不同，需要做类型匹配
            // 包装类的对象才有方法，比如toString()等   基本数据类型 没有方法
            // 断言值做字符换化 加toString()
        }
    }
}
