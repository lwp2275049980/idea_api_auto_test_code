package com.util;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.pojo.CaseData;

import java.io.File;
import java.util.List;

public class ExcelUtil {
    public static final String EXCEl_FILE_PATH = "src/test/resources/caseData.xlsx";

    /**
     * 读取外部Excel文件中数据
     * @param sheetNum   读取Excel文件表格的编号（从0开始，默认0--第一张）
     * @return 返回读取到的数据 [数据保存在集合中，若要参数化使用，要转为一维数组  datas.toArray()]
     */
    public static List<CaseData> readExcel(int sheetNum){
        ImportParams importParams = new ImportParams();
        importParams.setStartSheetIndex(sheetNum);
        // 读取的文件路径src/test/resources/caseData.xlsx
        List<CaseData> datas = ExcelImportUtil.importExcel(new File(EXCEl_FILE_PATH),CaseData.class,importParams);
        return datas;
    }
}
