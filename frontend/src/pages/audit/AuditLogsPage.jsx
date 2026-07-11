import { useEffect, useState } from 'react';
import { api } from '../../app/apiClient';
import { PageHeader, Section, LoadingState } from '../../components/ui/Primitives';

export default function AuditLogsPage() {
  const [data, setData] = useState(null);
  const [page, setPage] = useState(0);

  useEffect(() => {
    api.get('/audit-logs', { params: { page, size: 50 } }).then((r) => setData(r.data)).catch(() => setData({ content: [], totalElements: 0 }));
  }, [page]);

  if (!data) return <LoadingState />;

  return (
    <>
      <PageHeader subtitle="Compliance" title="Audit trail" />
      <Section title={`${data.totalElements || 0} events captured`}>
        <table className="min-w-full">
          <thead><tr>
            <th className="table-th">When</th>
            <th className="table-th">User</th>
            <th className="table-th">Action</th>
            <th className="table-th">HTTP</th>
            <th className="table-th">Status</th>
          </tr></thead>
          <tbody>
            {data.content.map((row) => (
              <tr key={row.id}>
                <td className="table-td text-xs">{new Date(row.occurredAt).toLocaleString()}</td>
                <td className="table-td">{row.userEmail || '—'}</td>
                <td className="table-td font-mono text-xs">{row.action}</td>
                <td className="table-td text-xs">{row.httpMethod} {row.requestUri}</td>
                <td className="table-td">{row.statusCode}</td>
              </tr>
            ))}
          </tbody>
        </table>
        <div className="flex justify-end gap-2 mt-4">
          <button disabled={data.first} className="btn-outline text-xs" onClick={() => setPage(page - 1)}>Prev</button>
          <button disabled={data.last} className="btn-outline text-xs" onClick={() => setPage(page + 1)}>Next</button>
        </div>
      </Section>
    </>
  );
}
