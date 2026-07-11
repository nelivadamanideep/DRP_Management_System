import { useEffect, useState } from 'react';
import { api } from '../../app/apiClient';
import { PageHeader, Section, LoadingState } from '../../components/ui/Primitives';

export default function NotificationsPage() {
  const [data, setData] = useState(null);
  const load = () => api.get('/notifications', { params: { page: 0, size: 50 } }).then((r) => setData(r.data));
  useEffect(load, []);

  const markAll = async () => { await api.post('/notifications/read-all'); load(); };
  const mark = async (id) => { await api.post(`/notifications/${id}/read`); load(); };

  if (!data) return <LoadingState />;

  return (
    <>
      <PageHeader
        subtitle="Alerts"
        title="Notification centre"
        actions={<button className="btn-outline" onClick={markAll}>Mark all as read</button>}
      />
      <Section title={`${data.totalElements} notification${data.totalElements === 1 ? '' : 's'}`}>
        {data.content.length === 0
          ? <p className="text-sm text-ink-500 dark:text-ink-300">You're all caught up. 🎉</p>
          : data.content.map((n) => (
            <div key={n.id} className={`py-3 border-b last:border-0 border-ink-100 dark:border-ink-800 flex items-start justify-between gap-4 ${n.read ? 'opacity-70' : ''}`}>
              <div>
                <div className="text-[11px] uppercase tracking-widest text-ink-500 dark:text-ink-300">{n.category}</div>
                <div className="font-semibold text-sm">{n.title}</div>
                {n.body && <p className="text-sm text-ink-600 dark:text-ink-300 mt-1">{n.body}</p>}
              </div>
              {!n.read && <button className="btn-outline text-xs" onClick={() => mark(n.id)}>Mark read</button>}
            </div>
          ))}
      </Section>
    </>
  );
}
