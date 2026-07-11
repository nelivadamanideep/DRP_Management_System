import { useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';

import { AuthShell } from '../../components/layout/AuthShell';
import { api } from '../../app/apiClient';

export default function ResetPasswordPage() {
  const location = useLocation();
  const navigate = useNavigate();
  const { email = '', resetToken = '' } = location.state || {};
  const [newPassword, setNewPassword] = useState('');
  const [confirm, setConfirm] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const onSubmit = async (e) => {
    e.preventDefault();
    if (newPassword !== confirm) {
      toast.error('Passwords do not match');
      return;
    }
    setSubmitting(true);
    try {
      await api.post('/auth/reset-password', { email, resetToken, newPassword });
      toast.success('Password reset. Please sign in.');
      navigate('/login');
    } catch { /* handled */ }
    finally { setSubmitting(false); }
  };

  if (!email || !resetToken) {
    return (
      <AuthShell title="Reset link required" subtitle="This page must be reached after OTP verification.">
        <Link to="/forgot-password" className="btn-primary w-full justify-center">Start over</Link>
      </AuthShell>
    );
  }

  return (
    <AuthShell
      eyebrow="ERPMS · Reset password"
      title="Set a new password"
      subtitle={`Choose a strong password for ${email}. Signing you in after this.`}
      footer={<>Changed your mind? <Link className="text-accent-deep dark:text-accent font-semibold" to="/login">Back to sign in</Link></>}
    >
      <form onSubmit={onSubmit} className="space-y-4">
        <div>
          <label className="field-label">New password (min. 8 chars)</label>
          <input data-testid="reset-password" type="password" minLength={8} className="field-input"
                 value={newPassword} onChange={(e) => setNewPassword(e.target.value)} required />
        </div>
        <div>
          <label className="field-label">Confirm password</label>
          <input data-testid="reset-confirm" type="password" minLength={8} className="field-input"
                 value={confirm} onChange={(e) => setConfirm(e.target.value)} required />
        </div>
        <button data-testid="reset-submit" disabled={submitting} className="btn-primary w-full justify-center">
          {submitting ? 'Saving…' : 'Reset password'}
        </button>
      </form>
    </AuthShell>
  );
}
