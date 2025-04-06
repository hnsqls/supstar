package com.ls.supstar.model.dto;

import lombok.Data;
import java.util.HashMap;
import java.util.Map;

@Data
public class MonthlyAttendanceDTO {
    private String employeeId;      // 员工编号
    private String employeeName;    // 员工姓名
    private String department;      // 所属组织
    private Map<Integer, String> attendanceRecords;  // 每天的考勤记录，key为日期(1-31)，value为考勤状态
    private Map<Integer, String> workHours;          // 每天的工作时长，key为日期(1-31)，value为工作时长
    
    public MonthlyAttendanceDTO() {
        this.attendanceRecords = new HashMap<>();
        this.workHours = new HashMap<>();
    }
}