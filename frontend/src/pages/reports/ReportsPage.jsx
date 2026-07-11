import { PictureAsPdf, TableView } from '@mui/icons-material';
import { api } from '../../app/apiClient';
import { PageHeader, Section } from '../../components/ui/Primitives';

async function download(url, filename) {
  const res = await api.get(url, { responseType: 'blob' });
  const blobUrl = URL.createObjectURL(res.data);
  const a = document.createElement('a');
  a.href = blobUrl; a.download = filename; a.click();
  URL.revokeObjectURL(blobUrl);
}

export default function ReportsPage() {
  return (
    <>
      <PageHeader subtitle="Exports" title="Reporting" />
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <Section title="Projects · PDF" subtitle="Formal export">
          <p className="text-sm text-ink-500 dark:text-ink-300 mb-4">
            Portfolio snapshot in a printable PDF: project code, title, priority, risk, status and budget.
          </p>
          <button className="btn-primary" onClick={() => download('/reports/projects.pdf', 'erpms-projects.pdf')}>
            <PictureAsPdf fontSize="small" />Download PDF
          </button>
        </Section>
        <Section title="Projects · Excel" subtitle="Analyst friendly">
          <p className="text-sm text-ink-500 dark:text-ink-300 mb-4">
            The same data as an .xlsx workbook — ready to slice in Excel or Google Sheets.
          </p>
          <button className="btn-primary" onClick={() => download('/reports/projects.xlsx', 'erpms-projects.xlsx')}>
            <TableView fontSize="small" />Download Excel
          </button>
        </Section>
      </div>
    </>
  );
}
