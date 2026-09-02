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
      <header className={styles.brand}>
        <p className={styles.logo} aria-hidden="true">
          🧭
        </p>
        <h1 className={styles.title}>我们的冒险</h1>
        <p className={styles.subtitle}>登录后开始你们的共同挑战</p>
      </header>

      <form className={styles.form} onSubmit={handleSubmit} noValidate>
        {formError ? (
          <p className={styles.formError} role="alert">
            {formError}
          </p>
        ) : null}

        <div className={styles.field}>
          <label className={styles.label} htmlFor="login-email">
            邮箱
          </label>
          <div className={controlClass(Boolean(fieldErrors.email))}>
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
              aria-invalid={Boolean(fieldErrors.email)}
              aria-describedby={fieldErrors.email ? 'login-email-error' : undefined}
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
          <label className={styles.label} htmlFor="login-password">
            密码
          </label>
          <div className={controlClass(Boolean(fieldErrors.password))}>
            <input
              id="login-password"
              className={styles.input}
              type={passwordVisible ? 'text' : 'password'}
              value={password}
              placeholder="请输入密码"
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
              {passwordVisible ? '隐藏' : '显示'}
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
            '登录'
          )}
        </button>
      </form>

      <p className={styles.footnote}>还没有账号？注册页面会在下一步接入。</p>
    </div>
  );
};

export default LoginView;
