// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** downloadMonthlyAttendance GET /api/attendanceSummary/downloadMonthlyAttendance */
export async function downloadMonthlyAttendanceUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.downloadMonthlyAttendanceUsingGETParams,
  options?: { [key: string]: any },
) {
  return request<any>('/api/attendanceSummary/downloadMonthlyAttendance', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** exportMonthlyAttendance GET /api/attendanceSummary/exportMonthlyAttendance */
export async function exportMonthlyAttendanceUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.exportMonthlyAttendanceUsingGETParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseString_>('/api/attendanceSummary/exportMonthlyAttendance', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}
