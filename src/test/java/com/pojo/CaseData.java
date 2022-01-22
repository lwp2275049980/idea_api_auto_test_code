package com.pojo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import org.testng.annotations.Test;

// 映射类，必须写入 私有属性，空参构造，set/get方法
// 通过@Excel注解映射到excel表格的哪一列，name=“列名”, 这里的列名，必须与excel文件中的列名一致。否则，读取时可能报错空指针
public class CaseData {
    @Excel(name = "用例编号")
    private int caseId;

    @Excel(name = "用例标题")
    private String caseTitle;

    @Excel(name = "接口入参")
    private String inputParams;

    @Excel(name = "响应断言")
    private String assertResponse;

    @Excel(name = "数据库断言")
    private String assertDB;

    public CaseData(){
         // 空参构造
    }

    public int getCaseId() {
        return caseId;
    }

    public void setCaseId(int caseId) {
        this.caseId = caseId;
    }

    public String getCaseTitle() {
        return caseTitle;
    }

    public void setCaseTitle(String caseTitle) {
        this.caseTitle = caseTitle;
    }

    public String getInputParams() {
        return inputParams;
    }

    public void setInputParams(String inputParams) {
        this.inputParams = inputParams;
    }

    public String getAssertResponse() {
        return assertResponse;
    }

    public void setAssertResponse(String assertResponse) {
        this.assertResponse = assertResponse;
    }

    public String getAssertDB() {
        return assertDB;
    }

    public void setAssertDB(String assertDB) {
        this.assertDB = assertDB;
    }

    @Override
    public String toString() {
        return "CaseData{" +
                "caseId=" + caseId +
                ", caseTitle='" + caseTitle + '\'' +
                ", inputParams='" + inputParams + '\'' +
                ", assertResponse='" + assertResponse + '\'' +
                ", assertDB='" + assertDB + '\'' +
                '}';
    }
}
