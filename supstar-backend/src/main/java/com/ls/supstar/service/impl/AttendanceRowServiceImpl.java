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
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AttendanceRowServiceImpl extends ServiceImpl<AttendanceRowMapper, AttendanceRaw>
        implements AttendanceRowService {

    @Resource
    private AttendanceRowMapper attendanceRawMapper;


    @Transactional
    public List<Long> cleanAndSave(List<AttendanceRaw> rawData) {
        // 1. 数据校验 主要是 用户id 以及打卡日志不能为空
        List<AttendanceRaw> validData = validateData(rawData);

        // 2. 数据转换   去除开头的','
        List<AttendanceRaw> transformedData = transformData(validData);

        // 3. 筛选每天最早和最晚打卡记录
        List<AttendanceRaw> filteredData = filterEarliestAndLatestRecords(transformedData);

        if (filteredData.isEmpty()) {
            return Collections.emptyList();
        }

        // 4. 先查询已存在记录的ID
        List<Long> existingIds = attendanceRawMapper.selectExistingIds(filteredData);
        Set<Long> resultIdSet = new HashSet<>(existingIds);

        // 4. 版本1：保存到数据库--- 没有去重
//        if (!filteredData.isEmpty()) {
////            attendanceRawMapper.insertBatch(filteredData); 自定义sql版本
//            this.saveBatch(filteredData);
//        }


        // 4. 版本2：批量插入忽略重复值----对于重复的元素获取不到回显id-----使用  INSERT IGNORE INTO attendance_raw
        if (!filteredData.isEmpty()) {
            try {
                attendanceRawMapper.insertBatchIgnoreDuplicate(filteredData);

            } catch (DuplicateKeyException e) {
                // MySQL抛出的重复键异常
                log.warn("检测到重复数据，已自动跳过");
            }
        }



        // 4. 版本3：批量插入/更新并获取所有ID（包括已存在的记录） 废弃
//        if (filteredData.isEmpty()) {
//            return Collections.emptyList();
//        }
//
//        attendanceRawMapper.upsertAttendanceRawBatch(filteredData);


        // 5. 提取插入成功的 ID 并合并已存在的ID
        filteredData.stream()
                .map(AttendanceRaw::getId)
                .filter(Objects::nonNull) // 过滤掉 null 值（因为 IGNORE 可能导致部分数据未插入）
                .forEach(resultIdSet::add);
        
        return new ArrayList<>(resultIdSet);
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