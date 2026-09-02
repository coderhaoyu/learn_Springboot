import { create } from 'zustand';

import { login as loginRequest } from '../api/auth';
import type { LoginPayload } from '../api/auth';
import type { UserVo } from '../types/api';
import { UNAUTHORIZED_EVENT, clearAuth, readAuth, writeAuth } from './authStorage';

type AuthState = {
  token: string | null;
  user: UserVo | null;
  /** 登录成功后写入本地并更新内存状态，失败时抛出，由页面展示错误 */
  signIn: (payload: LoginPayload) => Promise<void>;
  signOut: () => void;
};

const restored = readAuth();

export const useAuthStore = create<AuthState>((set) => ({
  token: restored?.token ?? null,
  user: restored?.user ?? null,

  signIn: async (payload) => {
    const result = await loginRequest(payload);

    writeAuth({
      token: result.token,
      user: result.user,
      expiresAt: Date.now() + result.expiresIn * 1000,
    });

    set({ token: result.token, user: result.user });
  },

  signOut: () => {
    clearAuth();
    set({ token: null, user: null });
  },
}));

// 请求层拿到 401 时同步清空内存状态，路由守卫会把用户送回登录页
window.addEventListener(UNAUTHORIZED_EVENT, () => {
  useAuthStore.setState({ token: null, user: null });
});
