import { UploadOutlined, DownloadOutlined } from '@ant-design/icons';
import { Button, DatePicker, Table, Card, Space, message, Input, Modal } from 'antd';
import React, { useState, useRef } from 'react';
import * as XLSX from 'xlsx';
import moment from 'moment';
import { PageContainer } from '@ant-design/pro-components';
import { importExcelUsingPost, exportExcelUsingPost, exportMonthlyAttendance } from '@/services/SupStar/attendanceRowController';


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
  const [downloadLoading, setDownloadLoading] = useState(false);
  const [selectedMonth, setSelectedMonth] = useState<string | null>(null);
  const [searchName, setSearchName] = useState<string>('');
  const [recordIds, setRecordIds] = useState<number[]>([]);
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
      
      // 保存后端返回的ID集合
      if (apiRes.data && Array.isArray(apiRes.data)) {
        setRecordIds(apiRes.data);
      }
      
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

  // 处理下载 - 使用axios
  const handleDownload = async () => {
    if (recordIds.length === 0) {
      message.warning('没有可下载的数据，请先上传文件');
      return;
    }
    
    setDownloadLoading(true);
    try {
      // 使用服务层方法发送请求，明确设置Accept头
      const response = await exportExcelUsingPost(recordIds, {
        responseType: 'blob',
        headers: {
          'Accept': 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
        }
      });

      // 检查响应类型
      if (!(response instanceof Blob)) {
        throw new Error('响应不是文件类型');
      }

      // 创建下载链接
      const url = window.URL.createObjectURL(response);
      const link = document.createElement('a');
      link.href = url;
      link.download = `考勤数据_${moment().format('YYYY-MM-DD')}.xlsx`;
      document.body.appendChild(link);
      link.click();
      
      // 清理
      setTimeout(() => {
        document.body.removeChild(link);
        window.URL.revokeObjectURL(url);
      }, 100);
      
      message.success('下载成功');
    } catch (error: any) {
      console.error('下载失败:', error);
      message.error(error.message || '下载失败');
    } finally {
      setDownloadLoading(false);
    }
  };

  // 处理月份变化
  const handleMonthChange = (date: moment.Moment | null) => {
    if (date) {
      const monthStr = date.format('YYYY-MM');
      setSelectedMonth(monthStr);
      
      // 筛选当月数据
      const filtered = data.filter(record => record.date.startsWith(monthStr));
      setFilteredData(filtered);
    } else {
      setSelectedMonth(null);
      setFilteredData(data);
    }
  };

  // 处理姓名搜索
  const handleNameSearch = (value: string) => {
    setSearchName(value);
    
    let filtered = data;
    
    // 先按月份筛选
    if (selectedMonth) {
      filtered = filtered.filter(record => record.date.startsWith(selectedMonth));
    }
    
    // 再按姓名筛选
    if (value) {
      filtered = filtered.filter(record => record.name.includes(value));
    }
    
    setFilteredData(filtered);
  };

  // 表格列定义
  const columns = [
    {
      title: '员工ID',
      dataIndex: 'id',
      key: 'id',
    },
    {
      title: '姓名',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: '日期',
      dataIndex: 'date',
      key: 'date',
      sorter: (a: AttendanceRecord, b: AttendanceRecord) => a.date.localeCompare(b.date),
    },
    {
      title: '签到时间',
      dataIndex: 'checkIn',
      key: 'checkIn',
    },
    {
      title: '签退时间',
      dataIndex: 'checkOut',
      key: 'checkOut',
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (status: string) => {
        const colorMap: Record<string, string> = {
          '正常': 'green',
          '迟到': 'orange',
          '早退': 'gold',
          '缺勤': 'red',
        };
        return <span style={{ color: colorMap[status] || 'black' }}>{status}</span>;
      },
      filters: [
        { text: '正常', value: '正常' },
        { text: '迟到', value: '迟到' },
        { text: '早退', value: '早退' },
        { text: '缺勤', value: '缺勤' },
      ],
      onFilter: (value: string, record: AttendanceRecord) => record.status === value,
    },
  ];

  // 新增处理考勤汇总下载函数
  const handleSummaryDownload = async () => {
    // 打开月份选择器
    const picker = await showMonthPicker();
    if (!picker) return;  // 用户取消选择
    
    const { year, month } = picker;
    setDownloadLoading(true);
    try {
      const response = await exportMonthlyAttendance(
        { year, month },
        { responseType: 'blob' }
      );

      const url = window.URL.createObjectURL(new Blob([response]));
      const link = document.createElement('a');
      link.href = url;
      link.download = `考勤月度汇总_${year}年${month}月.xlsx`;
      document.body.appendChild(link);
      link.click();
      
      setTimeout(() => {
        document.body.removeChild(link);
        window.URL.revokeObjectURL(url);
      }, 100);
      
      message.success('汇总下载成功');
    } catch (error: any) {
      console.error('汇总下载失败:', error);
      message.error(error.message || '汇总下载失败');
    } finally {
      setDownloadLoading(false);
    }
  };

  // 新增月份选择器函数
  const showMonthPicker = () => {
    return new Promise<{ year: number; month: number } | null>((resolve) => {
      let selectedDate: moment.Moment | null = null;
      
      Modal.confirm({
        title: '选择月份',
        content: (
          <DatePicker.MonthPicker
            style={{ width: '100%' }}
            onChange={(date) => {
              selectedDate = date;
            }}
          />
        ),
        onOk: () => {
          if (!selectedDate) {
            message.warning('请选择月份');
            return Promise.reject();
          }
          resolve({
            year: selectedDate.year(),
            month: selectedDate.month() + 1  // moment月份从0开始，需要+1
          });
        },
        onCancel: () => {
          resolve(null);
        },
      });
    });
  };

  return (
    <PageContainer>
      <Card title="考勤分析" bordered={false}>
        <Space size="large" style={{ marginBottom: 24 }}>
          {/* 上传按钮 */}
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
            disabled={recordIds.length === 0}
            loading={downloadLoading}
          >
            下载清洗后数据
          </Button>
          
          <Button 
            icon={<DownloadOutlined />} 
            onClick={handleSummaryDownload}
            loading={downloadLoading}
            type="primary"
          >
            下载考勤汇总
          </Button>
          
          <MonthPicker 
            placeholder="选择月份查询" 
            onChange={handleMonthChange}
            style={{ width: 200 }}
          />
          
          <Input.Search
            placeholder="搜索员工姓名"
            onSearch={handleNameSearch}
            style={{ width: 200 }}
            allowClear
          />
        </Space>
        
        <Table 
          columns={columns} 
          dataSource={filteredData} 
          rowKey="id"
          pagination={{ pageSize: 10 }}
          loading={loading}
        />
      </Card>
    </PageContainer>
  );
};

export default Attendance;