import { useState } from 'react';
import type { ChangeEvent, FormEvent } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';

import { getApiErrorMessage } from '../../api/client';
import { useAuthStore } from '../../stores/auth';
import styles from './LoginView.module.css';

type FieldErrors = {
  email?: string;
  password?: string;
};

/** 只做基础格式拦截，真正的判定仍以后端返回为准 */
const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

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
    <circle cx="12" cy="12" r="8.5" />
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

const validate = (email: string, password: string): FieldErrors => {
  const errors: FieldErrors = {};

  if (!email.trim()) {
    errors.email = '请输入邮箱';
  } else if (!EMAIL_PATTERN.test(email.trim())) {
    errors.email = '邮箱格式不正确';
  }

  if (!password) {
    errors.password = '请输入密码';
  }

  return errors;
};

const controlClass = (invalid: boolean): string =>
  invalid ? `${styles.control} ${styles.controlInvalid}` : styles.control;

const LoginView = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const signIn = useAuthStore((state) => state.signIn);

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [passwordVisible, setPasswordVisible] = useState(false);
  const [fieldErrors, setFieldErrors] = useState<FieldErrors>({});
  const [formError, setFormError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  // 被路由守卫拦下来时会带上原本要去的地址
  const redirectTo = (location.state as { from?: string } | null)?.from ?? '/';

  const handleChange =
    (field: keyof FieldErrors, setValue: (value: string) => void) =>
    (event: ChangeEvent<HTMLInputElement>) => {
      setValue(event.target.value);
      // 一开始修改就撤掉上一次的报错，不要一直红着
      setFormError('');
      setFieldErrors((prev) => (prev[field] ? { ...prev, [field]: undefined } : prev));
    };

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (submitting) {
      return;
    }

    const errors = validate(email, password);
    setFieldErrors(errors);
    setFormError('');

    if (errors.email || errors.password) {
      return;
    }

    setSubmitting(true);
    try {
      await signIn({ email: email.trim(), password });
      navigate(redirectTo, { replace: true });
    } catch (error) {
      // 后端已经返回了可直接展示的文案，例如“邮箱或密码错误”
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
        <p className={styles.overline}>WELCOME BACK</p>
        <h1 className={styles.title}>
          继续你们的
          <br />
          <span>下一段故事。</span>
        </h1>
        <p className={styles.subtitle}>登录后，把那些想一起完成的小事记录下来。</p>

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

          <div className={styles.field}>
            <div className={styles.fieldHeader}>
              <label className={styles.label} htmlFor="login-email">
                邮箱地址
              </label>
              <span className={styles.fieldHint}>账号登录</span>
            </div>
            <div className={controlClass(Boolean(fieldErrors.email))}>
              <span className={styles.iconWrap} aria-hidden="true">
                <MailIcon />
              </span>
              <input
                id="login-email"
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
                onChange={handleChange('email', setEmail)}
              />
            </div>
            {fieldErrors.email ? (
              <p className={styles.fieldError} id="login-email-error">
                {fieldErrors.email}
              </p>
            ) : null}
          </div>

          <div className={styles.field}>
            <div className={styles.fieldHeader}>
              <label className={styles.label} htmlFor="login-password">
                登录密码
              </label>
              <span className={styles.fieldHint}>保持私密</span>
            </div>
            <div className={controlClass(Boolean(fieldErrors.password))}>
              <span className={styles.iconWrap} aria-hidden="true">
                <LockIcon />
              </span>
              <input
                id="login-password"
                className={styles.input}
                type={passwordVisible ? 'text' : 'password'}
                value={password}
                placeholder="输入你的密码"
                autoComplete="current-password"
                autoCapitalize="off"
                autoCorrect="off"
                spellCheck={false}
                enterKeyHint="go"
                aria-invalid={Boolean(fieldErrors.password)}
                aria-describedby={fieldErrors.password ? 'login-password-error' : undefined}
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
              <p className={styles.fieldError} id="login-password-error">
                {fieldErrors.password}
              </p>
            ) : null}
          </div>

          <button className={styles.submit} type="submit" disabled={submitting}>
            {submitting ? (
              <>
                <span className={styles.spinner} aria-hidden="true" />
                登录中…
              </>
            ) : (
              <>
                开始冒险
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
        <p>还没有账号？注册页面会在下一步接入。</p>
        <span className={styles.footerRule} />
      </footer>
    </div>
  );
};

export default LoginView;
