package com.ls.supstar.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ls.supstar.mapper.AttendanceSummaryMapper;
import com.ls.supstar.mapper.UserMapper;
import com.ls.supstar.model.entity.AttendanceSummary;
import com.ls.supstar.model.entity.User;
import com.ls.supstar.service.AttendanceSummaryService;
import org.springframework.stereotype.Service;

/**
 * 考勤数据分析
 */
@Service
public class AttendanceSummaryServiceImpl extends ServiceImpl<AttendanceSummaryMapper, AttendanceSummary>
        implements AttendanceSummaryService {
}
