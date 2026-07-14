import { useEffect, useState } from 'react';
import { DeleteOutline } from '@mui/icons-material';
import toast from 'react-hot-toast';

import { api } from '../../app/apiClient';
import { PageHeader, Section, LoadingState } from '../../components/ui/Primitives';

const COLUMNS = ['TODO', 'IN_PROGRESS', 'IN_REVIEW', 'DONE'];
const COLUMN_COLORS = {
  TODO: 'bg-ink-200 text-ink-700 dark:bg-ink-700 dark:text-ink-100',
  IN_PROGRESS: 'bg-accent/25 text-accent-deep dark:text-accent-soft',
  IN_REVIEW: 'bg-moss-soft text-moss-deep',
  DONE: 'bg-ink-900 text-white dark:bg-accent dark:text-ink-900',
};

export default function TasksBoardPage() {
  const [tasks, setTasks] = useState(null);
  const [projects, setProjects] = useState([]);
  const [form, setForm] = useState({ projectId: '', title: '', priority: 'MEDIUM', status: 'TODO' });

  const load = () => api.get('/tasks').then((r) => setTasks(r.data));
  useEffect(() => {
    load();
    api.get('/projects').then((r) => setProjects(r.data)).catch(() => setProjects([]));
  }, []);

  const create = async (e) => {
    e.preventDefault();
    if (!form.projectId || !form.title) return toast.error('Project and title are required');
    try {
      await api.post('/tasks', {
        ...form,
        milestoneId: null,
        assignedToUserId: null,
        description: '',
        dueDate: null,
        progressPercent: 0,
      });
      toast.success('Task created');
      setForm({ ...form, title: '' });
      load();
    } catch { /* handled */ }
  };

  const moveTask = async (task, newStatus) => {
    try {
      await api.put(`/tasks/${task.id}`, {
        projectId: task.projectId,
        milestoneId: task.milestoneId,
        title: task.title,
        description: task.description,
        assignedToUserId: task.assignedToUserId,
        priority: task.priority,
        status: newStatus,
        dueDate: task.dueDate,
        progressPercent: newStatus === 'DONE' ? 100 : task.progressPercent,
      });
      load();
    } catch { /* handled */ }
  };

  const deleteTask = async (taskId) => {
    try {
      await api.delete(`/tasks/${taskId}`);
      setTasks(tasks.filter(t => t.id !== taskId));
      toast.success('Task deleted successfully');
    } catch (error) {
      toast.error('Failed to delete task');
    }
  };

  if (!tasks) return <LoadingState />;
  const grouped = COLUMNS.reduce((acc, col) => ({ ...acc, [col]: tasks.filter((t) => t.status === col) }), {});

  return (
    <>
      <PageHeader subtitle="Delivery" title="Tasks Kanban" />
      <Section title="Quick add" className="mb-6">
        <form onSubmit={create} className="grid grid-cols-1 md:grid-cols-6 gap-3">
          <select className="field-input md:col-span-2" value={form.projectId} onChange={(e) => setForm({ ...form, projectId: e.target.value })}>
            <option value="">Select project…</option>
            {projects.map((p) => <option key={p.id} value={p.id}>{p.projectCode} — {p.title}</option>)}
          </select>
          <input className="field-input md:col-span-2" placeholder="Task title" value={form.title} onChange={(e) => setForm({ ...form, title: e.target.value })} />
          <select className="field-input" value={form.priority} onChange={(e) => setForm({ ...form, priority: e.target.value })}>
            {['LOW', 'MEDIUM', 'HIGH', 'CRITICAL'].map((p) => <option key={p}>{p}</option>)}
          </select>
          <button data-testid="task-add-submit" className="btn-primary justify-center">Add task</button>
        </form>
      </Section>

      <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-4 gap-4">
        {COLUMNS.map((col) => (
          <Section key={col} title={col.replace('_', ' ')} subtitle={`${grouped[col].length} card${grouped[col].length === 1 ? '' : 's'}`}>
            <div className="space-y-3 min-h-24">
              {grouped[col].length === 0 && <div className="text-xs text-ink-500 dark:text-ink-300 py-6 text-center">No tasks here yet.</div>}
              {grouped[col].map((t) => (
                <div key={t.id} className="p-3 rounded-xl border border-ink-200 dark:border-ink-700 bg-white dark:bg-ink-800">
                  <div className="text-sm font-semibold">{t.title}</div>
                  <div className="flex justify-between items-center mt-2 text-xs">
                    <span className={`chip ${COLUMN_COLORS[t.status]}`}>{t.priority}</span>
                    <div className="flex gap-2 items-center">
                      <select className="text-xs bg-transparent focus:outline-none" value={t.status} onChange={(e) => moveTask(t, e.target.value)}>
                        {COLUMNS.map((c) => <option key={c} value={c}>{c}</option>)}
                      </select>
                      <button onClick={() => deleteTask(t.id)} className="text-red-600 hover:text-red-800 dark:text-red-400 dark:hover:text-red-300 transition-colors" title="Delete task">
                        <DeleteOutline fontSize="small" />
                      </button>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </Section>
        ))}
      </div>
    </>
  );
}
