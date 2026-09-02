import axios from 'axios';

import type { ApiResponse } from '../types/api';
import { UNAUTHORIZED_EVENT, clearAuth, readToken } from '../stores/authStorage';

const FALLBACK_MESSAGE = '网络异常，请稍后重试';

export const apiClient = axios.create({
  // 默认走 vite 代理（见 vite.config.ts），部署时用 VITE_API_BASE_URL 覆盖
  baseURL: import.meta.env.VITE_API_BASE_URL ?? '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

apiClient.interceptors.request.use((config) => {
  const token = readToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

apiClient.interceptors.response.use(
  (response) => response,
  (error: unknown) => {
    if (axios.isAxiosError(error) && error.response?.status === 401) {
      // 登录接口自身返回 401 表示账号密码错误，不是会话过期，不能清状态
      const isAuthRequest = (error.config?.url ?? '').startsWith('/auth/');
      if (!isAuthRequest) {
        clearAuth();
        window.dispatchEvent(new Event(UNAUTHORIZED_EVENT));
      }
    }
    return Promise.reject(error);
  },
);

/** 优先展示后端 ApiResponse.message，取不到再按网络错误类型兜底 */
export const getApiErrorMessage = (error: unknown): string => {
  if (!axios.isAxiosError(error)) {
    return FALLBACK_MESSAGE;
  }

  const message = (error.response?.data as ApiResponse<unknown> | undefined)?.message;
  if (message) {
    return message;
  }

  if (error.code === 'ECONNABORTED' || error.code === 'ETIMEDOUT') {
    return '请求超时，请检查网络后重试';
  }

  if (!error.response) {
    return '连接不上服务器，请确认后端已启动';
  }

  return FALLBACK_MESSAGE;
};
