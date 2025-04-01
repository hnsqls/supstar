create  database  supstar;

use supstar;
-- 用户表
create table if not exists user
(
    id           bigint auto_increment comment 'id' primary key,
    userAccount  varchar(256)                           not null comment '账号',
    userPassword varchar(512)                           not null comment '密码',
    userName     varchar(256)                           null comment '用户昵称',
    userAvatar   varchar(1024)                          null comment '用户头像',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user/admin',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除',
    index idx_userAccount (userAccount)
) comment '用户' collate = utf8mb4_unicode_ci;


-- 考勤表
-- 原始考勤记录表（存储从Excel导入的原始考勤数据）
CREATE TABLE attendance_raw (
                                id                   BIGINT AUTO_INCREMENT COMMENT '主键ID'
                                    PRIMARY KEY,
                                employee_id          VARCHAR(20)  NOT NULL                COMMENT '员工编号',
                                employee_name        VARCHAR(50)  NOT NULL                COMMENT '员工姓名',
                                department           VARCHAR(100) NOT NULL                COMMENT '所属部门',
                                record_date          DATETIME     NOT NULL                COMMENT '考勤记录时间',
                                attendance_status    VARCHAR(20)                          COMMENT '考勤状态',
                                attendance_point     VARCHAR(100)                         COMMENT '考勤点',
                                custom_name          VARCHAR(100)                         COMMENT '自定义名称',
                                data_source          VARCHAR(50)                          COMMENT '数据来源',
                                process_type         VARCHAR(50)                          COMMENT '处理类型',
                                temperature          VARCHAR(10)                          COMMENT '体温',
                                temperature_abnormal VARCHAR(10)                          COMMENT '体温异常',
                                create_time          DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                update_time          DATETIME     DEFAULT CURRENT_TIMESTAMP
                                    ON UPDATE CURRENT_TIMESTAMP      COMMENT '更新时间',

                                CONSTRAINT uk_employee_record
                                    UNIQUE (employee_id, record_date),

                                INDEX idx_date (record_date),
                                INDEX idx_employee_date (employee_id, record_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='原始考勤记录表';

-- 考勤统计表
-- 考勤统计表（存储按员工+日期聚合后的考勤统计数据）
CREATE TABLE `attendance_summary` (
                                      `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                      `employee_id` varchar(20) NOT NULL COMMENT '员工编号',
                                      `employee_name` varchar(50) NOT NULL COMMENT '员工姓名',
                                      `department` varchar(100) NOT NULL COMMENT '所属部门',
                                      `work_date` date NOT NULL COMMENT '工作日期',
                                      `day_of_week` varchar(10) NOT NULL COMMENT '星期几',
                                      `first_check_in` time DEFAULT NULL COMMENT '首次打卡时间',
                                      `last_check_out` time DEFAULT NULL COMMENT '最后打卡时间',
                                      `work_hours` decimal(5,2) DEFAULT NULL COMMENT '工作时长(小时)',
                                      `status` varchar(20) DEFAULT 'NORMAL' COMMENT '考勤状态(NORMAL正常, LATE迟到, LEAVE_EARLY早退, ABSENT缺勤)',
                                      `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                      `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                      PRIMARY KEY (`id`),
                                      UNIQUE KEY `uk_employee_date` (`employee_id`, `work_date`),
                                      KEY `idx_date` (`work_date`),
                                      KEY `idx_employee` (`employee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考勤统计表';