// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** register POST /api/user/login */
export async function registerUsingPost(
  body: API.UserLoginRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseLoginUserVo_>('/api/user/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** register POST /api/user/logout */
export async function registerUsingPost1(options?: { [key: string]: any }) {
  return request<API.BaseResponseBoolean_>('/api/user/logout', {
    method: 'POST',
    ...(options || {}),
  });
}

/** register POST /api/user/register */
export async function registerUsingPost2(
  body: API.UserRegisterRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseLong_>('/api/user/register', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
