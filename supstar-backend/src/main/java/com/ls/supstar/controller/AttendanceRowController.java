package com.ls.supstar.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.ls.supstar.common.BaseResponse;
import com.ls.supstar.common.ErrorCode;
import com.ls.supstar.common.ResultUtils;
import com.ls.supstar.custom.CustomDateConverter;
import com.ls.supstar.model.entity.AttendanceRaw;
import com.ls.supstar.service.AttendanceRowService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * 考勤-原始数据处理
 */

@RestController
@RequestMapping("/attendanceRow")
public class AttendanceRowController {

    @Resource
    private AttendanceRowService attendanceRowService;
    // 考勤数据清洗


    @PostMapping("/import")
    public BaseResponse<List<Long>> importExcel(@RequestParam("file") MultipartFile file) {
        try {
            // 1. 使用EasyExcel读取数据
            List<AttendanceRaw> rawData = EasyExcel.read(file.getInputStream())
                    .head(AttendanceRaw.class)
                    .sheet("原始考勤记录报表")
                    .registerConverter(new CustomDateConverter())
                    .doReadSync();

            // 2. 清洗并保存
           List<Long> listIds =  attendanceRowService.cleanAndSave(rawData);

            return ResultUtils.success(listIds);
        } catch (Exception e) {
            throw  new RuntimeException(e);
        }
    }


    /**
     * 下载清洗后的文件，Excel
     * @param ids 考勤记录ID集合
     */
    @PostMapping("/download")
    public void exportExcel(@RequestBody List<Long> ids, HttpServletResponse response) {
        try {
            // 1. 查询数据
            List<AttendanceRaw> attendanceRawList = attendanceRowService.listByIds(ids);

            // 2. 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("考勤清洗数据", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");

            // 3. 直接写入响应流
            EasyExcel.write(response.getOutputStream(), AttendanceRaw.class)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .registerConverter(new CustomDateConverter())
                    .excludeColumnFieldNames(Arrays.asList("id","createTime", "updateTime"))
                    .sheet("考勤清洗数据")
                    .doWrite(attendanceRawList);
            
        } catch (Exception e) {
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try {
                response.getWriter().write("{\"message\":\"导出Excel失败: " + e.getMessage() + "\"}");
            } catch (IOException ex) {
                // 忽略
            }
        }
    }

    /**
     * 本地测试写Excel文件
     * @param response
     */
    @GetMapping("/testExport")
    public BaseResponse<String> testExportToLocal() {
        try {
            // 1. 查询所有数据
            List<AttendanceRaw> attendanceRawList = attendanceRowService.list();
            
            if(attendanceRawList.isEmpty()) {
                return ResultUtils.error(ErrorCode.PARAMS_ERROR, "没有可导出的数据");
            }
            
            // 2. 设置本地文件路径
            String fileName = "测试考勤数据_" + System.currentTimeMillis() + ".xlsx";
            String filePath = "e:/temp/" + fileName;
            
            System.out.println("开始导出数据到: " + filePath);
            System.out.println("导出数据条数: " + attendanceRawList.size());
            
            // 3. 写入本地文件
            EasyExcel.write(filePath, AttendanceRaw.class)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .registerConverter(new CustomDateConverter())
                    .excludeColumnFieldNames(Arrays.asList("id","createTime", "updateTime"))
                    .sheet("测试考勤数据")
                    .doWrite(attendanceRawList);
            
            System.out.println("文件导出成功: " + filePath);
            return ResultUtils.success(filePath);
            
        } catch (Exception e) {
            System.err.println("文件导出失败: " + e.getMessage());
            return ResultUtils.error(ErrorCode.OPERATION_ERROR, "生成测试文件失败: " + e.getMessage());
        }
    }
}
