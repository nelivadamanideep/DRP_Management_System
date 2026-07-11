import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { AddCircleOutline } from '@mui/icons-material';
import toast from 'react-hot-toast';

import { api } from '../../app/apiClient';
import { PageHeader, Section, StatusChip, LoadingState, EmptyState } from '../../components/ui/Primitives';

const STATUS_PALETTE = {
  PLANNED: 'bg-ink-200 text-ink-700 dark:bg-ink-700 dark:text-ink-100',
  IN_PROGRESS: 'bg-accent/25 text-accent-deep dark:text-accent-soft',
  ON_HOLD: 'bg-moss-soft text-moss-deep',
  COMPLETED: 'bg-ink-900 text-white dark:bg-accent dark:text-ink-900',
  CANCELLED: 'bg-red-100 text-red-700',
};

const empty = {
  projectCode: '', title: '', summary: '', departmentId: '',
  priority: 'MEDIUM', riskLevel: 'MEDIUM', status: 'PLANNED',
  plannedStartDate: '', plannedEndDate: '', approvedBudget: 0,
};

export default function ProjectsPage() {
  const [projects, setProjects] = useState(null);
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState(empty);
  const [submitting, setSubmitting] = useState(false);

  const load = () => api.get('/projects').then((r) => setProjects(r.data));
  useEffect(() => { load(); }, []);

  const onSubmit = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    try {
      await api.post('/projects', {
        ...form,
        plannedStartDate: form.plannedStartDate || null,
        plannedEndDate: form.plannedEndDate || null,
        approvedBudget: Number(form.approvedBudget || 0),
      });
      toast.success('Project created');
      setShowForm(false);
      setForm(empty);
      load();
    } catch { /* handled */ }
    finally { setSubmitting(false); }
  };

  return (
    <>
      <PageHeader
        subtitle="Portfolio"
        title="Research projects"
        actions={
          <button data-testid="new-project-btn" onClick={() => setShowForm((v) => !v)} className="btn-primary">
            <AddCircleOutline fontSize="small" />
            {showForm ? 'Close' : 'New project'}
          </button>
        }
      />

      {showForm && (
        <Section title="Register a new project" className="mb-6">
          <form onSubmit={onSubmit} className="grid grid-cols-1 md:grid-cols-2 gap-4" data-testid="project-form">
            <Field label="Project code"><input required className="field-input" value={form.projectCode} onChange={(e) => setForm({ ...form, projectCode: e.target.value })} /></Field>
            <Field label="Title"><input required className="field-input" value={form.title} onChange={(e) => setForm({ ...form, title: e.target.value })} /></Field>
            <Field label="Priority">
              <select className="field-input" value={form.priority} onChange={(e) => setForm({ ...form, priority: e.target.value })}>
                {['LOW', 'MEDIUM', 'HIGH', 'CRITICAL'].map((v) => <option key={v}>{v}</option>)}
              </select>
            </Field>
            <Field label="Risk level">
              <select className="field-input" value={form.riskLevel} onChange={(e) => setForm({ ...form, riskLevel: e.target.value })}>
                {['LOW', 'MEDIUM', 'HIGH'].map((v) => <option key={v}>{v}</option>)}
              </select>
            </Field>
            <Field label="Status">
              <select className="field-input" value={form.status} onChange={(e) => setForm({ ...form, status: e.target.value })}>
                {['PLANNED', 'IN_PROGRESS', 'ON_HOLD', 'COMPLETED', 'CANCELLED'].map((v) => <option key={v}>{v}</option>)}
              </select>
            </Field>
            <Field label="Approved budget (₹)">
              <input type="number" className="field-input" value={form.approvedBudget} onChange={(e) => setForm({ ...form, approvedBudget: e.target.value })} />
            </Field>
            <Field label="Planned start"><input type="date" className="field-input" value={form.plannedStartDate} onChange={(e) => setForm({ ...form, plannedStartDate: e.target.value })} /></Field>
            <Field label="Planned end"><input type="date" className="field-input" value={form.plannedEndDate} onChange={(e) => setForm({ ...form, plannedEndDate: e.target.value })} /></Field>
            <div className="md:col-span-2">
              <Field label="Summary"><textarea rows={3} className="field-input" value={form.summary} onChange={(e) => setForm({ ...form, summary: e.target.value })} /></Field>
            </div>
            <div className="md:col-span-2 flex justify-end">
              <button disabled={submitting} data-testid="project-submit" className="btn-primary">
                {submitting ? 'Saving…' : 'Create project'}
              </button>
            </div>
          </form>
        </Section>
      )}

      {!projects && <LoadingState />}
      {projects && projects.length === 0 && (
        <EmptyState title="No projects yet" description="Create your first research project to begin tracking milestones, tasks, budgets and equipment usage." />
      )}
      {projects && projects.length > 0 && (
        <Section title="Portfolio">
          <div className="overflow-x-auto">
            <table className="min-w-full">
              <thead>
                <tr>
                  <th className="table-th">Code</th>
                  <th className="table-th">Title</th>
                  <th className="table-th">Priority</th>
                  <th className="table-th">Risk</th>
                  <th className="table-th">Status</th>
                  <th className="table-th">Budget</th>
                  <th className="table-th">Action</th>
                </tr>
              </thead>
              <tbody>
                {projects.map((p) => (
                  <tr key={p.id} className="hover:bg-ink-50 dark:hover:bg-ink-800/60">
                    <td className="table-td font-mono">{p.projectCode}</td>
                    <td className="table-td">{p.title}</td>
                    <td className="table-td">{p.priority}</td>
                    <td className="table-td">{p.riskLevel}</td>
                    <td className="table-td"><StatusChip value={p.status} palette={STATUS_PALETTE} /></td>
                    <td className="table-td">₹ {Number(p.approvedBudget).toLocaleString('en-IN')}</td>
                    <td className="table-td"><Link to={`/projects/${p.id}`} className="btn-outline text-xs">Open</Link></td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </Section>
      )}
    </>
  );
}

function Field({ label, children }) {
  return (
    <div>
      <label className="field-label">{label}</label>
      {children}
    </div>
  );
}
