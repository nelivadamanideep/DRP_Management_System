import { useEffect, useState } from 'react';
import { DeleteOutline } from '@mui/icons-material';
import toast from 'react-hot-toast';

import { api } from '../../app/apiClient';
import { PageHeader, Section, LoadingState, StatusChip } from '../../components/ui/Primitives';

const STATUS_PALETTE = {
  AVAILABLE: 'bg-moss-soft text-moss-deep',
  IN_USE: 'bg-accent/25 text-accent-deep dark:text-accent-soft',
  UNDER_MAINTENANCE: 'bg-red-100 text-red-700',
  RETIRED: 'bg-ink-200 text-ink-700 dark:bg-ink-700',
};

export default function EquipmentPage() {
  const [rows, setRows] = useState(null);
  const [form, setForm] = useState({ assetTag: '', name: '', manufacturer: '', modelNumber: '', laboratoryLocation: '', status: 'AVAILABLE' });

  const load = () => api.get('/equipment').then((r) => setRows(r.data));
  useEffect(() => { load(); }, []);

  const create = async (e) => {
    e.preventDefault();
    try { await api.post('/equipment', form); toast.success('Registered'); load(); }
    catch {}
  };

  const deleteEquipment = async (equipId) => {
    try {
      await api.delete(`/equipment/${equipId}`);
      setRows(rows.filter(e => e.id !== equipId));
      toast.success('Equipment deleted successfully');
    } catch (error) {
      toast.error('Failed to delete equipment');
    }
  };

  return (
    <>
      <PageHeader subtitle="Assets" title="Equipment registry" />
      <Section title="Register equipment" className="mb-6">
        <form onSubmit={create} className="grid grid-cols-1 md:grid-cols-3 gap-3">
          <input className="field-input" placeholder="Asset tag" value={form.assetTag} onChange={(e) => setForm({ ...form, assetTag: e.target.value })} required />
          <input className="field-input md:col-span-2" placeholder="Name" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} required />
          <input className="field-input" placeholder="Manufacturer" value={form.manufacturer} onChange={(e) => setForm({ ...form, manufacturer: e.target.value })} />
          <input className="field-input" placeholder="Model number" value={form.modelNumber} onChange={(e) => setForm({ ...form, modelNumber: e.target.value })} />
          <input className="field-input" placeholder="Location" value={form.laboratoryLocation} onChange={(e) => setForm({ ...form, laboratoryLocation: e.target.value })} />
          <select className="field-input" value={form.status} onChange={(e) => setForm({ ...form, status: e.target.value })}>
            {Object.keys(STATUS_PALETTE).map((v) => <option key={v}>{v}</option>)}
          </select>
          <button className="btn-primary md:col-span-2">Register</button>
        </form>
      </Section>

      {!rows ? <LoadingState /> : (
        <Section title="Equipment inventory">
          <table className="min-w-full">
            <thead><tr>
              <th className="table-th">Tag</th>
              <th className="table-th">Name</th>
              <th className="table-th">Manufacturer</th>
              <th className="table-th">Location</th>
              <th className="table-th">Next calibration</th>
              <th className="table-th">Status</th>
              <th className="table-th">Actions</th>
            </tr></thead>
            <tbody>
              {rows.map((e) => (
                <tr key={e.id}>
                  <td className="table-td font-mono">{e.assetTag}</td>
                  <td className="table-td">{e.name}</td>
                  <td className="table-td">{e.manufacturer || '—'}</td>
                  <td className="table-td">{e.laboratoryLocation || '—'}</td>
                  <td className="table-td">{e.nextCalibrationDate || '—'}</td>
                  <td className="table-td"><StatusChip value={e.status} palette={STATUS_PALETTE} /></td>
                  <td className="table-td"><button onClick={() => deleteEquipment(e.id)} className="btn-outline text-xs text-red-600 hover:bg-red-50 dark:hover:bg-red-900/20"><DeleteOutline fontSize="small" /></button></td>
                </tr>
              ))}
            </tbody>
          </table>
        </Section>
      )}
    </>
  );
}
