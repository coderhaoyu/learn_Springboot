import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
    // 监听 0.0.0.0，方便手机连同一个 Wi-Fi 用局域网 IP 做真机调试
    host: true,
    port: 5173,
    proxy: {
      // 前端统一请求 /api/**，开发环境代理到后端，绕开跨域
      // 后端接口本身没有 /api 前缀（如 POST /auth/login），所以转发时去掉
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, ''),
      },
    },
  },
});
