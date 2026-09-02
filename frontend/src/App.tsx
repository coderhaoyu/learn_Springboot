import { Outlet } from 'react-router-dom';

/** H5 页面容器：限制最大宽度、处理安全区，具体内容由子路由渲染。 */
const App = () => (
  <div className="app-shell">
    <Outlet />
  </div>
);

export default App;
