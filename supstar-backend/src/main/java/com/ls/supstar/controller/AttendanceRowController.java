package com.ls.supstar.controller;

import com.alibaba.excel.EasyExcel;
import com.ls.supstar.common.BaseResponse;
import com.ls.supstar.common.ResultUtils;
import com.ls.supstar.custom.CustomDateConverter;
import com.ls.supstar.model.entity.AttendanceRaw;
import com.ls.supstar.service.AttendanceRowService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
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
    public BaseResponse<String> importExcel(@RequestParam("file") MultipartFile file) {
        try {
            // 1. 使用EasyExcel读取数据
            List<AttendanceRaw> rawData = EasyExcel.read(file.getInputStream())
                    .head(AttendanceRaw.class)
                    .sheet("原始考勤记录报表")
                    .registerConverter(new CustomDateConverter())
                    .doReadSync();

            // 2. 清洗并保存
            attendanceRowService.cleanAndSave(rawData);

            return ResultUtils.success("数据导入成功");
        } catch (Exception e) {
            throw  new RuntimeException(e);
        }
    }

}
