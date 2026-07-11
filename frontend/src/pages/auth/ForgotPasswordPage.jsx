import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';

import { AuthShell } from '../../components/layout/AuthShell';
import { api } from '../../app/apiClient';

export default function ForgotPasswordPage() {
  const [email, setEmail] = useState('');
  const [otp, setOtp] = useState('');
  const [step, setStep] = useState(1);
  const [submitting, setSubmitting] = useState(false);
  const navigate = useNavigate();

  const sendOtp = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    try {
      const res = await api.post('/auth/forgot-password', { email });
      toast.success(res.data.otp ? `OTP dispatched · (demo) code: ${res.data.otp}` : 'OTP dispatched');
      setStep(2);
    } catch { /* handled */ }
    finally { setSubmitting(false); }
  };

  const verify = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    try {
      const res = await api.post('/auth/verify-otp', { email, otp });
      navigate('/reset-password', { state: { email, resetToken: res.data.resetToken } });
    } catch { /* handled */ }
    finally { setSubmitting(false); }
  };

  return (
    <AuthShell
      eyebrow="ERPMS · Reset password"
      title={step === 1 ? 'Forgot your password?' : 'Enter the OTP'}
      subtitle={step === 1
        ? 'We\'ll email a 6-digit verification code to your registered address.'
        : `We sent a code to ${email}. Enter it below to continue.`}
      footer={<>Remembered it? <Link className="text-accent-deep dark:text-accent font-semibold" to="/login">Back to sign in</Link></>}
    >
      {step === 1 ? (
        <form onSubmit={sendOtp} className="space-y-4">
          <div>
            <label className="field-label">Work email</label>
            <input data-testid="forgot-email" type="email" className="field-input" value={email} onChange={(e) => setEmail(e.target.value)} required />
          </div>
          <button data-testid="forgot-submit" disabled={submitting} className="btn-primary w-full justify-center">
            {submitting ? 'Sending…' : 'Send verification code'}
          </button>
        </form>
      ) : (
        <form onSubmit={verify} className="space-y-4">
          <div>
            <label className="field-label">6-digit code</label>
            <input
              data-testid="otp-input"
              inputMode="numeric"
              pattern="[0-9]{6}"
              maxLength={6}
              className="field-input tracking-[0.6em] text-center font-mono text-lg"
              value={otp}
              onChange={(e) => setOtp(e.target.value)}
              required
            />
          </div>
          <button data-testid="otp-submit" disabled={submitting} className="btn-primary w-full justify-center">
            {submitting ? 'Verifying…' : 'Verify code'}
          </button>
        </form>
      )}
    </AuthShell>
  );
}
