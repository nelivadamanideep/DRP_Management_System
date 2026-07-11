import { useEffect, useState } from 'react';
import toast from 'react-hot-toast';

import { api } from '../../app/apiClient';
import { PageHeader, Section, LoadingState } from '../../components/ui/Primitives';

export default function BudgetPage() {
  const [projects, setProjects] = useState([]);
  const [selected, setSelected] = useState('');
  const [summary, setSummary] = useState(null);
  const [expenses, setExpenses] = useState([]);
  const [allocForm, setAllocForm] = useState({ fiscalYear: new Date().getFullYear(), category: 'MANPOWER', allocatedAmount: 0, notes: '' });
  const [expForm, setExpForm] = useState({ category: 'MANPOWER', amount: 0, description: '' });

  useEffect(() => { api.get('/projects').then((r) => setProjects(r.data)); }, []);

  useEffect(() => {
    if (!selected) return;
    api.get(`/budgets/projects/${selected}/summary`).then((r) => setSummary(r.data));
    api.get(`/budgets/projects/${selected}/expenses`).then((r) => setExpenses(r.data));
  }, [selected]);

  const allocate = async (e) => {
    e.preventDefault();
    try {
      await api.post('/budgets/allocations', { projectId: selected, ...allocForm, allocatedAmount: Number(allocForm.allocatedAmount || 0) });
      toast.success('Allocation added');
      api.get(`/budgets/projects/${selected}/summary`).then((r) => setSummary(r.data));
    } catch {}
  };
  const recordExp = async (e) => {
    e.preventDefault();
    try {
      await api.post('/budgets/expenses', { projectId: selected, ...expForm, amount: Number(expForm.amount || 0) });
      toast.success('Expense recorded');
      api.get(`/budgets/projects/${selected}/summary`).then((r) => setSummary(r.data));
      api.get(`/budgets/projects/${selected}/expenses`).then((r) => setExpenses(r.data));
    } catch {}
  };

  return (
    <>
      <PageHeader subtitle="Finance" title="Budget & expenses" />
      <Section title="Choose a project" className="mb-6">
        <select className="field-input" value={selected} onChange={(e) => setSelected(e.target.value)}>
          <option value="">Select a project…</option>
          {projects.map((p) => <option key={p.id} value={p.id}>{p.projectCode} — {p.title}</option>)}
        </select>
      </Section>

      {!selected && <p className="text-sm text-ink-500 dark:text-ink-300">Pick a project above to view its budget.</p>}

      {selected && !summary && <LoadingState />}

      {summary && (
        <>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
            <div className="surface p-6"><div className="text-[11px] uppercase tracking-widest text-ink-500 dark:text-ink-300">Allocated</div><div className="font-display text-3xl mt-1">₹ {Number(summary.totalAllocated).toLocaleString('en-IN')}</div></div>
            <div className="surface p-6"><div className="text-[11px] uppercase tracking-widest text-ink-500 dark:text-ink-300">Spent</div><div className="font-display text-3xl mt-1">₹ {Number(summary.totalSpent).toLocaleString('en-IN')}</div></div>
            <div className="surface p-6"><div className="text-[11px] uppercase tracking-widest text-ink-500 dark:text-ink-300">Remaining</div><div className="font-display text-3xl mt-1">₹ {Number(summary.remaining).toLocaleString('en-IN')}</div></div>
          </div>

          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <Section title="Add an allocation">
              <form onSubmit={allocate} className="grid grid-cols-2 gap-3">
                <input type="number" className="field-input" placeholder="Year" value={allocForm.fiscalYear} onChange={(e) => setAllocForm({ ...allocForm, fiscalYear: Number(e.target.value) })} />
                <select className="field-input" value={allocForm.category} onChange={(e) => setAllocForm({ ...allocForm, category: e.target.value })}>
                  {['MANPOWER', 'EQUIPMENT', 'CONSUMABLES', 'TRAVEL', 'MISC'].map((v) => <option key={v}>{v}</option>)}
                </select>
                <input type="number" className="field-input col-span-2" placeholder="Amount" value={allocForm.allocatedAmount} onChange={(e) => setAllocForm({ ...allocForm, allocatedAmount: e.target.value })} />
                <button className="btn-primary col-span-2">Allocate</button>
              </form>
            </Section>

            <Section title="Record an expense">
              <form onSubmit={recordExp} className="grid grid-cols-2 gap-3">
                <select className="field-input col-span-2" value={expForm.category} onChange={(e) => setExpForm({ ...expForm, category: e.target.value })}>
                  {['MANPOWER', 'EQUIPMENT', 'CONSUMABLES', 'TRAVEL', 'MISC'].map((v) => <option key={v}>{v}</option>)}
                </select>
                <input type="number" className="field-input" placeholder="Amount" value={expForm.amount} onChange={(e) => setExpForm({ ...expForm, amount: e.target.value })} />
                <input className="field-input" placeholder="Description" value={expForm.description} onChange={(e) => setExpForm({ ...expForm, description: e.target.value })} />
                <button className="btn-primary col-span-2">Record</button>
              </form>
            </Section>
          </div>

          <Section title="Allocations" className="mt-6">
            <table className="min-w-full">
              <thead><tr><th className="table-th">Year</th><th className="table-th">Category</th><th className="table-th">Allocated</th><th className="table-th">Spent</th><th className="table-th">Remaining</th></tr></thead>
              <tbody>
                {summary.allocations.map((a) => (
                  <tr key={a.id}>
                    <td className="table-td">{a.fiscalYear}</td>
                    <td className="table-td">{a.category}</td>
                    <td className="table-td">₹ {Number(a.allocatedAmount).toLocaleString('en-IN')}</td>
                    <td className="table-td">₹ {Number(a.spentAmount).toLocaleString('en-IN')}</td>
                    <td className="table-td">₹ {Number(a.remainingAmount).toLocaleString('en-IN')}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </Section>

          {expenses.length > 0 && (
            <Section title="Recent expenses" className="mt-6">
              <ul className="text-sm divide-y divide-ink-100 dark:divide-ink-800">
                {expenses.map((e) => (
                  <li key={e.id} className="py-2 flex justify-between">
                    <span>{e.description || e.category}</span>
                    <span className="font-mono">₹ {Number(e.amount).toLocaleString('en-IN')} · {e.expenseDate}</span>
                  </li>
                ))}
              </ul>
            </Section>
          )}
        </>
      )}
    </>
  );
}
