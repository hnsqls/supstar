package com.ls.supstar.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ls.supstar.mapper.AttendanceRowMapper;
import com.ls.supstar.mapper.AttendanceSummaryMapper;
import com.ls.supstar.model.entity.AttendanceRaw;
import com.ls.supstar.model.entity.AttendanceSummary;
import com.ls.supstar.service.AttendanceRowService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AttendanceRowServiceImpl extends ServiceImpl<AttendanceRowMapper, AttendanceRaw>
        implements AttendanceRowService {

    @Resource
    private AttendanceRowMapper attendanceRawMapper;


    @Transactional
    public void cleanAndSave(List<AttendanceRaw> rawData) {
        // 1. 数据校验 主要是 用户id 以及打卡日志不能为空
        List<AttendanceRaw> validData = validateData(rawData);

        // 2. 数据转换   去除开头的‘,’
        List<AttendanceRaw> transformedData = transformData(validData);

        // 3. 筛选每天最早和最晚打卡记录
        List<AttendanceRaw> filteredData = filterEarliestAndLatestRecords(transformedData);

        // 4. 保存到数据库
//        if (!filteredData.isEmpty()) {
////            attendanceRawMapper.insertBatch(filteredData); 自定义sql版本
//            this.saveBatch(filteredData);
//        }


        // 批量插入忽略重复值
        // 4. 批量插入忽略重复
        if (!filteredData.isEmpty()) {
            try {
                attendanceRawMapper.insertBatchIgnoreDuplicate(filteredData);
            } catch (DuplicateKeyException e) {
                // MySQL抛出的重复键异常
                log.warn("检测到重复数据，已自动跳过");
            } catch (DataIntegrityViolationException e) {
                // 其他数据库可能抛出的异常
                if (e.getMessage().contains("unique constraint") || e.getMessage().contains("Duplicate entry")) {
                    log.warn("检测到重复数据，已自动跳过");
                } else {
                    throw e;
                }
            }
        }
    }

    /**
     * 筛选每天最早和最晚打卡记录
     */
    private List<AttendanceRaw> filterEarliestAndLatestRecords(List<AttendanceRaw> data) {
        // 按员工ID和日期分组
        Map<String, Map<LocalDate, List<AttendanceRaw>>> groupedByEmployeeAndDate = data.stream()
                .collect(Collectors.groupingBy(
                        AttendanceRaw::getEmployeeId,
                        Collectors.groupingBy(record ->
                                record.getRecordDate().toInstant()  // 先转Instant
                                        .atZone(ZoneId.systemDefault()) // 转ZonedDateTime
                                        .toLocalDate() // 最后取LocalDate
                        )
                ));

        List<AttendanceRaw> result = new ArrayList<>();

        groupedByEmployeeAndDate.forEach((employeeId, dateMap) -> {
            dateMap.forEach((date, records) -> {
                if (!records.isEmpty()) {
                    // 按打卡时间排序
                    records.sort(Comparator.comparing(AttendanceRaw::getRecordDate));

                    // 添加最早记录
                    result.add(records.get(0));

                    // 如果有多条记录，添加最晚记录（排除与最早记录相同的情况）
                    if (records.size() > 1 && !records.get(records.size() - 1).getRecordDate()
                            .equals(records.get(0).getRecordDate())) {
                        result.add(records.get(records.size() - 1));
                    }
                }
            });
        });

        return result;
    }

    // 保留原有的validateData和transformData方法
    private List<AttendanceRaw> validateData(List<AttendanceRaw> rawData) {
        return rawData.stream()
                .filter(record ->
                        StringUtils.isNotBlank(record.getEmployeeId()) &&
                                record.getRecordDate() != null
                )
                .collect(Collectors.toList());
    }

    private List<AttendanceRaw> transformData(List<AttendanceRaw> validData) {
        return validData.stream()
                .peek(record -> {
                    if (record.getEmployeeId().startsWith("'")) {
                        record.setEmployeeId(record.getEmployeeId().substring(1));
                    }

                    if (record.getDepartment() != null) {
                        record.setDepartment(
                                record.getDepartment()
                                        .replace("SUPSTAR/", "")
                                        .trim()
                        );
                    }
                })
                .collect(Collectors.toList());
    }
}