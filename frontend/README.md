# 我们的冒险前端

React + TypeScript + Vite 的 H5 前端。

## 开发命令

```bash
pnpm install
pnpm dev
pnpm build
pnpm preview
```

`pnpm dev` 监听 `0.0.0.0:5173`，终端里会打印局域网地址，手机连同一个 Wi-Fi 可以直接打开做真机调试。

## 后端联调

后端默认跑在 `http://localhost:8080`，接口本身没有 `/api` 前缀（例如 `POST /auth/login`）。

前端统一请求 `/api/**`，由 `vite.config.ts` 里的代理转发并去掉前缀，因此开发阶段不需要后端配置 CORS。
部署到别的域名时用 `VITE_API_BASE_URL` 覆盖请求前缀。

## H5 适配

设计稿基准 **375px**，方案是 **vw 计算根字号 + postcss-pxtorem 把 px 换算成 rem**：

- 写样式时直接按设计稿写 `px`，构建时按 `1rem = 37.5px` 换算（`postcss.config.js`）；
- 根字号 `min(10vw, 48px)`（`src/styles/adaptive.css`），视口 375px 时 1:1 还原，变宽变窄等比缩放；
- 超过 `--shell-max-width`（480px）停止放大，`.app-shell` 居中显示，宽屏下相当于一个手机视口；
- `48px` 和 `480px` 是同一个比例（`37.5 × 480 / 375`），要改就成对改。

几条书写约定：

- `adaptive.css` 里的 `html {}` 块被 postcss-pxtorem 排除，里面的 `px` 保持原样，不要在这里写需要缩放的尺寸；
- `1px` 不参与换算（`minPixelValue: 2`），细线用 `.hairline-bottom` 压到高分屏的物理 1px；
- 媒体查询不参与换算，按真实视口宽度判断；
- 输入框字号不要小于 `16px`（`--fs-md`），否则 iOS 聚焦时会缩放页面；
- 安全区用 `--safe-top` 等变量，已经在 `.app-shell` 上统一加了内边距；
- 视口高度用 `100dvh`，`100vh` 只作为老设备兜底。

根字号跟随视口计算，代价是浏览器的“字体大小”设置对页面不再生效，这是移动端 H5 的常见取舍。

## 目录结构

```text
src/
├── api/         # 按后端模块封装请求，client.ts 负责 token 与错误文案
├── router/      # 路由表与登录守卫
├── stores/      # zustand 状态，authStorage.ts 负责本地会话持久化
├── styles/      # tokens / reset / adaptive / index
├── types/       # 与后端 VO 对应的类型
├── views/       # 按页面模块组织视图
└── App.tsx      # H5 页面容器
```

## 已完成页面

- 登录（`/login`）：邮箱密码登录、字段校验、错误提示、密码显隐、登录态持久化；
- 登录后落地页（`/`）：展示当前用户并支持退出登录。

注册页面、情侣绑定和挑战相关页面尚未接入。
