import { useEffect, useState } from 'react';
import { AddCircleOutline, DeleteOutline } from '@mui/icons-material';
import toast from 'react-hot-toast';

import { api } from '../../app/apiClient';
import { PageHeader, Section, LoadingState } from '../../components/ui/Primitives';

export default function DepartmentsPage() {
  const [rows, setRows] = useState(null);
  const [form, setForm] = useState({ code: '', name: '', description: '', active: true });
  const load = () => api.get('/departments').then((r) => setRows(r.data));
  useEffect(() => { load(); }, []);

  const create = async (e) => {
    e.preventDefault();
    try { await api.post('/departments', form); toast.success('Created'); setForm({ code: '', name: '', description: '', active: true }); load(); }
    catch {}
  };

  const deleteDepartment = async (deptId) => {
    try {
      await api.delete(`/departments/${deptId}`);
      setRows(rows.filter(d => d.id !== deptId));
      toast.success('Department deleted successfully');
    } catch (error) {
      toast.error('Failed to delete department');
    }
  };

  return (
    <>
      <PageHeader subtitle="Organisation" title="Departments" />
      <Section title="Register a department" className="mb-6">
        <form onSubmit={create} className="grid grid-cols-1 md:grid-cols-4 gap-3">
          <input className="field-input" placeholder="Code (e.g. R&D)" value={form.code} onChange={(e) => setForm({ ...form, code: e.target.value })} required />
          <input className="field-input md:col-span-2" placeholder="Department name" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} required />
          <button className="btn-primary justify-center"><AddCircleOutline fontSize="small" />Add</button>
          <input className="field-input md:col-span-4" placeholder="Description" value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} />
        </form>
      </Section>

      {!rows ? <LoadingState /> : (
        <Section title="Registered departments">
          <table className="min-w-full">
            <thead><tr><th className="table-th">Code</th><th className="table-th">Name</th><th className="table-th">Description</th><th className="table-th">Active</th><th className="table-th">Actions</th></tr></thead>
            <tbody>
              {rows.map((d) => (
                <tr key={d.id}>
                  <td className="table-td font-mono">{d.code}</td>
                  <td className="table-td">{d.name}</td>
                  <td className="table-td text-ink-500 dark:text-ink-300">{d.description}</td>
                  <td className="table-td">{d.active ? '✅' : '⛔️'}</td>
                  <td className="table-td"><button onClick={() => deleteDepartment(d.id)} className="btn-outline text-xs text-red-600 hover:bg-red-50 dark:hover:bg-red-900/20"><DeleteOutline fontSize="small" /></button></td>
                </tr>
              ))}
            </tbody>
          </table>
        </Section>
      )}
    </>
  );
}
