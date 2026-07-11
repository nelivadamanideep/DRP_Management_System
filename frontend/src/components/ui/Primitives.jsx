import clsx from 'clsx';

export function PageHeader({ title, subtitle, actions }) {
  return (
    <div className="mb-8 flex flex-wrap items-end justify-between gap-4">
      <div>
        {subtitle && (
          <div className="text-[11px] uppercase tracking-widest text-ink-500 dark:text-ink-300 mb-1">
            {subtitle}
          </div>
        )}
        <h1 className="font-display text-3xl lg:text-4xl">{title}</h1>
      </div>
      {actions && <div className="flex items-center gap-2 flex-wrap">{actions}</div>}
    </div>
  );
}

export function EmptyState({ icon, title, description, action }) {
  return (
    <div className="surface p-10 text-center">
      {icon && <div className="mx-auto mb-4 text-ink-400">{icon}</div>}
      <h3 className="font-display text-xl mb-1">{title}</h3>
      <p className="text-sm text-ink-500 dark:text-ink-300 mb-5">{description}</p>
      {action}
    </div>
  );
}

export function Section({ title, subtitle, children, className }) {
  return (
    <section className={clsx('surface p-6 lg:p-8', className)}>
      {(title || subtitle) && (
        <header className="mb-6">
          {subtitle && (
            <div className="text-[11px] uppercase tracking-widest text-ink-500 dark:text-ink-300 mb-1">
              {subtitle}
            </div>
          )}
          {title && <h2 className="font-display text-2xl">{title}</h2>}
        </header>
      )}
      {children}
    </section>
  );
}

export function StatCard({ label, value, hint, tone = 'default' }) {
  const toneClasses = {
    default: 'from-ink-100 to-white dark:from-ink-800 dark:to-ink-900',
    accent: 'from-accent/25 to-accent-soft/40 dark:from-accent/30 dark:to-accent/10',
    moss: 'from-moss-soft/70 to-white dark:from-moss/40 dark:to-ink-900',
    warn: 'from-red-100 to-white dark:from-red-900/40 dark:to-ink-900',
  }[tone];

  return (
    <div className={clsx('surface relative overflow-hidden p-6 bg-gradient-to-br animate-float-in', toneClasses)}>
      <div className="text-[11px] uppercase tracking-widest text-ink-500 dark:text-ink-300">{label}</div>
      <div className="font-display text-3xl mt-2 leading-tight">{value}</div>
      {hint && <div className="text-xs text-ink-500 dark:text-ink-300 mt-2">{hint}</div>}
    </div>
  );
}

export function StatusChip({ value, palette }) {
  const map = palette || {};
  const style = map[value] || 'bg-ink-200 text-ink-700 dark:bg-ink-700 dark:text-ink-100';
  return <span className={clsx('chip', style)}>{value || '—'}</span>;
}

export function LoadingState({ label = 'Loading…' }) {
  return (
    <div className="flex items-center gap-3 text-sm text-ink-500 dark:text-ink-300 py-8">
      <span className="h-2.5 w-2.5 rounded-full bg-accent animate-pulse" />
      {label}
    </div>
  );
}
