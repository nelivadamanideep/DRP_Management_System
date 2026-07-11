import { useEffect, useState } from 'react';
import {
  ResponsiveContainer, BarChart, Bar, XAxis, YAxis, Tooltip, CartesianGrid, PieChart, Pie, Cell, Legend,
} from 'recharts';

import { api } from '../app/apiClient';
import { PageHeader, StatCard, Section, LoadingState } from '../components/ui/Primitives';

const STATUS_COLORS = {
  PLANNED: '#94a1c0',
  IN_PROGRESS: '#e2b23d',
  ON_HOLD: '#5b8a5a',
  COMPLETED: '#3e4e75',
  CANCELLED: '#c94b4b',
};

const currency = (n) => new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR', maximumFractionDigits: 0 }).format(Number(n || 0));

export default function DashboardPage() {
  const [summary, setSummary] = useState(null);

  useEffect(() => {
    api.get('/dashboard/summary').then((res) => setSummary(res.data)).catch(() => {});
  }, []);

  if (!summary) return <LoadingState label="Assembling dashboard…" />;

  const statusData = Object.entries(summary.projectsByStatus || {}).map(([status, count]) => ({
    status, count, fill: STATUS_COLORS[status] || '#94a1c0',
  }));

  const budgetData = [
    { name: 'Spent', value: Number(summary.totalExpenses || 0), fill: '#e2b23d' },
    {
      name: 'Remaining',
      value: Math.max(Number(summary.totalApprovedBudget || 0) - Number(summary.totalExpenses || 0), 0),
      fill: '#3e4e75',
    },
  ];

  return (
    <>
      <PageHeader
        subtitle="Home"
        title="Programme cockpit"
      />

      <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-4 gap-4 mb-8">
        <StatCard label="Active projects" value={summary.totalProjects} hint={`${summary.projectsByStatus?.IN_PROGRESS || 0} in progress`} tone="accent" />
        <StatCard label="Open tasks" value={summary.openTasks} hint={`${summary.myOpenTasks} assigned to you`} />
        <StatCard label="Equipment" value={`${summary.availableEquipment}/${summary.totalEquipment}`} hint="Available / total" tone="moss" />
        <StatCard label="Low stock alerts" value={summary.lowStockItems} hint={`${summary.pendingPurchaseRequests} PRs pending`} tone={summary.lowStockItems > 0 ? 'warn' : 'default'} />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <Section title="Projects by status" subtitle="Portfolio" className="lg:col-span-2">
          <div className="h-72">
            <ResponsiveContainer>
              <BarChart data={statusData} margin={{ top: 10, right: 10, left: 0, bottom: 0 }}>
                <CartesianGrid strokeDasharray="3 3" stroke="rgba(148,161,192,0.2)" />
                <XAxis dataKey="status" tick={{ fontSize: 12 }} />
                <YAxis allowDecimals={false} tick={{ fontSize: 12 }} />
                <Tooltip cursor={{ fill: 'rgba(148,161,192,0.15)' }} />
                <Bar dataKey="count" radius={[8, 8, 0, 0]}>
                  {statusData.map((d) => (<Cell key={d.status} fill={d.fill} />))}
                </Bar>
              </BarChart>
            </ResponsiveContainer>
          </div>
        </Section>

        <Section title="Budget deployment" subtitle="All programmes">
          <div className="h-72">
            <ResponsiveContainer>
              <PieChart>
                <Pie data={budgetData} dataKey="value" nameKey="name" innerRadius={60} outerRadius={90} paddingAngle={4}>
                  {budgetData.map((entry, i) => (<Cell key={i} fill={entry.fill} />))}
                </Pie>
                <Legend />
                <Tooltip formatter={(v) => currency(v)} />
              </PieChart>
            </ResponsiveContainer>
          </div>
          <div className="grid grid-cols-2 gap-3 mt-4 text-sm">
            <div>
              <div className="text-[11px] uppercase tracking-widest text-ink-500 dark:text-ink-300">Approved</div>
              <div className="font-display text-xl">{currency(summary.totalApprovedBudget)}</div>
            </div>
            <div>
              <div className="text-[11px] uppercase tracking-widest text-ink-500 dark:text-ink-300">Expenses</div>
              <div className="font-display text-xl">{currency(summary.totalExpenses)}</div>
            </div>
          </div>
        </Section>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mt-6">
        <StatCard label="Total users" value={summary.totalUsers} hint={`${summary.activeDepartments} active departments`} />
        <StatCard label="Unread notifications" value={summary.unreadNotifications} hint="Open the bell in your header" />
      </div>
    </>
  );
}
