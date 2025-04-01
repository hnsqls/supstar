package com.ls.supstar.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ls.supstar.model.entity.AttendanceRaw;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
* @author 26611
* @description 针对表【user(用户)】的数据库操作Mapper
* @createDate 2025-03-09 15:55:40
* @Entity com.ls.supstar.model.entity.User
*/
public interface AttendanceRowMapper extends BaseMapper<AttendanceRaw> {


    // 批量插入（MySQL语法）
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



    /**
     * 批量插入忽略重复记录（MySQL语法）
     * 注意：需要表中有唯一约束(如UK(employee_id, record_date))
     *
     * @param records 考勤记录列表
     */
//    @Insert("<script>" +
//            "INSERT IGNORE INTO attendance_raw " +
//            "(employee_id, employee_name, department, record_date, attendance_status, " +
//            "attendance_point, custom_name, data_source, process_type, temperature, temperature_abnormal) " +
//            "VALUES " +
//            "<foreach collection='list' item='item' separator=','> " +
//            "(#{item.employeeId}, #{item.employeeName}, #{item.department}, #{item.recordDate}, " +
//            "#{item.attendanceStatus}, #{item.attendancePoint}, #{item.customName}, #{item.dataSource}, " +
//            "#{item.processType}, #{item.temperature}, #{item.temperatureAbnormal})" +
//            "</foreach>" +
//            "</script>")
    int insertBatchIgnoreDuplicate(@Param("list") List<AttendanceRaw> records);
}




