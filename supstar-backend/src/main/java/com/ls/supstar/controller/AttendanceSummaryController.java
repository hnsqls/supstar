package com.ls.supstar.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.ls.supstar.common.BaseResponse;
import com.ls.supstar.common.ErrorCode;
import com.ls.supstar.common.ResultUtils;
import com.ls.supstar.model.dto.MonthlyAttendanceDTO;
import com.ls.supstar.model.entity.AttendanceRaw;
import com.ls.supstar.service.AttendanceRowService;
import com.ls.supstar.util.CustomCellWriteHandler;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 考勤-考勤分析
 */

@RestController
@RequestMapping("/attendanceSummary")
public class AttendanceSummaryController {

    // 考勤数据清洗
    
    @Resource
    private AttendanceRowService attendanceRawService;
    
    /**
     * 按月导出考勤数据到Excel（从原始打卡记录生成）
     * @param year 年份
     * @param month 月份
     * @return 文件路径
     */
    @GetMapping("/exportMonthlyAttendance")
    public BaseResponse<String> exportMonthlyAttendance(@RequestParam int year, @RequestParam int month) {
        try {
            // 1. 获取月度考勤数据
            List<MonthlyAttendanceDTO> monthlyData = getMonthlyAttendanceDataFromRaw(year, month);
            
            if(monthlyData.isEmpty()) {
                return ResultUtils.error(ErrorCode.PARAMS_ERROR, "该月份没有考勤数据");
            }
            
            // 2. 设置本地文件路径
            String fileName = year + "年" + month + "月考勤数据_" + System.currentTimeMillis() + ".xlsx";
            // 确保目录存在
            File dir = new File("e:/temp/");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String filePath = "e:/temp/" + fileName;
            
            System.out.println("开始导出" + year + "年" + month + "月数据到: " + filePath);
            
            // 3. 直接写入Excel，不使用EasyExcel的表头功能
            try (ExcelWriter excelWriter = EasyExcel.write(filePath)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .registerWriteHandler(new CustomCellWriteHandler()) // 添加自定义单元格样式处理器
                    .build()) {
                WriteSheet writeSheet = EasyExcel.writerSheet(month + "月考勤数据").build();
                
                // 创建完整的数据列表（包含表头和数据）
                List<List<Object>> allData = createCompleteDataList(monthlyData, year, month);
                
                // 写入所有数据
                excelWriter.write(allData, writeSheet);
            }
            
            System.out.println("文件导出成功: " + filePath);
            return ResultUtils.success(filePath);
            
        } catch (Exception e) {
            System.err.println("文件导出失败: " + e.getMessage());
            e.printStackTrace();
            return ResultUtils.error(ErrorCode.OPERATION_ERROR, "生成月度考勤数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 创建完整的数据列表（包含表头和数据）
     */
    private List<List<Object>> createCompleteDataList(List<MonthlyAttendanceDTO> monthlyData, int year, int month) {
        List<List<Object>> allData = new ArrayList<>();
        
        // 计算当月天数
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, 1); // 月份从0开始
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        
        // 第一行：月份和日期
        List<Object> row1 = new ArrayList<>();
        row1.add("月份");
        row1.add(month);
        row1.add("日期");
        
        // 添加每一天的日期
        for (int i = 1; i <= daysInMonth; i++) {
            row1.add(month + "月" + i + "日");
        }
        allData.add(row1);
        
        // 第二行：员工信息和星期
        List<Object> row2 = new ArrayList<>();
        row2.add("员工编号");
        row2.add("员工姓名");
        row2.add("所属组织");
        
        // 添加每一天的星期
        cal.set(year, month - 1, 1);
        for (int i = 1; i <= daysInMonth; i++) {
            cal.set(Calendar.DAY_OF_MONTH, i);
            String weekDay = getWeekDay(cal.get(Calendar.DAY_OF_WEEK));
            row2.add(weekDay);
        }
        allData.add(row2);
        
        // 按员工编号排序
        monthlyData.sort(Comparator.comparing(MonthlyAttendanceDTO::getEmployeeId));
        
        // 添加员工数据
        for (MonthlyAttendanceDTO dto : monthlyData) {
            // 考勤状态行
            List<Object> attendanceRow = new ArrayList<>();
            attendanceRow.add(dto.getEmployeeId());
            attendanceRow.add(dto.getEmployeeName());
            attendanceRow.add(dto.getDepartment());
            
            // 添加每天的考勤状态
            for (int i = 1; i <= daysInMonth; i++) {
                String attendance = dto.getAttendanceRecords().getOrDefault(i, "");
                attendanceRow.add(attendance);
            }
            allData.add(attendanceRow);
            
            // 工时行
            List<Object> workHoursRow = new ArrayList<>();
            workHoursRow.add("");  // 员工编号占位
            workHoursRow.add("");  // 员工姓名占位
            workHoursRow.add("");  // 所属组织占位
            
            // 添加每天的工时
            for (int i = 1; i <= daysInMonth; i++) {
                String workHour = dto.getWorkHours().getOrDefault(i, "");
                workHoursRow.add(workHour);
            }
            allData.add(workHoursRow);
        }
        
        return allData;
    }
    
    /**
     * 从原始打卡记录获取月度考勤数据
     */
    private List<MonthlyAttendanceDTO> getMonthlyAttendanceDataFromRaw(int year, int month) {
        // 1. 计算月份的起止时间
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1, 0, 0, 0); // 月份从0开始，所以要减1
        Date startDate = calendar.getTime();
        
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.SECOND, -1);
        Date endDate = calendar.getTime();
        
        // 2. 查询原始打卡记录
        List<AttendanceRaw> rawRecords = attendanceRawService.getByDateRange(startDate, endDate);
        
        if (rawRecords.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 3. 按员工ID分组
        Map<String, List<AttendanceRaw>> groupedByEmployee = rawRecords.stream()
                .collect(Collectors.groupingBy(AttendanceRaw::getEmployeeId));
        
        // 4. 转换为DTO
        List<MonthlyAttendanceDTO> result = new ArrayList<>();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        
        groupedByEmployee.forEach((employeeId, records) -> {
            MonthlyAttendanceDTO dto = new MonthlyAttendanceDTO();
            dto.setEmployeeId(employeeId);
            dto.setEmployeeName(records.get(0).getEmployeeName());
            dto.setDepartment(records.get(0).getDepartment());
            
            // 按日期分组
            Map<Integer, List<AttendanceRaw>> recordsByDay = records.stream()
                    .collect(Collectors.groupingBy(record -> {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(record.getRecordDate());
                        return cal.get(Calendar.DAY_OF_MONTH);
                    }));
            
            // 处理每天的打卡记录
            recordsByDay.forEach((day, dayRecords) -> {
                // 找出最早和最晚的打卡时间
                Date firstCheckIn = dayRecords.stream()
                        .map(AttendanceRaw::getRecordDate)
                        .min(Date::compareTo)
                        .orElse(null);
                
                Date lastCheckOut = dayRecords.stream()
                        .map(AttendanceRaw::getRecordDate)
                        .max(Date::compareTo)
                        .orElse(null);
                
                if (firstCheckIn != null && lastCheckOut != null) {
                    // 计算工作时长（毫秒）
                    long workDuration = lastCheckOut.getTime() - firstCheckIn.getTime();
                    // 转换为小时
                    double hours = workDuration / (1000.0 * 60 * 60);
                    // 格式化为保留1位小数
                    String workHours = String.format("%.1f小时", hours);
                    
                    // 设置考勤记录
                    dto.getAttendanceRecords().put(day, 
                        timeFormat.format(firstCheckIn) + "-" + timeFormat.format(lastCheckOut));
                    // 设置工作时长
                    dto.getWorkHours().put(day, workHours);
                }
            });
            
            result.add(dto);
        });
        
        return result;
    }

    /**
     * 获取星期几
     */
    private String getWeekDay(int dayOfWeek) {
        switch (dayOfWeek) {
            case Calendar.MONDAY: return "星期一";
            case Calendar.TUESDAY: return "星期二";
            case Calendar.WEDNESDAY: return "星期三";
            case Calendar.THURSDAY: return "星期四";
            case Calendar.FRIDAY: return "星期五";
            case Calendar.SATURDAY: return "星期六";
            case Calendar.SUNDAY: return "星期日";
            default: return "";
        }
    }
}
