import { useState } from 'react';
import type { ChangeEvent, FormEvent } from 'react';
import { Link, useNavigate } from 'react-router-dom';

import { getApiErrorMessage } from '../../api/client';
import { useAuthStore } from '../../stores/auth';
import styles from './RegisterView.module.css';

type FieldErrors = {
  nickname?: string;
  email?: string;
  password?: string;
};

/** 基础格式校验，与后端校验约束对齐（nickname 2-20, email 邮箱格式, password 6-20） */
const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

const UserIcon = () => (
  <svg viewBox="0 0 24 24" aria-hidden="true" className={styles.controlIcon}>
    <circle cx="12" cy="8" r="3.2" />
    <path d="M5.5 20c.7-3.3 2.8-5 6.5-5s5.8 1.7 6.5 5" />
  </svg>
);

const MailIcon = () => (
  <svg viewBox="0 0 24 24" aria-hidden="true" className={styles.controlIcon}>
    <rect x="3.5" y="5" width="17" height="14" rx="2.5" />
    <path d="m5 7 7 5 7-5" />
  </svg>
);

const LockIcon = () => (
  <svg viewBox="0 0 24 24" aria-hidden="true" className={styles.controlIcon}>
    <rect x="4.5" y="10" width="15" height="10" rx="2.5" />
    <path d="M8 10V7.5a4 4 0 0 1 8 0V10M12 14v2.5" />
  </svg>
);

const EyeIcon = ({ visible }: { visible: boolean }) =>
  visible ? (
    <svg viewBox="0 0 24 24" aria-hidden="true" className={styles.eyeIcon}>
      <path d="M3.5 12s3-5 8.5-5 8.5 5 8.5 5-3 5-8.5 5-8.5-5-8.5-5Z" />
      <circle cx="12" cy="12" r="2.2" />
      <path d="m5 5 14 14" />
    </svg>
  ) : (
    <svg viewBox="0 0 24 24" aria-hidden="true" className={styles.eyeIcon}>
      <path d="M3.5 12s3-5 8.5-5 8.5 5 8.5 5-3 5-8.5 5-8.5-5-8.5-5Z" />
      <circle cx="12" cy="12" r="2.2" />
    </svg>
  );

const CompassIcon = () => (
  <svg viewBox="0 0 24 24" aria-hidden="true" className={styles.logoIcon}>
    <circle cx="12" cy="8.5" r="8.5" />
    <path d="m14.9 9.1-1.7 4.1-4.1 1.7 1.7-4.1 4.1-1.7Z" />
    <path d="M12 3v1.5M12 19.5V21M3 12h1.5M19.5 12H21" />
  </svg>
);

const SparkleIcon = () => (
  <svg viewBox="0 0 24 24" aria-hidden="true" className={styles.sparkleIcon}>
    <path d="m12 3 1.8 5.2L19 10l-5.2 1.8L12 17l-1.8-5.2L5 10l5.2-1.8L12 3Z" />
    <path d="m19 16 .8 2.2L22 19l-2.2.8L19 22l-.8-2.2L16 19l2.2-.8L19 16Z" />
  </svg>
);

const validate = (nickname: string, email: string, password: string): FieldErrors => {
  const errors: FieldErrors = {};
  const trimmedNickname = nickname.trim();
  const trimmedEmail = email.trim();

  if (!trimmedNickname) {
    errors.nickname = '请输入昵称';
  } else if (trimmedNickname.length < 2 || trimmedNickname.length > 20) {
    errors.nickname = '昵称长度必须在 2 到 20 个字符之间';
  }

  if (!trimmedEmail) {
    errors.email = '请输入邮箱';
  } else if (!EMAIL_PATTERN.test(trimmedEmail)) {
    errors.email = '邮箱格式不正确';
  }

  if (!password) {
    errors.password = '请输入密码';
  } else if (password.length < 6 || password.length > 20) {
    errors.password = '密码长度必须在 6 到 20 位之间';
  }

  return errors;
};

const controlClass = (invalid: boolean): string =>
  invalid ? `${styles.control} ${styles.controlInvalid}` : styles.control;

