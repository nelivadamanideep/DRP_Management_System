import { useEffect, useState } from 'react';
import toast from 'react-hot-toast';

import { api } from '../../app/apiClient';
import { PageHeader, Section, LoadingState, StatusChip } from '../../components/ui/Primitives';

const STATUS_PALETTE = {
  SUBMITTED: 'bg-accent/25 text-accent-deep dark:text-accent-soft',
  APPROVED: 'bg-moss-soft text-moss-deep',
  REJECTED: 'bg-red-100 text-red-700',
  ORDERED: 'bg-ink-900 text-white dark:bg-accent dark:text-ink-900',
  CLOSED: 'bg-ink-200 text-ink-700',
};

export default function ProcurementPage() {
  const [requests, setRequests] = useState(null);
  const [orders, setOrders] = useState([]);
  const [tab, setTab] = useState('requests');
  const [form, setForm] = useState({ requestNumber: '', title: '', justification: '', estimatedCost: 0 });

  const load = () => {
    api.get('/procurement/requests').then((r) => setRequests(r.data));
    api.get('/procurement/orders').then((r) => setOrders(r.data));
  };
  useEffect(load, []);

  const createRequest = async (e) => {
    e.preventDefault();
    try { await api.post('/procurement/requests', { ...form, estimatedCost: Number(form.estimatedCost || 0) }); toast.success('Submitted'); load(); }
    catch {}
  };
  const approve = async (id) => { try { await api.post(`/procurement/requests/${id}/approve`, { comments: 'Approved via console' }); toast.success('Approved'); load(); } catch {} };
  const reject = async (id) => { try { await api.post(`/procurement/requests/${id}/reject`, { comments: 'Rejected via console' }); toast('Rejected'); load(); } catch {} };

  return (
    <>
      <PageHeader subtitle="Sourcing" title="Procurement" />
      <div className="flex gap-2 mb-6">
        <button onClick={() => setTab('requests')} className={tab==='requests' ? 'btn-primary':'btn-outline'}>Requests</button>
        <button onClick={() => setTab('orders')} className={tab==='orders' ? 'btn-primary':'btn-outline'}>Purchase orders</button>
      </div>

      {tab === 'requests' && (
        <>
          <Section title="Raise a purchase request" className="mb-6">
            <form onSubmit={createRequest} className="grid grid-cols-1 md:grid-cols-4 gap-3">
              <input className="field-input" placeholder="Request no." value={form.requestNumber} onChange={(e) => setForm({ ...form, requestNumber: e.target.value })} required />
              <input className="field-input md:col-span-2" placeholder="Title" value={form.title} onChange={(e) => setForm({ ...form, title: e.target.value })} required />
              <input type="number" className="field-input" placeholder="Est. cost" value={form.estimatedCost} onChange={(e) => setForm({ ...form, estimatedCost: e.target.value })} />
              <textarea rows={2} className="field-input md:col-span-3" placeholder="Justification" value={form.justification} onChange={(e) => setForm({ ...form, justification: e.target.value })} />
              <button className="btn-primary">Submit</button>
            </form>
          </Section>
          {!requests ? <LoadingState /> : (
            <Section title="Requests">
              <table className="min-w-full"><thead><tr>
                <th className="table-th">No.</th><th className="table-th">Title</th><th className="table-th">Est. cost</th>
                <th className="table-th">Status</th><th className="table-th">Actions</th>
              </tr></thead><tbody>
                {requests.map((r) => (
                  <tr key={r.id}>
                    <td className="table-td font-mono">{r.requestNumber}</td>
                    <td className="table-td">{r.title}</td>
                    <td className="table-td">₹ {Number(r.estimatedCost).toLocaleString('en-IN')}</td>
                    <td className="table-td"><StatusChip value={r.status} palette={STATUS_PALETTE} /></td>
                    <td className="table-td space-x-2">
                      {r.status === 'SUBMITTED' && (<>
                        <button className="btn-outline text-xs" onClick={() => approve(r.id)}>Approve</button>
                        <button className="btn-outline text-xs" onClick={() => reject(r.id)}>Reject</button>
                      </>)}
                    </td>
                  </tr>
                ))}
              </tbody></table>
            </Section>
          )}
        </>
      )}

      {tab === 'orders' && (
        <Section title="Purchase orders">
          {orders.length === 0 ? <p className="text-sm text-ink-500 dark:text-ink-300">No POs raised yet.</p> : (
            <table className="min-w-full"><thead><tr>
              <th className="table-th">PO no.</th><th className="table-th">Supplier</th><th className="table-th">Total</th>
              <th className="table-th">Status</th><th className="table-th">Issued on</th>
            </tr></thead><tbody>
              {orders.map((o) => (
                <tr key={o.id}>
                  <td className="table-td font-mono">{o.poNumber}</td>
                  <td className="table-td font-mono text-xs">{o.supplierId.slice(0, 10)}…</td>
                  <td className="table-td">₹ {Number(o.totalAmount).toLocaleString('en-IN')}</td>
                  <td className="table-td"><StatusChip value={o.status} palette={STATUS_PALETTE} /></td>
                  <td className="table-td">{o.issuedOn}</td>
                </tr>
              ))}
            </tbody></table>
          )}
        </Section>
      )}
    </>
  );
}
