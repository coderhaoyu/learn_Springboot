import type { ReactElement } from 'react';
import { Navigate, useLocation } from 'react-router-dom';

import { useAuthStore } from '../stores/auth';

type GuardProps = {
  children: ReactElement;
};

/** 未登录时跳到登录页，并把原本要访问的地址带过去 */
export const RequireAuth = ({ children }: GuardProps) => {
  const token = useAuthStore((state) => state.token);
  const location = useLocation();

  if (!token) {
    return (
      <Navigate to="/login" replace state={{ from: `${location.pathname}${location.search}` }} />
    );
  }

  return children;
};

/** 已登录时不再展示登录页 */
export const GuestOnly = ({ children }: GuardProps) => {
  const token = useAuthStore((state) => state.token);

  if (token) {
    return <Navigate to="/" replace />;
  }

  return children;
};
