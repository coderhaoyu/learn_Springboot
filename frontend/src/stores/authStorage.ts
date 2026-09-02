import type { UserVo } from '../types/api';

/**
 * 登录状态的本地持久化。
 * 单独放一层，是为了让 api/client.ts 取 token 时不需要反向依赖 stores/auth.ts。
 */

const STORAGE_KEY = 'oa.auth';

/** 请求层发现 token 失效时派发的事件，stores/auth.ts 监听后清空内存状态 */
export const UNAUTHORIZED_EVENT = 'oa:unauthorized';

export type AuthSession = {
  token: string;
  user: UserVo;
  /** 过期时间戳（毫秒），由登录返回的 expiresIn 换算 */
  expiresAt: number;
};

export const clearAuth = (): void => {
  try {
    localStorage.removeItem(STORAGE_KEY);
  } catch {
    // 隐私模式下 localStorage 可能不可用，忽略
  }
};

export const writeAuth = (session: AuthSession): void => {
  try {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(session));
  } catch {
    // 写不进去只影响“记住登录”，不影响当次会话
  }
};

/** 读取本地会话，已过期或格式不对时清掉并返回 null */
export const readAuth = (): AuthSession | null => {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (!raw) {
      return null;
    }

    const session = JSON.parse(raw) as Partial<AuthSession>;
    if (!session.token || !session.user || !session.expiresAt) {
      clearAuth();
      return null;
    }

    if (session.expiresAt <= Date.now()) {
      clearAuth();
      return null;
    }

    return session as AuthSession;
  } catch {
    clearAuth();
    return null;
  }
};

export const readToken = (): string | null => readAuth()?.token ?? null;
