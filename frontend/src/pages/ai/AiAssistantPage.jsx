import { useRef, useState } from 'react';
import { AutoAwesomeOutlined, SendOutlined } from '@mui/icons-material';
import { api } from '../../app/apiClient';
import { PageHeader, Section } from '../../components/ui/Primitives';

export default function AiAssistantPage() {
  const [messages, setMessages] = useState([
    { role: 'assistant', content: 'Hi! I\'m the ERPMS AI assistant. Ask me about your projects, budgets, equipment or anything you see in the platform.' },
  ]);
  const [input, setInput] = useState('');
  const [busy, setBusy] = useState(false);
  const [semanticQuery, setSemanticQuery] = useState('');
  const [semanticResult, setSemanticResult] = useState(null);
  const [transcript, setTranscript] = useState('');
  const [summary, setSummary] = useState(null);
  const bottomRef = useRef(null);

  const send = async (e) => {
    e.preventDefault();
    if (!input.trim() || busy) return;
    const next = [...messages, { role: 'user', content: input.trim() }];
    setMessages(next);
    setInput('');
    setBusy(true);
    try {
      const res = await api.post('/ai/chat', { messages: next });
      setMessages([...next, { role: 'assistant', content: res.data.reply || '' }]);
      setTimeout(() => bottomRef.current?.scrollIntoView({ behavior: 'smooth' }), 40);
    } catch { /* handled */ }
    finally { setBusy(false); }
  };

  const runSemanticSearch = async () => {
    if (!semanticQuery.trim()) return;
    const res = await api.get('/ai/semantic-search', { params: { q: semanticQuery } });
    setSemanticResult(res.data);
  };
  const summarize = async () => {
    if (!transcript.trim()) return;
    const res = await api.post('/ai/meeting-summary', { transcript });
    setSummary(res.data);
  };

  return (
    <>
      <PageHeader subtitle="Intelligence" title="AI assistant" />
      <div className="grid grid-cols-1 xl:grid-cols-3 gap-6">
        <Section title="Chat" subtitle="Multi-turn" className="xl:col-span-2">
          <div className="min-h-[420px] max-h-[520px] overflow-y-auto space-y-3 mb-4">
            {messages.map((m, i) => (
              <div key={i} className={`flex ${m.role === 'user' ? 'justify-end' : 'justify-start'}`}>
                <div className={`max-w-[80%] rounded-2xl px-4 py-2.5 text-sm whitespace-pre-wrap ${
                  m.role === 'user'
                    ? 'bg-ink-900 text-white dark:bg-accent dark:text-ink-900'
                    : 'bg-ink-100 dark:bg-ink-800 text-ink-800 dark:text-ink-100'
                }`}>
                  {m.content}
                </div>
              </div>
            ))}
            <div ref={bottomRef} />
          </div>
          <form onSubmit={send} className="flex gap-2">
            <input className="field-input flex-1" placeholder="Ask about a project, budget, risk…" value={input} onChange={(e) => setInput(e.target.value)} disabled={busy} />
            <button className="btn-primary" disabled={busy || !input.trim()} data-testid="ai-send">
              <SendOutlined fontSize="small" />{busy ? 'Thinking…' : 'Send'}
            </button>
          </form>
        </Section>

        <div className="space-y-6">
          <Section title="Semantic search" subtitle="Documents">
            <input className="field-input mb-2" placeholder="e.g. propulsion test results" value={semanticQuery} onChange={(e) => setSemanticQuery(e.target.value)} />
            <button className="btn-outline w-full" onClick={runSemanticSearch}><AutoAwesomeOutlined fontSize="small" />Search</button>
            {semanticResult && (
              <pre className="whitespace-pre-wrap text-xs text-ink-700 dark:text-ink-200 mt-3">{semanticResult.content}</pre>
            )}
          </Section>

          <Section title="Meeting summariser" subtitle="Actionable">
            <textarea rows={5} className="field-input mb-2" placeholder="Paste transcript…" value={transcript} onChange={(e) => setTranscript(e.target.value)} />
            <button className="btn-outline w-full" onClick={summarize}><AutoAwesomeOutlined fontSize="small" />Summarise</button>
            {summary && (
              <pre className="whitespace-pre-wrap text-xs text-ink-700 dark:text-ink-200 mt-3">{summary.content}</pre>
            )}
          </Section>
        </div>
      </div>
    </>
  );
}
