package com.ls.supstar.custom;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.alibaba.excel.util.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomDateConverter implements Converter<Date> {

    // 支持的日期格式列表
    private static final String[] SUPPORTED_FORMATS = {
            "yyyy/M/d H:mm",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy/M/d",
            "yyyy-MM-dd",
            "yyyy年M月d日"
    };

    @Override
    public Class<Date> supportJavaTypeKey() {
        return Date.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING; // 指定Excel中存储为字符串格式
    }

    @Override
    public Date convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty,
                                  GlobalConfiguration globalConfiguration) throws ExcelDataConvertException {
        Integer rowIndex = cellData.getRowIndex();
        Integer columnIndex = cellData.getColumnIndex();

        try {
            // 处理文本型日期
            if (cellData.getType() == CellDataTypeEnum.STRING) {
                String dateStr = cellData.getStringValue().trim();
                for (String format : SUPPORTED_FORMATS) {
                    try {
                        return new SimpleDateFormat(format).parse(dateStr);
                    } catch (ParseException ignored) {}
                }
                throw new ExcelDataConvertException(rowIndex, columnIndex, cellData, contentProperty,
                        "无法解析日期: " + dateStr + "，支持格式: " + String.join("、", SUPPORTED_FORMATS));
            }

            // 处理数字型日期(Excel序列号)
            if (cellData.getType() == CellDataTypeEnum.NUMBER) {
                try {
                    return DateUtils.getJavaDate(cellData.getNumberValue().doubleValue(), false);
                } catch (Exception e) {
                    throw new ExcelDataConvertException(rowIndex, columnIndex, cellData, contentProperty,
                            "数字日期转换失败: " + cellData.getNumberValue(), e);
                }
            }

            throw new ExcelDataConvertException(rowIndex, columnIndex, cellData, contentProperty,
                    "不支持的数据类型: " + cellData.getType());

        } catch (Exception e) {
            throw new ExcelDataConvertException(rowIndex, columnIndex, cellData, contentProperty,
                    "日期转换失败: " + e.getMessage(), e);
        }
    }

//    @Override
//    public ReadCellData<?> convertToExcelData(Date value, ExcelContentProperty contentProperty,
//                                              GlobalConfiguration globalConfiguration) {
//        if (value == null) {
//            return new ReadCellData<>("");
//        }
//        // 统一转换为 yyyy-MM-dd HH:mm:ss 格式
//        return new ReadCellData<>(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(value));
//    }
}