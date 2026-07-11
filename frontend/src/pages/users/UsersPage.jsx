import { useEffect, useState } from 'react';
import toast from 'react-hot-toast';

import { api } from '../../app/apiClient';
import { PageHeader, Section, LoadingState, StatusChip } from '../../components/ui/Primitives';

const ROLES = ['ADMINISTRATOR', 'PROJECT_DIRECTOR', 'DEPARTMENT_HEAD', 'SCIENTIST', 'RESEARCH_ENGINEER',
  'LABORATORY_MANAGER', 'FINANCE_OFFICER', 'PROCUREMENT_OFFICER', 'DOCUMENT_CONTROLLER', 'AUDITOR', 'GUEST'];

const STATUS_PALETTE = {
  ACTIVE: 'bg-moss-soft text-moss-deep',
  SUSPENDED: 'bg-red-100 text-red-700',
};

export default function UsersPage() {
  const [users, setUsers] = useState(null);
  const load = () => api.get('/users').then((r) => setUsers(r.data));
  useEffect(() => { load(); }, []);

  const updateRole = async (u, role) => {
    try { await api.put(`/users/${u.id}/role`, { role }); toast.success('Role updated'); load(); }
    catch {}
  };
  const updateStatus = async (u, status) => {
    try { await api.put(`/users/${u.id}/status`, { status }); toast.success('Status updated'); load(); }
    catch {}
  };

  return (
    <>
      <PageHeader subtitle="Access" title="User management" />
      {!users ? <LoadingState /> : (
        <Section title="Registered users">
          <table className="min-w-full">
            <thead><tr>
              <th className="table-th">Name</th>
              <th className="table-th">Email</th>
              <th className="table-th">Role</th>
              <th className="table-th">Status</th>
            </tr></thead>
            <tbody>
              {users.map((u) => (
                <tr key={u.id}>
                  <td className="table-td">{u.fullName}</td>
                  <td className="table-td text-ink-500 dark:text-ink-300">{u.email}</td>
                  <td className="table-td">
                    <select className="field-input py-1.5" value={u.role} onChange={(e) => updateRole(u, e.target.value)}>
                      {ROLES.map((r) => <option key={r}>{r}</option>)}
                    </select>
                  </td>
                  <td className="table-td">
                    <StatusChip value={u.status} palette={STATUS_PALETTE} />
                    <button className="ml-2 text-xs underline text-ink-500 dark:text-ink-300" onClick={() => updateStatus(u, u.status === 'ACTIVE' ? 'SUSPENDED' : 'ACTIVE')}>
                      {u.status === 'ACTIVE' ? 'Suspend' : 'Reactivate'}
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </Section>
      )}
    </>
  );
}
