import { useAuthStore } from '../../stores/auth';
import styles from './HomeView.module.css';

const CompassIcon = () => (
  <svg viewBox="0 0 24 24" aria-hidden="true" className={styles.icon}>
    <circle cx="12" cy="12" r="8.5" />
    <path d="m14.9 9.1-1.7 4.1-4.1 1.7 1.7-4.1 4.1-1.7Z" />
    <path d="M12 3v1.5M12 19.5V21M3 12h1.5M19.5 12H21" />
  </svg>
);

const ArrowIcon = () => (
  <svg viewBox="0 0 24 24" aria-hidden="true" className={styles.icon}>
    <path d="M5 12h13M13 6l6 6-6 6" />
  </svg>
);

const CheckIcon = () => (
  <svg viewBox="0 0 24 24" aria-hidden="true" className={styles.icon}>
    <path d="m5 12 4.2 4.2L19 6.5" />
  </svg>
);

const SparkIcon = () => (
  <svg viewBox="0 0 24 24" aria-hidden="true" className={styles.icon}>
    <path d="m12 3 1.8 5.2L19 10l-5.2 1.8L12 17l-1.8-5.2L5 10l5.2-1.8L12 3Z" />
    <path d="m19 16 .8 2.2L22 19l-2.2.8L19 22l-.8-2.2L16 19l2.2-.8L19 16Z" />
  </svg>
);

const HeartIcon = () => (
  <svg viewBox="0 0 24 24" aria-hidden="true" className={styles.icon}>
    <path d="M20.8 8.8c0 5.1-8.8 10.2-8.8 10.2S3.2 13.9 3.2 8.8A4.5 4.5 0 0 1 12 6.7a4.5 4.5 0 0 1 8.8 2.1Z" />
  </svg>
);

const HomeIcon = () => (
  <svg viewBox="0 0 24 24" aria-hidden="true" className={styles.icon}>
    <path d="m4 10 8-6 8 6v9a1 1 0 0 1-1 1H5a1 1 0 0 1-1-1v-9Z" />
    <path d="M9.5 20v-5h5v5" />
  </svg>
);

const CalendarIcon = () => (
  <svg viewBox="0 0 24 24" aria-hidden="true" className={styles.icon}>
    <rect x="4" y="5.5" width="16" height="14.5" rx="2" />
    <path d="M8 3.5v4M16 3.5v4M4 10h16" />
    <path d="M8 14h.01M12 14h.01M16 14h.01M8 17h.01M12 17h.01" />
  </svg>
);

const UserIcon = () => (
  <svg viewBox="0 0 24 24" aria-hidden="true" className={styles.icon}>
    <circle cx="12" cy="8" r="3.2" />
    <path d="M5.5 20c.7-3.3 2.8-5 6.5-5s5.8 1.7 6.5 5" />
  </svg>
);

