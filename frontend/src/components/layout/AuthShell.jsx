import { Link } from 'react-router-dom';
import { motion } from 'framer-motion';

export function AuthShell({ eyebrow, title, subtitle, children, footer }) {
  return (
    <div className="min-h-screen grid lg:grid-cols-2 relative overflow-hidden">
      <div className="hidden lg:flex flex-col justify-between p-14 relative bg-ink-900 text-ink-100 overflow-hidden">
        <div className="absolute inset-0 opacity-40 bg-noise pointer-events-none" />
        <div className="absolute -bottom-40 -right-40 h-[500px] w-[500px] rounded-full bg-accent/25 blur-3xl" />
        <div className="absolute -top-40 -left-40 h-[420px] w-[420px] rounded-full bg-moss/30 blur-3xl" />
        <div className="relative z-10">
          <div className="flex items-center gap-3">
            <div className="h-14 w-16 rounded-2xl bg-accent grid place-items-center text-ink-900 font-display font-bold text-x1">
              DRDO
            </div>
            <div>
              <div className="font-display text-xl leading-tight">Defence Research Project Management System</div>
              <div className="text-[11px] uppercase tracking-widest text-ink-300">
                Research Platform
              </div>
            </div>
          </div>
        </div>
        <div className="relative z-10 space-y-6 max-w-md">
          <h2 className="font-display text-4xl leading-tight">
            Run every research programme with clarity.
          </h2>
          <p className="text-ink-300 text-sm leading-relaxed">
            Projects, milestones, labs, equipment, procurement and budgets — unified,
            audited and augmented by an on-tap AI assistant.
          </p>
          <div className="grid grid-cols-3 gap-4 pt-4">
            {['Projects', 'Equipment', 'Budgets'].map((label) => (
              <div key={label} className="rounded-2xl bg-ink-800/60 backdrop-blur p-4">
                <div className="text-[10px] uppercase tracking-widest text-ink-300">Module</div>
                <div className="font-display text-lg mt-1">{label}</div>
              </div>
            ))}
          </div>
        </div>
        <div className="relative z-10 text-xs text-ink-400">
          © {new Date().getFullYear()} Defence Research Project Management System. All rights reserved.
        </div>
      </div>

      <div className="flex items-center justify-center px-6 py-14 lg:px-16">
        <motion.div
          initial={{ opacity: 0, y: 12 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.28, ease: 'easeOut' }}
          className="w-full max-w-md surface p-8 lg:p-10"
        >
          {eyebrow && (
            <div className="text-[11px] uppercase tracking-widest text-ink-500 dark:text-ink-300 mb-2">
              {eyebrow}
            </div>
          )}
          <h1 className="font-display text-3xl mb-2">{title}</h1>
          {subtitle && <p className="text-sm text-ink-500 dark:text-ink-300 mb-8">{subtitle}</p>}
          {children}
          {footer && (
            <div className="text-sm text-ink-500 dark:text-ink-300 mt-6 text-center">{footer}</div>
          )}
          <div className="mt-8 text-center">
            <Link to="/" className="text-xs text-ink-500 dark:text-ink-300 hover:text-accent">
              ← Back to dashboard
            </Link>
          </div>
        </motion.div>
      </div>
    </div>
  );
}
