import { Navigate, createBrowserRouter } from 'react-router-dom';

import App from '../App';
import HomeView from '../views/home/HomeView';
import LoginView from '../views/login/LoginView';
import { GuestOnly, RequireAuth } from './guards';

export const router = createBrowserRouter([
  {
    // 无 path 的布局路由：只负责套一层 H5 页面容器
    element: <App />,
    children: [
      {
        index: true,
        element: (
          <RequireAuth>
            <HomeView />
          </RequireAuth>
        ),
      },
      {
        path: 'login',
        element: (
          <GuestOnly>
            <LoginView />
          </GuestOnly>
        ),
      },
      {
        path: '*',
        element: <Navigate to="/" replace />,
      },
    ],
  },
]);