/** 登录后的落地页：先用静态旅程内容建立首页骨架，后续可把每个模块替换成 dashboard 接口数据。 */
const HomeView = () => {
  const user = useAuthStore((state) => state.user);
  const signOut = useAuthStore((state) => state.signOut);
  const displayName = user?.name ?? '旅人';
  const avatarLabel = displayName.slice(0, 1);

  return (
    <div className={styles.page}>
      <div className={styles.ambient} aria-hidden="true" />

      <header className={styles.header}>
        <div className={styles.headerCopy}>
          <p className={styles.eyebrow}>OUR ADVENTURE · DAY 06</p>
          <h1 className={styles.greeting}>
            早上好，{displayName}
            <span className={styles.greetingMark}>✦</span>
          </h1>
          <p className={styles.intro}>把平凡的一天，也走成只属于你们的故事。</p>
        </div>

        <div className={styles.accountActions}>
          <div className={styles.avatar} aria-hidden="true">
            {avatarLabel}
          </div>
          <button className={styles.logoutButton} type="button" onClick={signOut}>
            退出
          </button>
        </div>
      </header>

      <main className={styles.content}>
        <section className={styles.journey} aria-labelledby="journey-title">
          <div className={styles.journeyHeader}>
            <div>
              <p className={styles.sectionKicker}>正在进行</p>
              <h2 id="journey-title">两个人的周末</h2>
            </div>
            <div className={styles.compass}>
              <CompassIcon />
            </div>
          </div>

          <div className={styles.route} aria-hidden="true">
            <div className={styles.routeLine} />
            <div className={`${styles.routeStop} ${styles.routeStopDone}`}>
              <span className={styles.stopDot}>
                <CheckIcon />
              </span>
              <span>出发</span>
            </div>
            <div className={`${styles.routeStop} ${styles.routeStopCurrent}`}>
              <span className={styles.stopDot}>
                <span />
              </span>
              <span>今天</span>
            </div>
            <div className={styles.routeStop}>
              <span className={styles.stopDot} />
              <span>下一站</span>
            </div>
          </div>

          <div className={styles.journeyFooter}>
            <div>
              <p className={styles.journeyMeta}>已一起完成 5 个小挑战</p>
              <p className={styles.journeyNext}>下一站：一起做一顿晚餐</p>
            </div>
            <button className={styles.journeyButton} type="button">
              继续探索
              <ArrowIcon />
            </button>
          </div>
        </section>

        <section className={styles.todaySection} aria-labelledby="today-title">
          <div className={styles.sectionHeading}>
            <div>
              <p className={styles.sectionKicker}>TODAY</p>
              <h2 id="today-title">今天一起完成</h2>
            </div>
            <button className={styles.textButton} type="button">
              查看全部
              <ArrowIcon />
            </button>
          </div>

          <div className={styles.taskList}>
            <div className={`${styles.taskRow} ${styles.taskRowDone}`}>
              <span className={styles.taskIcon}>
                <HeartIcon />
              </span>
              <div className={styles.taskCopy}>
                <strong>互相说一句晚安</strong>
                <span>让今天在温柔里收尾</span>
              </div>
              <span className={styles.taskStatus}>
                <CheckIcon />
              </span>
            </div>

            <div className={styles.taskRow}>
              <span className={`${styles.taskIcon} ${styles.taskIconWarm}`}>
                <SparkIcon />
              </span>
              <div className={styles.taskCopy}>
                <strong>分享一张今天的照片</strong>
                <span>记录一个值得记住的瞬间</span>
              </div>
              <span className={styles.taskArrow}>
                <ArrowIcon />
              </span>
            </div>

            <div className={styles.taskRow}>
              <span className={`${styles.taskIcon} ${styles.taskIconSoft}`}>
                <CalendarIcon />
              </span>
              <div className={styles.taskCopy}>
                <strong>约好下一次见面</strong>
                <span>把期待写进你们的日历</span>
              </div>
              <span className={styles.taskArrow}>
                <ArrowIcon />
              </span>
            </div>
          </div>
        </section>

        <section className={styles.memorySection} aria-label="共同记录">
          <div className={styles.memory}>
            <div className={styles.memoryGlow} aria-hidden="true" />
            <div className={styles.memoryTopline}>
              <span>OUR MEMORY</span>
              <span>03 / 12</span>
            </div>
            <div className={styles.memoryContent}>
              <span className={styles.memoryIcon}>
                <HeartIcon />
              </span>
              <p>“和喜欢的人，去看每一场日落。”</p>
              <span className={styles.memoryDate}>写于 · 第一次旅行</span>
            </div>
          </div>

          <div className={styles.streak}>
            <div className={styles.streakHeader}>
              <span className={styles.streakIcon}>
                <SparkIcon />
              </span>
              <span>连续记录</span>
            </div>
            <strong>06 <small>天</small></strong>
            <p>保持这份默契，明天见。</p>
          </div>
        </section>
      </main>

      <nav className={styles.bottomNav} aria-label="主导航">
        <button className={`${styles.navItem} ${styles.navItemActive}`} type="button">
          <HomeIcon />
          <span>首页</span>
        </button>
        <button className={styles.navItem} type="button">
          <CalendarIcon />
          <span>日历</span>
        </button>
        <button className={styles.navItem} type="button">
          <HeartIcon />
          <span>我们的事</span>
        </button>
        <button className={styles.navItem} type="button">
          <UserIcon />
          <span>我的</span>
        </button>
      </nav>
    </div>
  );
};

export default HomeView;
