package com.ls.supstar.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ls.supstar.model.entity.AttendanceRaw;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AttendanceRowMapper extends BaseMapper<AttendanceRaw> {

    // 保留原有的批量插入方法
    @Insert("<script>" +
            "INSERT INTO attendance_raw " +
            "(employee_id, employee_name, department, record_date, attendance_status, " +
            "attendance_point, custom_name, data_source, process_type, temperature, temperature_abnormal) " +
            "VALUES " +
            "<foreach collection='list' item='item' separator=','> " +
            "(#{item.employeeId}, #{item.employeeName}, #{item.department}, #{item.recordDate}, " +
            "#{item.attendanceStatus}, #{item.attendancePoint}, #{item.customName}, #{item.dataSource}, " +
            "#{item.processType}, #{item.temperature}, #{item.temperatureAbnormal})" +
            "</foreach>" +
            "</script>")
    void insertBatch(@Param("list") List<AttendanceRaw> records);

    // 保留原有的批量插入忽略重复记录方法
    int insertBatchIgnoreDuplicate(@Param("list") List<AttendanceRaw> records);

    // 新增查询已存在记录ID的方法
    List<Long> selectExistingIds(@Param("list") List<AttendanceRaw> records);
}




