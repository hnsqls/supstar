package com.ls.supstar.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.Date;
@Data
@TableName("attendance_summary")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceSummary {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("employee_id")
    private String employeeId;

    @TableField("employee_name")
    private String employeeName;

    private String department;

    @TableField("work_date")
    private Date workDate;

    @TableField("day_of_week")
    private String dayOfWeek;

    @TableField("first_check_in")
    private Time firstCheckIn;

    @TableField("last_check_out")
    private Time lastCheckOut;

    @TableField("work_hours")
    private BigDecimal workHours;

    private String status;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}