// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** listInterceptors GET /api/debug/interceptors */
export async function listInterceptorsUsingGet(options?: { [key: string]: any }) {
  return request<string[]>('/api/debug/interceptors', {
    method: 'GET',
    ...(options || {}),
  });
}
