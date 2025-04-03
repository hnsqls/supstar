package com.ls.supstar.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ls.supstar.model.entity.AttendanceRaw;
import com.ls.supstar.model.entity.User;

import java.util.List;

public interface AttendanceRowService extends IService<AttendanceRaw> {
    /**
     * 清洗数据 并且保存到数据库
     * @param rawData
     */
    List<Long > cleanAndSave(List<AttendanceRaw> rawData);

    /**
     * 根据ids 获取清洗后的数据
     * @param ids
     * @return
     */
    List<AttendanceRaw> getAttendanceRawByIds(List<Long> ids);
}
