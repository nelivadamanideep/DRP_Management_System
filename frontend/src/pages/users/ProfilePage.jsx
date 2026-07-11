import { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import toast from 'react-hot-toast';

import { api } from '../../app/apiClient';
import { PageHeader, Section, LoadingState } from '../../components/ui/Primitives';

const empty = { designation: '', departmentId: '', experienceYears: 0, skills: '', certifications: '', phone: '' };

export default function ProfilePage() {
  const user = useSelector((s) => s.auth.user);
  const [profile, setProfile] = useState(null);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if (!user?.userId) return;
    api.get(`/users/${user.userId}/profile`).then((r) => setProfile(r.data)).catch(() => setProfile(empty));
  }, [user]);

  const save = async (e) => {
    e.preventDefault();
    setSaving(true);
    try {
      await api.put(`/users/${user.userId}/profile`, profile);
      toast.success('Profile updated');
    } catch {}
    finally { setSaving(false); }
  };

  if (!profile) return <LoadingState />;

  return (
    <>
      <PageHeader subtitle="Account" title="My profile" />
      <Section title="Public bio">
        <form onSubmit={save} className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div><label className="field-label">Full name</label><input className="field-input" value={user?.fullName || ''} disabled /></div>
          <div><label className="field-label">Email</label><input className="field-input" value={user?.email || ''} disabled /></div>
          <div><label className="field-label">Designation</label>
            <input className="field-input" value={profile.designation || ''} onChange={(e) => setProfile({ ...profile, designation: e.target.value })} />
          </div>
          <div><label className="field-label">Phone</label>
            <input className="field-input" value={profile.phone || ''} onChange={(e) => setProfile({ ...profile, phone: e.target.value })} />
          </div>
          <div><label className="field-label">Experience (years)</label>
            <input type="number" min={0} className="field-input" value={profile.experienceYears || 0} onChange={(e) => setProfile({ ...profile, experienceYears: Number(e.target.value) })} />
          </div>
          <div><label className="field-label">Department ID</label>
            <input className="field-input" value={profile.departmentId || ''} onChange={(e) => setProfile({ ...profile, departmentId: e.target.value })} />
          </div>
          <div className="md:col-span-2"><label className="field-label">Skills</label>
            <textarea rows={3} className="field-input" value={profile.skills || ''} onChange={(e) => setProfile({ ...profile, skills: e.target.value })} />
          </div>
          <div className="md:col-span-2"><label className="field-label">Certifications</label>
            <textarea rows={3} className="field-input" value={profile.certifications || ''} onChange={(e) => setProfile({ ...profile, certifications: e.target.value })} />
          </div>
          <div className="md:col-span-2 flex justify-end">
            <button className="btn-primary" disabled={saving}>{saving ? 'Saving…' : 'Save profile'}</button>
          </div>
        </form>
      </Section>
    </>
  );
}
