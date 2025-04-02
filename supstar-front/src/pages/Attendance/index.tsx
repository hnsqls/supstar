import { UploadOutlined, DownloadOutlined } from '@ant-design/icons';
import { Button, Upload, DatePicker, Table, Card, Space, message, Input } from 'antd';
import React, { useState, useRef } from 'react';
import * as XLSX from 'xlsx';
import moment from 'moment';
import { PageContainer } from '@ant-design/pro-components';
import { importExcelUsingPost } from '@/services/SupStar/attendanceRowController';

const { MonthPicker } = DatePicker;

interface AttendanceRecord {
  id: string;
  name: string;
  date: string;
  checkIn: string;
  checkOut: string;
  status: '正常' | '迟到' | '早退' | '缺勤';
}

const Attendance: React.FC = () => {
  const [data, setData] = useState<AttendanceRecord[]>([]);
  const [filteredData, setFilteredData] = useState<AttendanceRecord[]>([]);
  const [loading, setLoading] = useState(false);
  const [selectedMonth, setSelectedMonth] = useState<string | null>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);

  // 处理文件选择
  const handleFileSelect = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      handleUpload(file);
    }
    // 重置input值，允许重复选择同一文件
    if (e.target) {
      e.target.value = '';
    }
  };

  // 处理上传
  const handleUpload = async (file: File) => {
    if (!file) return;
    
    setLoading(true);
    try {
      // 1. 发送到后端
      const apiRes = await importExcelUsingPost({}, file);
      if (apiRes.code !== 0) throw new Error(apiRes.message);
      
      // 2. 前端处理数据展示
      const excelData = await processExcelData(file);
      setData(excelData);
      setFilteredData(excelData);
      
      message.success('文件上传成功');
    } catch (error: any) {
      console.error('上传出错:', error);
      message.error(error.message || '文件上传失败');
    } finally {
      setLoading(false);
    }
  };

  // 处理Excel数据
  const processExcelData = async (file: File): Promise<AttendanceRecord[]> => {
    const data = await file.arrayBuffer();
    const workbook = XLSX.read(data);
    const worksheet = workbook.Sheets[workbook.SheetNames[0]];
    const jsonData = XLSX.utils.sheet_to_json<AttendanceRecord>(worksheet);
    
    return jsonData.map(item => ({
      ...item,
      status: calculateAttendanceStatus(item.checkIn, item.checkOut)
    }));
  };

  // 计算考勤状态
  const calculateAttendanceStatus = (checkIn: string, checkOut: string) => {
    if (!checkIn || !checkOut) return '缺勤';
    
    const isLate = moment(checkIn, 'HH:mm').isAfter(moment('09:00', 'HH:mm'));
    const isEarly = moment(checkOut, 'HH:mm').isBefore(moment('18:00', 'HH:mm'));
    
    if (isLate) return '迟到';
    if (isEarly) return '早退';
    return '正常';
  };

  // 其他保持不变的方法...
  const handleDownload = () => { /*...*/ };
  const handleMonthChange = (date: moment.Moment | null) => { /*...*/ };

  return (
    <PageContainer>
      <Card title="考勤分析" bordered={false}>
        <Space size="large" style={{ marginBottom: 24 }}>
          {/* 新增的上传按钮 */}
          <Button 
            icon={<UploadOutlined />} 
            loading={loading}
            onClick={() => fileInputRef.current?.click()}
          >
            上传考勤Excel
          </Button>
          
          {/* 隐藏的文件input */}
          <input
            type="file"
            ref={fileInputRef}
            style={{ display: 'none' }}
            accept=".xlsx,.xls"
            onChange={handleFileSelect}
          />
          
          <Button 
            icon={<DownloadOutlined />} 
            onClick={handleDownload}
            disabled={data.length === 0}
          >
            下载清洗后数据
          </Button>
          
          <MonthPicker 
            placeholder="选择月份查询" 
            onChange={handleMonthChange}
            style={{ width: 200 }}
          />
        </Space>
        
        {/* 表格保持不变... */}
      </Card>
    </PageContainer>
  );
};

export default Attendance;