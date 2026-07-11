import { useEffect, useState } from 'react';
import { Download } from '@mui/icons-material';
import toast from 'react-hot-toast';

import { api } from '../../app/apiClient';
import { PageHeader, Section, LoadingState } from '../../components/ui/Primitives';

export default function DocumentsPage() {
  const [documents, setDocuments] = useState(null);
  const [form, setForm] = useState({ title: '', documentType: 'RESEARCH_PAPER', description: '', confidential: false });
  const [file, setFile] = useState(null);

  const load = () => api.get('/documents').then((r) => setDocuments(r.data));
  useEffect(() => { load(); }, []);

  const create = async (e) => {
    e.preventDefault();
    try {
      const doc = await api.post('/documents', form);
      if (file) {
        const fd = new FormData();
        fd.append('file', file);
        fd.append('changelog', 'Initial upload');
        await api.post(`/documents/${doc.data.id}/versions`, fd, { headers: { 'Content-Type': 'multipart/form-data' } });
      }
      toast.success('Document created');
      setForm({ title: '', documentType: 'RESEARCH_PAPER', description: '', confidential: false });
      setFile(null);
      load();
    } catch {}
  };

  const download = async (docId) => {
    const versions = await api.get(`/documents/${docId}/versions`);
    if (!versions.data.length) return toast.error('No versions available');
    const latest = versions.data[0];
    const res = await api.get(`/documents/versions/${latest.id}/download`, { responseType: 'blob' });
    const url = URL.createObjectURL(res.data);
    const a = document.createElement('a');
    a.href = url; a.download = latest.fileName; a.click();
    URL.revokeObjectURL(url);
  };

  return (
    <>
      <PageHeader subtitle="Library" title="Document management" />
      <Section title="Upload a document" className="mb-6">
        <form onSubmit={create} className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <input className="field-input" placeholder="Title" value={form.title} onChange={(e) => setForm({ ...form, title: e.target.value })} required />
          <select className="field-input" value={form.documentType} onChange={(e) => setForm({ ...form, documentType: e.target.value })}>
            {['RESEARCH_PAPER', 'TECHNICAL_REPORT', 'DESIGN_DOCUMENT', 'TEST_REPORT', 'PRESENTATION', 'SOURCE_CODE', 'PATENT', 'DRAWING', 'USER_MANUAL', 'OTHER'].map((v) => <option key={v}>{v}</option>)}
          </select>
          <input type="file" className="field-input" onChange={(e) => setFile(e.target.files?.[0] || null)} />
          <textarea className="field-input md:col-span-3" rows={2} placeholder="Description" value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} />
          <label className="flex items-center gap-2 text-sm">
            <input type="checkbox" checked={form.confidential} onChange={(e) => setForm({ ...form, confidential: e.target.checked })} />
            Confidential
          </label>
          <div className="md:col-span-2 flex justify-end">
            <button className="btn-primary">Create document</button>
          </div>
        </form>
      </Section>

      {!documents ? <LoadingState /> : (
        <Section title="Library">
          <table className="min-w-full">
            <thead><tr>
              <th className="table-th">Title</th>
              <th className="table-th">Type</th>
              <th className="table-th">Status</th>
              <th className="table-th">Confidential</th>
              <th className="table-th">Actions</th>
            </tr></thead>
            <tbody>
              {documents.map((d) => (
                <tr key={d.id}>
                  <td className="table-td">{d.title}</td>
                  <td className="table-td text-xs">{d.documentType}</td>
                  <td className="table-td text-xs">{d.status}</td>
                  <td className="table-td">{d.confidential ? '🔒' : '—'}</td>
                  <td className="table-td">
                    {d.currentVersionId && (
                      <button className="btn-outline text-xs" onClick={() => download(d.id)}>
                        <Download fontSize="small" />Download
                      </button>
                    )}
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
