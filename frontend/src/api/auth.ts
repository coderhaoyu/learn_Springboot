import { apiClient } from './client';
import type { ApiResponse, LoginVo } from '../types/api';

export type LoginPayload = {
  email: string;
  password: string;
};

export type RegisterPayload = {
  nickname: string;
  email: string;
  password: string;
};

/** POST /auth/login，对应后端 AuthController#login */
export const login = async (payload: LoginPayload): Promise<LoginVo> => {
  const response = await apiClient.post<ApiResponse<LoginVo>>('/auth/login', payload);
  return response.data.data;
};

/** POST /auth/register，对应后端 AuthController#registerUser */
export const register = async (payload: RegisterPayload): Promise<boolean> => {
  const response = await apiClient.post<ApiResponse<boolean>>('/auth/register', payload);
  return response.data.data;
};

