package com.ls.supstar.model.entity;


import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.*;
import com.ls.supstar.custom.CustomDateConverter;
import lombok.*;

import java.util.Date;

@Data
@TableName("attendance_raw")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceRaw {
    @TableId(type = IdType.AUTO)
//    @ExcelProperty(value = "ID", converter = CustomLongConverter.class)
    private Long id;

    @TableField("employee_id")
    @ExcelProperty(value = "编号")
    private String employeeId;

    @TableField("employee_name")
    @ExcelProperty(value = "姓名")
    private String employeeName;
    @ExcelProperty(value = "部门")
    private String department;

    @TableField("record_date")
    @ExcelProperty(value = "日期", converter = CustomDateConverter.class)
    private Date recordDate;

    @TableField("attendance_status")
    @ExcelProperty(value = "考勤状态")
    private String attendanceStatus;

    @TableField("attendance_point")
    @ExcelProperty(value = "考勤点")
    private String attendancePoint;

    @TableField("custom_name")
    @ExcelProperty(value = "自定义名称")
    private String customName;

    @TableField("data_source")
    @ExcelProperty(value = "数据来源")
    private String dataSource;

    @TableField("process_type")
    @ExcelProperty(value = "处理类型")
    private String processType;

    @ExcelProperty(value = "体温")
    private String temperature;

    @TableField("temperature_abnormal")
    @ExcelProperty(value = "体温异常")
    private String temperatureAbnormal;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}