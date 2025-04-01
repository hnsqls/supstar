package com.ls.supstar.service;

import com.ls.supstar.model.entity.AttendanceRaw;

import java.util.List;

public interface AttendanceRowService {
    /**
     * 清洗数据 并且保存到数据库
     * @param rawData
     */
    void cleanAndSave(List<AttendanceRaw> rawData);
}
