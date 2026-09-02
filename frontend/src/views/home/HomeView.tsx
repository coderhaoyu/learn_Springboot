import { useAuthStore } from '../../stores/auth';
import styles from './HomeView.module.css';

/** 登录后的落地页。真正的首页汇总要等后端 dashboard 接口，这里只展示会话状态。 */
const HomeView = () => {
  const user = useAuthStore((state) => state.user);
  const signOut = useAuthStore((state) => state.signOut);

  return (
    <div className={styles.page}>
      <header className={styles.header}>
        <p className={styles.greeting}>你好，{user?.name ?? '旅人'}</p>
        {user?.email ? <p className={styles.email}>{user.email}</p> : null}
      </header>

      <section className={styles.card}>
        <p className={styles.cardTitle}>登录已接通</p>
        <p className={styles.cardText}>情侣绑定、挑战和打卡页面会在后续阶段接入。</p>
      </section>

      <button className={styles.signOut} type="button" onClick={signOut}>
        退出登录
      </button>
    </div>
  );
};

export default HomeView;
