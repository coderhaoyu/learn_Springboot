/**
 * H5 适配：按 375px 设计稿直接书写 px，构建时统一换算成 rem。
 *
 * 根字号写在 src/styles/tokens.css 的 html 选择器里，用 vw 计算，
 * 所以 html 选择器必须排除在换算之外，否则会出现“自己换算自己”的循环。
 */
import pxtorem from 'postcss-pxtorem';

export default {
  plugins: [
    pxtorem({
      // 375px 设计稿下 1rem = 37.5px，即设计稿 1px === 代码里写的 1px
      rootValue: 37.5,
      unitPrecision: 5,
      propList: ['*'],
      // html 选择器里放的是视口相关的固定值，保持 px 原样
      selectorBlackList: ['html'],
      replace: true,
      // 媒体查询按真实视口判断，不参与换算
      mediaQuery: false,
      // 1px 保留为物理细线，不被缩放成小数
      minPixelValue: 2,
      exclude: /node_modules/i,
    }),
  ],
};
