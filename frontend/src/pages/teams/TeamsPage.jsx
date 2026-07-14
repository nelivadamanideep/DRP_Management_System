import { useEffect, useState } from 'react';
import { AddCircleOutline, DeleteOutline } from '@mui/icons-material';
import toast from 'react-hot-toast';

import { api } from '../../app/apiClient';
import { PageHeader, Section, LoadingState } from '../../components/ui/Primitives';

export default function TeamsPage() {
  const [rows, setRows] = useState(null);
  const [projects, setProjects] = useState([]);
  const [users, setUsers] = useState([]);
  const [form, setForm] = useState({ projectId: '', userId: '', roleInProject: 'SCIENTIST', allocationPercent: 100, active: true });

  const load = () => api.get('/project-teams').then((r) => setRows(r.data));
  useEffect(() => {
    load();
    api.get('/projects').then((r) => setProjects(r.data)).catch(() => setProjects([]));
    api.get('/users').then((r) => setUsers(r.data)).catch(() => setUsers([]));
  }, []);

  const create = async (e) => {
    e.preventDefault();
    try { await api.post('/project-teams', form); toast.success('Assigned'); load(); }
    catch {}
  };

  const deleteTeam = async (teamId) => {
    try {
      await api.delete(`/project-teams/${teamId}`);
      setRows(rows.filter(t => t.id !== teamId));
      toast.success('Team member removed successfully');
    } catch (error) {
      toast.error('Failed to remove team member');
    }
  };

  return (
    <>
      <PageHeader subtitle="People" title="Project teams" />
      <Section title="Assign a team member" className="mb-6">
        <form onSubmit={create} className="grid grid-cols-1 md:grid-cols-5 gap-3">
          <select className="field-input" value={form.projectId} onChange={(e) => setForm({ ...form, projectId: e.target.value })} required>
            <option value="">Project…</option>
            {projects.map((p) => <option key={p.id} value={p.id}>{p.projectCode}</option>)}
          </select>
          <select className="field-input" value={form.userId} onChange={(e) => setForm({ ...form, userId: e.target.value })} required>
            <option value="">User…</option>
            {users.map((u) => <option key={u.id} value={u.id}>{u.fullName}</option>)}
          </select>
          <select className="field-input" value={form.roleInProject} onChange={(e) => setForm({ ...form, roleInProject: e.target.value })}>
            {['SCIENTIST', 'RESEARCH_ENGINEER', 'LEAD', 'REVIEWER', 'CONTRIBUTOR'].map((r) => <option key={r}>{r}</option>)}
          </select>
          <input type="number" min={1} max={100} className="field-input" value={form.allocationPercent} onChange={(e) => setForm({ ...form, allocationPercent: Number(e.target.value) })} />
          <button className="btn-primary justify-center"><AddCircleOutline fontSize="small" />Assign</button>
        </form>
      </Section>

      {!rows ? <LoadingState /> : (
        <Section title="Assignments">
          <table className="min-w-full">
            <thead><tr><th className="table-th">Project</th><th className="table-th">User</th><th className="table-th">Role</th><th className="table-th">Allocation</th><th className="table-th">Active</th><th className="table-th">Actions</th></tr></thead>
            <tbody>
              {rows.map((t) => (
                <tr key={t.id}>
                  <td className="table-td font-mono text-xs">{t.projectId.slice(0, 10)}…</td>
                  <td className="table-td font-mono text-xs">{t.userId.slice(0, 10)}…</td>
                  <td className="table-td">{t.roleInProject}</td>
                  <td className="table-td">{t.allocationPercent}%</td>
                  <td className="table-td">{t.active ? '✅' : '—'}</td>
                  <td className="table-td"><button onClick={() => deleteTeam(t.id)} className="btn-outline text-xs text-red-600 hover:bg-red-50 dark:hover:bg-red-900/20"><DeleteOutline fontSize="small" /></button></td>
                </tr>
              ))}
            </tbody>
          </table>
        </Section>
      )}
    </>
  );
}
