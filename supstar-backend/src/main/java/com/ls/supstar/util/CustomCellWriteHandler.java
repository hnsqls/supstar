package com.ls.supstar.util;

import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.handler.context.CellWriteHandlerContext;
import com.alibaba.excel.write.handler.context.SheetWriteHandlerContext;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * 自定义单元格样式处理器
 * 用于设置表头单元格的样式（加粗加黑）和数据单元格的样式
 */
public class CustomCellWriteHandler implements CellWriteHandler, SheetWriteHandler {

    @Override
    public void afterCellCreate(CellWriteHandlerContext context) {
        Cell cell = context.getCell();
        Workbook workbook = context.getWriteWorkbookHolder().getWorkbook();
        
        if (context.getRowIndex() < 2) {
            // 表头样式
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.BLACK.getIndex());
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            
            // 为表头添加边框
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            
            cell.setCellStyle(headerStyle);
        } else {
            // 数据单元格样式
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            
            // 添加边框
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);
            
            // 设置背景色
            int employeeIndex = (context.getRowIndex() - 2) / 2;
            if (employeeIndex % 2 == 0) {
                dataStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
            } else {
                dataStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            }
            dataStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            
            cell.setCellStyle(dataStyle);
        }
    }
    
    @Override
    public void afterSheetCreate(SheetWriteHandlerContext context) {
        Sheet sheet = context.getWriteSheetHolder().getSheet();
        int rowCount = sheet.getLastRowNum() + 1;
        
        // 设置"所属组织"列的宽度（第3列，索引为2）
        // 1厘米约等于4.7个字符宽度，40厘米约等于188个字符
        sheet.setColumnWidth(2, (int)(188 * 256));
        
        // 从第3行开始（索引为2），每隔2行合并员工信息单元格
        for (int i = 2; i < rowCount; i += 2) {
            // 确保有下一行可以合并
            if (i + 1 < rowCount) {
                // 只合并前三列（员工信息）
                for (int col = 0; col < 3; col++) {
                    sheet.addMergedRegion(new CellRangeAddress(i, i + 1, col, col));
                }
            }
        }
    }
}