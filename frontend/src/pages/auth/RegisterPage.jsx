import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useDispatch } from 'react-redux';
import toast from 'react-hot-toast';

import { AuthShell } from '../../components/layout/AuthShell';
import { register } from '../../app/authSlice';

export default function RegisterPage() {
  const [form, setForm] = useState({ fullName: '', email: '', password: '' });
  const [submitting, setSubmitting] = useState(false);
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const onChange = (field) => (e) => setForm({ ...form, [field]: e.target.value });
  const onSubmit = async (event) => {
    event.preventDefault();
    setSubmitting(true);
    try {
      await dispatch(register(form)).unwrap();
      toast.success('Welcome to ERPMS');
      navigate('/');
    } catch { /* handled */ }
    finally { setSubmitting(false); }
  };

  return (
    <AuthShell
      eyebrow="ERPMS · Create account"
      title="Join the platform."
      subtitle="Create your workspace access. An administrator will assign you the appropriate role."
      footer={<>Already a member? <Link className="text-accent-deep dark:text-accent font-semibold" to="/login">Sign in</Link></>}
    >
      <form onSubmit={onSubmit} className="space-y-4" data-testid="register-form">
        <div>
          <label className="field-label">Full name</label>
          <input data-testid="register-name" className="field-input" value={form.fullName} onChange={onChange('fullName')} required />
        </div>
        <div>
          <label className="field-label">Work email</label>
          <input data-testid="register-email" className="field-input" type="email" value={form.email} onChange={onChange('email')} required />
        </div>
        <div>
          <label className="field-label">Password (min. 8 chars)</label>
          <input data-testid="register-password" className="field-input" type="password" minLength={8} value={form.password} onChange={onChange('password')} required />
        </div>
        <button data-testid="register-submit" disabled={submitting} className="btn-primary w-full justify-center">
          {submitting ? 'Creating account…' : 'Create account'}
        </button>
      </form>
    </AuthShell>
  );
}
