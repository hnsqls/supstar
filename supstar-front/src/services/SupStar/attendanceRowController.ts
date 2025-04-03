// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** exportExcel POST /api/attendanceRow/download */
export async function exportExcelUsingPost(
  body: number[],
  options?: { [key: string]: any } & { responseType?: 'blob' }
) {
  return request<Blob>('/api/attendanceRow/download', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    responseType: options?.responseType || 'json',
    ...(options || {}),
  });
}

/** importExcel POST /api/attendanceRow/import */
export async function importExcelUsingPost(
  body: {},
  file?: File,
  options?: { [key: string]: any },
) {
  const formData = new FormData();

  if (file) {
    formData.append('file', file);
  }

  Object.keys(body).forEach((ele) => {
    const item = (body as any)[ele];

    if (item !== undefined && item !== null) {
      if (typeof item === 'object' && !(item instanceof File)) {
        if (item instanceof Array) {
          item.forEach((f) => formData.append(ele, f || ''));
        } else {
          formData.append(ele, JSON.stringify(item));
        }
      } else {
        formData.append(ele, item);
      }
    }
  });

  return request<API.BaseResponseListLong_>('/api/attendanceRow/import', {
    method: 'POST',
    data: formData,
    requestType: 'form',
    ...(options || {}),
  });
}
