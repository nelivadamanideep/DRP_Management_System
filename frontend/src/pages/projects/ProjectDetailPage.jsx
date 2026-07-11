import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { ArrowBack, AutoAwesomeOutlined } from '@mui/icons-material';
import toast from 'react-hot-toast';

import { api } from '../../app/apiClient';
import { PageHeader, Section, StatusChip, LoadingState } from '../../components/ui/Primitives';

export default function ProjectDetailPage() {
  const { id } = useParams();
  const [project, setProject] = useState(null);
  const [milestones, setMilestones] = useState([]);
  const [tasks, setTasks] = useState([]);
  const [team, setTeam] = useState([]);
  const [insight, setInsight] = useState(null);
  const [loadingAi, setLoadingAi] = useState(false);

  useEffect(() => {
    api.get(`/projects/${id}`).then((r) => setProject(r.data)).catch(() => {});
    api.get(`/milestones/project/${id}`).then((r) => setMilestones(r.data)).catch(() => setMilestones([]));
    api.get(`/tasks/project/${id}`).then((r) => setTasks(r.data)).catch(() => setTasks([]));
    api.get(`/project-teams/project/${id}`).then((r) => setTeam(r.data)).catch(() => setTeam([]));
  }, [id]);

  const runAi = async (kind) => {
    setLoadingAi(true);
    try {
      const url = kind === 'risk'
        ? `/ai/projects/${id}/risk`
        : kind === 'delay'
          ? `/ai/projects/${id}/delay`
          : `/ai/projects/${id}/budget-forecast`;
      const res = await api.get(url);
      setInsight(res.data);
      toast.success('AI insight generated');
    } catch { /* handled */ }
    finally { setLoadingAi(false); }
  };

  if (!project) return <LoadingState />;

  return (
    <>
      <Link to="/projects" className="btn-ghost mb-4 text-xs"><ArrowBack fontSize="inherit" /> Back to portfolio</Link>
      <PageHeader
        subtitle={project.projectCode}
        title={project.title}
        actions={
          <div className="flex gap-2">
            <button className="btn-outline" onClick={() => runAi('risk')} disabled={loadingAi}><AutoAwesomeOutlined fontSize="small" />Risk</button>
            <button className="btn-outline" onClick={() => runAi('delay')} disabled={loadingAi}><AutoAwesomeOutlined fontSize="small" />Delay</button>
            <button className="btn-primary" onClick={() => runAi('budget')} disabled={loadingAi}><AutoAwesomeOutlined fontSize="small" />Budget forecast</button>
          </div>
        }
      />

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 mb-6">
        <Section title="Overview" subtitle="Project card">
          <p className="text-sm text-ink-600 dark:text-ink-300 whitespace-pre-wrap">{project.summary || 'No summary provided.'}</p>
          <dl className="mt-4 grid grid-cols-2 gap-3 text-sm">
            <Info label="Priority" value={project.priority} />
            <Info label="Risk level" value={project.riskLevel} />
            <Info label="Status" value={<StatusChip value={project.status} />} />
            <Info label="Budget" value={`₹ ${Number(project.approvedBudget).toLocaleString('en-IN')}`} />
            <Info label="Planned start" value={project.plannedStartDate || '—'} />
            <Info label="Planned end" value={project.plannedEndDate || '—'} />
          </dl>
        </Section>

        <Section title="Milestones" subtitle={`${milestones.length} total`}>
          {milestones.length === 0
            ? <p className="text-sm text-ink-500 dark:text-ink-300">No milestones yet.</p>
            : milestones.map((m) => (
              <div key={m.id} className="py-3 border-b last:border-0 border-ink-100 dark:border-ink-800">
                <div className="flex justify-between items-center">
                  <div className="font-semibold text-sm">{m.name}</div>
                  <StatusChip value={m.status} />
                </div>
                <div className="text-xs text-ink-500 dark:text-ink-300 mt-1">
                  Due {m.dueDate || '—'} · {m.progressPercent}% complete
                </div>
              </div>
            ))}
        </Section>

        <Section title="Team" subtitle={`${team.length} members`}>
          {team.length === 0
            ? <p className="text-sm text-ink-500 dark:text-ink-300">No team members allocated.</p>
            : team.map((t) => (
              <div key={t.id} className="py-2 border-b last:border-0 border-ink-100 dark:border-ink-800 flex justify-between items-center text-sm">
                <span className="font-mono text-xs">{t.userId.slice(0, 8)}…</span>
                <span>{t.roleInProject}</span>
                <span className="chip bg-ink-100 dark:bg-ink-800 text-ink-600 dark:text-ink-300">{t.allocationPercent}%</span>
              </div>
            ))}
        </Section>
      </div>

      <Section title="Tasks" subtitle={`${tasks.length} total`}>
        {tasks.length === 0
          ? <p className="text-sm text-ink-500 dark:text-ink-300">No tasks yet.</p>
          : (
            <table className="min-w-full">
              <thead><tr>
                <th className="table-th">Title</th>
                <th className="table-th">Priority</th>
                <th className="table-th">Status</th>
                <th className="table-th">Due</th>
                <th className="table-th">Progress</th>
              </tr></thead>
              <tbody>
                {tasks.map((t) => (
                  <tr key={t.id}>
                    <td className="table-td">{t.title}</td>
                    <td className="table-td">{t.priority}</td>
                    <td className="table-td"><StatusChip value={t.status} /></td>
                    <td className="table-td">{t.dueDate || '—'}</td>
                    <td className="table-td">{t.progressPercent}%</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
      </Section>

      {insight && (
        <Section title={`AI insight · ${insight.kind}`} subtitle="Powered by Claude" className="mt-6">
          <pre className="whitespace-pre-wrap text-sm text-ink-700 dark:text-ink-200">{insight.content}</pre>
        </Section>
      )}
    </>
  );
}

function Info({ label, value }) {
  return (
    <div>
      <dt className="text-[11px] uppercase tracking-widest text-ink-500 dark:text-ink-300">{label}</dt>
      <dd className="mt-1 font-semibold">{value}</dd>
    </div>
  );
}