const RegisterView = () => {
  const navigate = useNavigate();
  const signUp = useAuthStore((state) => state.signUp);

  const [nickname, setNickname] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [passwordVisible, setPasswordVisible] = useState(false);
  const [fieldErrors, setFieldErrors] = useState<FieldErrors>({});
  const [formError, setFormError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const handleChange =
    (field: keyof FieldErrors, setValue: (value: string) => void) =>
    (event: ChangeEvent<HTMLInputElement>) => {
      setValue(event.target.value);
      setFormError('');
      setFieldErrors((prev) => (prev[field] ? { ...prev, [field]: undefined } : prev));
    };

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (submitting) {
      return;
    }

    const errors = validate(nickname, email, password);
    setFieldErrors(errors);
    setFormError('');

    if (errors.nickname || errors.email || errors.password) {
      return;
    }

    setSubmitting(true);
    try {
      // 注册成功后直接自动调用登录并更新状态，一步进入首页
      await signUp({
        nickname: nickname.trim(),
        email: email.trim(),
        password,
      });
      navigate('/', { replace: true });
    } catch (error) {
      // 捕获例如“邮箱已被注册”等后端业务异常或校验错误
      setFormError(getApiErrorMessage(error));
      setSubmitting(false);
    }
  };

  return (
    <div className={styles.page}>
      <div className={`${styles.ambient} ${styles.ambientOne}`} aria-hidden="true" />
      <div className={`${styles.ambient} ${styles.ambientTwo}`} aria-hidden="true" />

      <header className={styles.brand}>
        <div className={styles.logo} aria-hidden="true">
          <CompassIcon />
        </div>
        <div>
          <p className={styles.wordmark}>OUR ADVENTURE</p>
          <p className={styles.brandNote}>a little place for two</p>
        </div>
      </header>

      <main className={styles.main}>
        <p className={styles.overline}>CREATE ACCOUNT</p>
        <h1 className={styles.title}>
          开启属于你们的
          <br />
          <span>第一段冒险。</span>
        </h1>
        <p className={styles.subtitle}>注册账号，把那些想一起完成的小事记录下来。</p>

        <div className={styles.promise}>
          <span className={styles.promiseIcon}>
            <SparkleIcon />
          </span>
          <span>只属于你们的共同空间</span>
          <span className={styles.promiseLine} />
          <span className={styles.promiseDot} />
        </div>

        <form className={styles.form} onSubmit={handleSubmit} noValidate>
          {formError ? (
            <p className={styles.formError} role="alert">
              {formError}
            </p>
          ) : null}

          {/* 昵称输入 */}
          <div className={styles.field}>
            <div className={styles.fieldHeader}>
              <label className={styles.label} htmlFor="register-nickname">
                你的昵称
              </label>
              <span className={styles.fieldHint}>2-20 个字符</span>
            </div>
            <div className={controlClass(Boolean(fieldErrors.nickname))}>
              <span className={styles.iconWrap} aria-hidden="true">
                <UserIcon />
              </span>
              <input
                id="register-nickname"
                className={styles.input}
                type="text"
                value={nickname}
                placeholder="例如：阿星、桃子"
                autoComplete="nickname"
                autoCapitalize="off"
                autoCorrect="off"
                spellCheck={false}
                enterKeyHint="next"
                maxLength={20}
                aria-invalid={Boolean(fieldErrors.nickname)}
                aria-describedby={fieldErrors.nickname ? 'register-nickname-error' : undefined}
                onChange={handleChange('nickname', setNickname)}
              />
            </div>
            {fieldErrors.nickname ? (
              <p className={styles.fieldError} id="register-nickname-error">
                {fieldErrors.nickname}
              </p>
            ) : null}
          </div>

          {/* 邮箱输入 */}
          <div className={styles.field}>
            <div className={styles.fieldHeader}>
              <label className={styles.label} htmlFor="register-email">
                邮箱地址
              </label>
              <span className={styles.fieldHint}>账号登录凭据</span>
            </div>
            <div className={controlClass(Boolean(fieldErrors.email))}>
              <span className={styles.iconWrap} aria-hidden="true">
                <MailIcon />
              </span>
              <input
                id="register-email"
                className={styles.input}
                type="email"
                value={email}
                placeholder="you@example.com"
                inputMode="email"
                autoComplete="email"
                autoCapitalize="off"
                autoCorrect="off"
                spellCheck={false}
                enterKeyHint="next"
                aria-invalid={Boolean(fieldErrors.email)}
                aria-describedby={fieldErrors.email ? 'register-email-error' : undefined}
                onChange={handleChange('email', setEmail)}
              />
            </div>
            {fieldErrors.email ? (
              <p className={styles.fieldError} id="register-email-error">
                {fieldErrors.email}
              </p>
            ) : null}
          </div>

          {/* 密码输入 */}
          <div className={styles.field}>
            <div className={styles.fieldHeader}>
              <label className={styles.label} htmlFor="register-password">
                设置密码
              </label>
              <span className={styles.fieldHint}>6-20 位字符</span>
            </div>
            <div className={controlClass(Boolean(fieldErrors.password))}>
              <span className={styles.iconWrap} aria-hidden="true">
                <LockIcon />
              </span>
              <input
                id="register-password"
                className={styles.input}
                type={passwordVisible ? 'text' : 'password'}
                value={password}
                placeholder="输入 6-20 位密码"
                autoComplete="new-password"
                autoCapitalize="off"
                autoCorrect="off"
                spellCheck={false}
                enterKeyHint="done"
                maxLength={20}
                aria-invalid={Boolean(fieldErrors.password)}
                aria-describedby={fieldErrors.password ? 'register-password-error' : undefined}
                onChange={handleChange('password', setPassword)}
              />
              <button
                className={styles.toggle}
                type="button"
                aria-pressed={passwordVisible}
                aria-label={passwordVisible ? '隐藏密码' : '显示密码'}
                onClick={() => setPasswordVisible((visible) => !visible)}
              >
                <EyeIcon visible={passwordVisible} />
              </button>
            </div>
            {fieldErrors.password ? (
              <p className={styles.fieldError} id="register-password-error">
                {fieldErrors.password}
              </p>
            ) : null}
          </div>

          <button className={styles.submit} type="submit" disabled={submitting}>
            {submitting ? (
              <>
                <span className={styles.spinner} aria-hidden="true" />
                注册中…
              </>
            ) : (
              <>
                创建账号
                <span className={styles.submitArrow} aria-hidden="true">
                  →
                </span>
              </>
            )}
          </button>
        </form>
      </main>

      <footer className={styles.footer}>
        <span className={styles.footerRule} />
        <p>
          已有账号？
          <Link to="/login" className={styles.link}>
            直接登录
          </Link>
        </p>
        <span className={styles.footerRule} />
      </footer>
    </div>
  );
};

export default RegisterView;
