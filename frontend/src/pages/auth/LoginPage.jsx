import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import toast from 'react-hot-toast';

import { AuthShell } from '../../components/layout/AuthShell';
import { login } from '../../app/authSlice';

export default function LoginPage() {
  const [email, setEmail] = useState('admin@example.com');
  const [password, setPassword] = useState('Admin12345');
  const [submitting, setSubmitting] = useState(false);
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const user = useSelector((s) => s.auth.user);
  if (user) navigate('/', { replace: true });

  const onSubmit = async (event) => {
    event.preventDefault();
    setSubmitting(true);
    try {
      await dispatch(login({ email, password })).unwrap();
      toast.success('Signed in');
      navigate('/');
    } catch { /* toast already shown */ }
    finally { setSubmitting(false); }
  };

  return (
    <AuthShell
      eyebrow="ERPMS · Sign in"
      title="Welcome back."
      subtitle="Enter your credentials to access the research management console."
      footer={<>New here? <Link className="text-accent-deep dark:text-accent font-semibold" to="/register">Create an account</Link></>}
    >
      <form onSubmit={onSubmit} className="space-y-4" data-testid="login-form">
        <div>
          <label className="field-label">Work email</label>
          <input
            data-testid="login-email"
            className="field-input"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
            autoComplete="email"
          />
        </div>
        <div>
          <label className="field-label flex items-center justify-between">
            <span>Password</span>
            <Link to="/forgot-password" className="text-[11px] text-accent-deep dark:text-accent-soft hover:underline">
              Forgot?
            </Link>
          </label>
          <input
            data-testid="login-password"
            className="field-input"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
            autoComplete="current-password"
          />
        </div>
        <button data-testid="login-submit" disabled={submitting} className="btn-primary w-full justify-center">
          {submitting ? 'Signing in…' : 'Sign in'}
        </button>
      </form>
    </AuthShell>
  );
}
