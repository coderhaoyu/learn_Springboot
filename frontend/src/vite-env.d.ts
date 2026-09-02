/// <reference types="vite/client" />

interface ImportMetaEnv {
  /** 后端接口前缀，默认走 vite 代理的 /api */
  readonly VITE_API_BASE_URL?: string;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}
