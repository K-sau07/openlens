import { useEffect, useState } from 'react'
import { useLocation, useNavigate } from 'react-router-dom'
import { api } from '../services/api'

const c = {
  bg: '#0C0604', text: '#ECD9B8', muted: 'rgba(236,217,184,0.5)',
  dim: 'rgba(236,217,184,0.25)', accent: '#AA5535', accent2: '#C4714D',
  border: 'rgba(170,85,53,0.2)', card: '#130907', green: '#7CB67A',
  greenBg: 'rgba(124,182,122,0.08)', greenBorder: 'rgba(124,182,122,0.2)',
  blue: '#7AA8D8', blueBg: 'rgba(122,168,216,0.08)', blueBorder: 'rgba(122,168,216,0.2)',
}

const STYLES = `
  @import url('https://fonts.googleapis.com/css2?family=Playfair+Display:ital,wght@0,400;1,400&family=JetBrains+Mono:wght@400&family=DM+Sans:wght@300;400;500&display=swap');
  .step-header{cursor:pointer;transition:background .15s}
  .step-header:hover{background:rgba(170,85,53,.04)!important}
  .check-item{cursor:pointer;transition:opacity .15s}
  .check-item:hover{opacity:.85}
  .code{font-family:'JetBrains Mono',monospace;font-size:12px;background:#0A0402;border:0.5px solid rgba(170,85,53,.15);border-radius:4px;padding:12px 16px;line-height:1.8;color:rgba(236,217,184,.75);overflow-x:auto;white-space:pre}
  .sidebar-item{cursor:pointer;transition:background .15s,border-color .15s}
  .sidebar-item:hover{background:rgba(170,85,53,.06)!important}
  .progress-fill{transition:width .4s ease}
`

// fallback guide used until backend returns real one
const FALLBACK_GUIDE = {
  repo: { name: 'opencodeintel/opencodeintel', description: 'Codebase intelligence platform — semantic search and context generation.', language: 'Python', stars: 0, openIssues: 50, mergedPrs: 23, avgResponseHours: 48, ciPassing: true, hasTests: true },
  issue: { number: 17, title: 'Add support for multi-file context', labels: ['good first issue', 'help wanted'], description: 'The context builder currently handles one file at a time. Add support for passing multiple files so the LLM gets broader context.' },
  matchReason: 'Comfortable reading Python, have opened PRs before. This issue touches 3 files with clear scope. Similar PR #23 merged in under a week.',
  estimatedHours: '2–4 hours',
  steps: [
    {
      title: 'Understand the repo',
      subtitle: 'Read the codebase structure before touching anything',
      body: 'Before writing any code, read through the key files. The context builder is the core of what you\'re changing.',
      files: ['src/context/builder.py', 'src/api/routes/context.py', 'tests/test_context_builder.py'],
      checklist: ['Read src/context/builder.py top to bottom', 'Understand the build_context() function signature', 'Read tests/test_context_builder.py to understand existing test patterns'],
    },
    {
      title: 'Set up locally',
      subtitle: 'Fork, clone, and get the dev environment running',
      body: 'Fork the repo on GitHub, then clone your fork. This repo uses Docker for local dev.',
      code: 'git clone https://github.com/YOUR_USERNAME/opencodeintel\ncd opencodeintel\ncp .env.example .env\ndocker compose up --build',
      tip: 'The .env.example has all keys needed for local dev. There\'s a mock mode — you don\'t need a real LLM API key to run tests.',
      checklist: ['Forked the repo on GitHub', 'Cloned my fork locally', 'Copied .env.example to .env', 'Docker containers running — app responds at localhost:8000', 'Existing tests pass: pytest returns green'],
    },
    {
      title: 'Find your files',
      subtitle: 'Locate exactly what to change and understand the current code',
      body: 'Open src/context/builder.py. The build_context() function currently accepts a single file_path: str. Your change makes it accept file_paths: list[str].',
      code: '# current — what you\'ll find\ndef build_context(file_path: str) -> ContextResult:\n\n# what it should look like after your change\ndef build_context(file_paths: list[str]) -> ContextResult:',
      warn: 'The API route in context.py passes file_path as a query param. You\'ll need to update that schema too. Check PR #23 — it touched the same route.',
      checklist: ['Found build_context() in builder.py', 'Understood the current function signature', 'Checked PR #23 to understand the route pattern'],
    },
    {
      title: 'Make the change',
      subtitle: 'Write the actual code — small and focused',
      body: 'Keep your change minimal. Update the function signature, iterate over file_paths, and concatenate the context. Don\'t refactor anything else.',
      tip: '"Please don\'t refactor surrounding code in the same PR. One thing at a time makes review easier." — maintainer comment on issue #17',
      checklist: ['Updated build_context() signature to accept list[str]', 'Updated the API route schema in routes/context.py', 'Manually tested the change works locally'],
    },
    {
      title: 'Write your tests',
      subtitle: 'Every merged PR in this repo includes tests — so does yours',
      body: 'Add tests to tests/test_context_builder.py. You need at minimum: one test for single file (existing behavior), one for multiple files (new behavior), one for empty list (edge case).',
      code: 'def test_build_context_multiple_files():\n    result = build_context(["file_a.py", "file_b.py"])\n    assert result.files_processed == 2\n    assert len(result.context) > 0',
      checklist: ['Added test for single file (existing behavior)', 'Added test for multiple files (new behavior)', 'Added test for empty list edge case', 'All tests pass: pytest returns green'],
    },
    {
      title: 'Commit and push',
      subtitle: 'Write a commit message this maintainer expects',
      body: 'Based on merged PR history, this maintainer prefers short imperative commit messages. No prefixes, no emojis. Reference the issue number.',
      code: 'git checkout -b feature/multi-file-context\ngit add src/context/builder.py src/api/routes/context.py tests/\ngit commit -m "add multi-file context support to builder (#17)"\ngit push origin feature/multi-file-context',
      checklist: ['Created a new branch', 'Committed with the right message format', 'Pushed to my fork'],
    },
    {
      title: 'Open the PR',
      subtitle: 'Title, description, and what this maintainer wants to see',
      body: 'The PR title must reference the issue number. Description should be short — what you changed, why, and how to test. Don\'t over-explain.',
      code: 'Title:\nadd multi-file context support to builder (#17)\n\nDescription:\nUpdated build_context() to accept list[str] instead of str.\nUpdated the API route schema to match.\nAdded 3 tests covering single file, multiple files, and empty list.\n\nCloses #17',
      checklist: ['Opened PR with correct title format', 'Description covers what changed and how to test', 'Issue reference is in the description (Closes #17)'],
    },
    {
      title: 'Handle review feedback',
      subtitle: 'What to do if the maintainer asks for changes',
      body: 'This maintainer responds within 48 hours and gives clear, specific feedback. If they ask for changes, push new commits to the same branch — don\'t open a new PR. Respond to every comment.',
      tip: 'In PR #23 and #31, the maintainer left 1–2 small comments, the contributor addressed them in a single follow-up commit, and the PR was merged the next day.',
      checklist: ['PR submitted and waiting for review'],
    },
  ],
}

export default function GuidePage() {
  const { state } = useLocation()
  const navigate = useNavigate()
  const { repoUrl, repoId, issue } = state || {}

  const [guide, setGuide] = useState(null)
  const [loading, setLoading] = useState(true)
  const [activeStep, setActiveStep] = useState(0)
  const [checked, setChecked] = useState({})

  useEffect(() => {
    if (!repoUrl) { navigate('/'); return }
    const load = async () => {
      try {
        const res = await api.getGuide(repoId, issue?.id)
        setGuide(res)
      } catch {
        setGuide(FALLBACK_GUIDE)
      } finally {
        setLoading(false)
      }
    }
    load()
  }, [repoId, issue, repoUrl, navigate])

  const toggleCheck = (stepIdx, itemIdx) => {
    const key = `${stepIdx}-${itemIdx}`
    setChecked(prev => ({ ...prev, [key]: !prev[key] }))
  }

  const totalChecks = guide ? guide.steps.reduce((acc, s) => acc + (s.checklist?.length || 0), 0) : 0
  const doneChecks = Object.values(checked).filter(Boolean).length
  const pct = totalChecks ? Math.round((doneChecks / totalChecks) * 100) : 0
  const repoName = repoUrl?.replace('https://github.com/', '') || ''

  if (loading) {
    return (
      <div style={{ background: c.bg, color: c.text, minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
        <div style={{ textAlign: 'center' }}>
          <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '11px', color: c.accent, marginBottom: '12px' }}>Generating your guide...</div>
          <div style={{ fontSize: '13px', color: c.muted, fontWeight: '300' }}>Analyzing issue complexity, files, and maintainer patterns</div>
        </div>
      </div>
    )
  }

  const g = guide || FALLBACK_GUIDE

  return (
    <div style={{ background: c.bg, color: c.text, minHeight: '100vh', fontFamily: "'DM Sans', sans-serif" }}>
      <style>{STYLES}</style>

      {/* NAV */}
      <nav style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '16px 32px', borderBottom: `0.5px solid ${c.border}`, position: 'sticky', top: 0, background: c.bg, zIndex: 10 }}>
        <div style={{ fontFamily: "'Playfair Display', serif", fontSize: '18px' }}>OpenLens</div>
        <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
          <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '10px', color: c.dim }}>{doneChecks}/{totalChecks} steps done</div>
          <div style={{ width: '80px', height: '2px', background: 'rgba(170,85,53,0.12)', borderRadius: '1px', overflow: 'hidden' }}>
            <div className="progress-fill" style={{ height: '100%', width: `${pct}%`, background: pct === 100 ? c.green : c.accent, borderRadius: '1px' }} />
          </div>
          <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '11px', color: c.accent2, background: 'rgba(170,85,53,0.1)', border: `0.5px solid ${c.border}`, padding: '4px 10px', borderRadius: '3px' }}>{repoName}</div>
        </div>
      </nav>

      <div style={{ display: 'grid', gridTemplateColumns: '240px 1fr', minHeight: 'calc(100vh - 57px)' }}>

        {/* SIDEBAR */}
        <div style={{ borderRight: `0.5px solid ${c.border}`, padding: '24px 0', background: '#0E0705', position: 'sticky', top: '57px', height: 'calc(100vh - 57px)', overflowY: 'auto' }}>
          <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '9px', letterSpacing: '2px', color: c.dim, textTransform: 'uppercase', padding: '0 20px', marginBottom: '12px' }}>Your guide</div>
          {g.steps.map((step, i) => {
            const stepChecks = step.checklist?.length || 0
            const stepDone = step.checklist?.filter((_, j) => checked[`${i}-${j}`]).length || 0
            const allDone = stepChecks > 0 && stepDone === stepChecks
            return (
              <div key={i} className="sidebar-item" onClick={() => setActiveStep(i)}
                style={{ display: 'flex', alignItems: 'center', gap: '10px', padding: '9px 20px', borderLeft: `2px solid ${activeStep === i ? c.accent : 'transparent'}`, background: activeStep === i ? 'rgba(170,85,53,0.08)' : 'transparent' }}>
                <div style={{ width: '16px', height: '16px', borderRadius: '50%', border: `1px solid`, borderColor: allDone ? c.green : activeStep === i ? c.accent : 'rgba(170,85,53,0.25)', background: allDone ? c.greenBg : 'transparent', display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }}>
                  {allDone && <div style={{ width: '5px', height: '5px', borderRadius: '50%', background: c.green }} />}
                </div>
                <div style={{ fontSize: '12px', color: allDone ? c.dim : activeStep === i ? c.text : c.muted, lineHeight: '1.3', textDecoration: allDone ? 'line-through' : 'none', textDecorationColor: 'rgba(170,85,53,0.3)' }}>{step.title}</div>
              </div>
            )
          })}
          <div style={{ marginTop: '20px', padding: '16px 20px', borderTop: `0.5px solid ${c.border}` }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '6px' }}>
              <span style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '9px', color: c.dim }}>Progress</span>
              <span style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '9px', color: c.accent2 }}>{pct}%</span>
            </div>
            <div style={{ height: '2px', background: 'rgba(170,85,53,0.1)', borderRadius: '1px' }}>
              <div className="progress-fill" style={{ height: '100%', width: `${pct}%`, background: pct === 100 ? c.green : c.accent, borderRadius: '1px' }} />
            </div>
          </div>
        </div>

        {/* MAIN */}
        <div style={{ padding: '48px 64px 80px', overflowY: 'auto' }}>
          {/* repo intel header */}
          <div style={{ background: '#130907', border: `0.5px solid ${c.border}`, borderRadius: '8px', padding: '24px 28px', marginBottom: '36px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '18px' }}>
              <div>
                <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '13px', color: c.text, marginBottom: '4px' }}>{g.repo?.name}</div>
                <div style={{ fontSize: '13px', color: c.muted, fontWeight: '300', maxWidth: '480px', lineHeight: '1.5' }}>{g.repo?.description}</div>
              </div>
              <div style={{ display: 'flex', alignItems: 'center', gap: '6px', padding: '4px 10px', background: c.greenBg, border: `0.5px solid ${c.greenBorder}`, borderRadius: '3px', flexShrink: 0 }}>
                <div style={{ width: '6px', height: '6px', borderRadius: '50%', background: c.green }} />
                <span style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '10px', color: c.green }}>Active repo</span>
              </div>
            </div>
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(5, 1fr)', gap: '10px', marginBottom: '16px' }}>
              {[
                { val: g.repo?.openIssues, label: 'Open issues' },
                { val: g.repo?.mergedPrs, label: 'Merged PRs' },
                { val: `<${g.repo?.avgResponseHours}h`, label: 'PR response' },
                { val: g.repo?.language, label: 'Language' },
                { val: g.repo?.ciPassing ? 'Yes' : 'No', label: 'CI passing' },
              ].map((s, i) => (
                <div key={i} style={{ background: 'rgba(170,85,53,0.05)', border: `0.5px solid ${c.border}`, borderRadius: '4px', padding: '10px 12px' }}>
                  <div style={{ fontFamily: "'Playfair Display', serif", fontSize: '18px', color: c.accent2, marginBottom: '2px' }}>{s.val}</div>
                  <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '9px', color: c.dim }}>{s.label}</div>
                </div>
              ))}
            </div>
            <div style={{ display: 'flex', gap: '16px', flexWrap: 'wrap' }}>
              {[g.repo?.hasTests && 'Tests in all merged PRs', g.repo?.ciPassing && 'CI passing'].filter(Boolean).map(s => (
                <div key={s} style={{ display: 'flex', gap: '6px', alignItems: 'center' }}>
                  <div style={{ width: '5px', height: '5px', borderRadius: '50%', background: c.green }} />
                  <span style={{ fontSize: '12px', color: c.muted, fontWeight: '300' }}>{s}</span>
                </div>
              ))}
            </div>
          </div>

          {/* contribution overview */}
          <div style={{ background: '#130907', border: `0.5px solid rgba(170,85,53,0.3)`, borderRadius: '8px', padding: '24px 28px', marginBottom: '36px' }}>
            <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '9px', letterSpacing: '2px', color: c.accent, textTransform: 'uppercase', marginBottom: '12px' }}>Your contribution</div>
            <h2 style={{ fontFamily: "'Playfair Display', serif", fontSize: '22px', fontWeight: '400', lineHeight: '1.3', marginBottom: '12px' }}>
              <em style={{ fontStyle: 'italic', color: c.accent2 }}>#{g.issue?.number}</em> — {g.issue?.title}
            </h2>
            <div style={{ display: 'flex', gap: '8px', marginBottom: '14px', flexWrap: 'wrap' }}>
              {g.issue?.labels?.map(l => (
                <span key={l} style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '10px', padding: '3px 10px', borderRadius: '20px', background: 'rgba(170,85,53,0.08)', color: c.accent2, border: `0.5px solid ${c.border}` }}>{l}</span>
              ))}
            </div>
            <div style={{ display: 'flex', gap: '32px', flexWrap: 'wrap' }}>
              <div><span style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '9px', color: c.dim, display: 'block', marginBottom: '3px' }}>WHY IT FITS YOU</span><span style={{ fontSize: '13px', color: c.muted, fontWeight: '300' }}>{g.matchReason}</span></div>
              {g.estimatedHours && <div><span style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '9px', color: c.dim, display: 'block', marginBottom: '3px' }}>EST. TIME</span><span style={{ fontSize: '13px', color: c.muted, fontWeight: '300' }}>{g.estimatedHours}</span></div>}
            </div>
          </div>

          {/* step-by-step guide */}
          <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '9px', letterSpacing: '2px', color: c.accent, textTransform: 'uppercase', marginBottom: '16px' }}>Step-by-step guide</div>

          {g.steps.map((step, i) => {
            const isActive = activeStep === i
            const stepChecks = step.checklist?.length || 0
            const stepDone = step.checklist?.filter((_, j) => checked[`${i}-${j}`]).length || 0
            const allDone = stepChecks > 0 && stepDone === stepChecks
            return (
              <div key={i} style={{ background: '#130907', border: `0.5px solid`, borderColor: allDone ? c.greenBorder : isActive ? 'rgba(170,85,53,0.35)' : c.border, borderRadius: '6px', marginBottom: '8px', overflow: 'hidden', transition: 'border-color .2s' }}>
                <div className="step-header" onClick={() => setActiveStep(isActive ? -1 : i)}
                  style={{ display: 'flex', alignItems: 'center', gap: '14px', padding: '16px 20px' }}>
                  <div style={{ width: '26px', height: '26px', borderRadius: '4px', border: `0.5px solid`, borderColor: allDone ? c.greenBorder : c.border, background: allDone ? c.greenBg : 'rgba(170,85,53,0.05)', display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }}>
                    <span style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '10px', color: allDone ? c.green : c.accent }}>{String(i + 1).padStart(2, '0')}</span>
                  </div>
                  <div style={{ flex: 1 }}>
                    <div style={{ fontSize: '14px', fontWeight: '500', color: allDone ? 'rgba(236,217,184,0.45)' : c.text, textDecoration: allDone ? 'line-through' : 'none', textDecorationColor: 'rgba(170,85,53,0.3)' }}>{step.title}</div>
                    <div style={{ fontSize: '11.5px', color: c.dim, marginTop: '2px' }}>{step.subtitle}</div>
                  </div>
                  <span style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '9px', padding: '3px 8px', borderRadius: '3px', background: allDone ? c.greenBg : isActive ? 'rgba(170,85,53,0.1)' : 'rgba(236,217,184,0.04)', color: allDone ? c.green : isActive ? c.accent2 : c.dim, border: `0.5px solid`, borderColor: allDone ? c.greenBorder : isActive ? c.border : 'rgba(236,217,184,0.08)' }}>
                    {allDone ? 'done' : isActive ? 'in progress' : 'not started'}
                  </span>
                </div>

                {isActive && (
                  <div style={{ borderTop: `0.5px solid ${c.border}`, padding: '20px 20px 24px' }}>
                    <p style={{ fontSize: '13.5px', lineHeight: '1.75', color: c.muted, fontWeight: '300', marginBottom: step.files || step.code || step.tip || step.warn ? '18px' : '0' }}>{step.body}</p>

                    {step.files && (
                      <div style={{ marginBottom: '16px' }}>
                        {step.files.map(f => (
                          <div key={f} style={{ display: 'flex', alignItems: 'center', gap: '8px', padding: '6px 12px', background: 'rgba(170,85,53,0.05)', border: `0.5px solid ${c.border}`, borderRadius: '3px', marginBottom: '4px' }}>
                            <div style={{ width: '12px', height: '1px', background: c.accent, flexShrink: 0 }} />
                            <span style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '11.5px', color: 'rgba(236,217,184,0.7)' }}>{f}</span>
                          </div>
                        ))}
                      </div>
                    )}

                    {step.code && <pre className="code" style={{ marginBottom: '14px' }}>{step.code}</pre>}

                    {step.tip && (
                      <div style={{ background: c.blueBg, border: `0.5px solid ${c.blueBorder}`, borderRadius: '4px', padding: '12px 14px', marginBottom: '14px' }}>
                        <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '9px', letterSpacing: '1.5px', color: c.blue, textTransform: 'uppercase', marginBottom: '5px' }}>Note</div>
                        <div style={{ fontSize: '12.5px', color: 'rgba(236,217,184,0.55)', lineHeight: '1.65', fontWeight: '300' }}>{step.tip}</div>
                      </div>
                    )}

                    {step.warn && (
                      <div style={{ background: 'rgba(170,85,53,0.06)', border: `0.5px solid ${c.border}`, borderRadius: '4px', padding: '12px 14px', marginBottom: '14px' }}>
                        <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '9px', letterSpacing: '1.5px', color: c.accent2, textTransform: 'uppercase', marginBottom: '5px' }}>Watch out</div>
                        <div style={{ fontSize: '12.5px', color: c.muted, lineHeight: '1.65', fontWeight: '300' }}>{step.warn}</div>
                      </div>
                    )}

                    {step.checklist && (
                      <div style={{ display: 'flex', flexDirection: 'column', gap: '8px', marginTop: '16px' }}>
                        {step.checklist.map((item, j) => {
                          const key = `${i}-${j}`
                          const done = checked[key]
                          return (
                            <div key={j} className="check-item" onClick={() => toggleCheck(i, j)} style={{ display: 'flex', alignItems: 'flex-start', gap: '10px' }}>
                              <div style={{ width: '16px', height: '16px', border: `1px solid`, borderColor: done ? c.accent : 'rgba(170,85,53,0.3)', borderRadius: '3px', background: done ? c.accent : 'transparent', display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0, marginTop: '2px', transition: 'all .15s' }}>
                                {done && <svg width="9" height="7" viewBox="0 0 9 7" fill="none"><path d="M1 3.5L3.5 6L8 1" stroke="#ECD9B8" strokeWidth="1.5" strokeLinecap="round"/></svg>}
                              </div>
                              <span style={{ fontSize: '13px', color: done ? c.dim : c.muted, fontWeight: '300', lineHeight: '1.5', textDecoration: done ? 'line-through' : 'none', textDecorationColor: 'rgba(170,85,53,0.3)' }}>{item}</span>
                            </div>
                          )
                        })}
                      </div>
                    )}

                    {i < g.steps.length - 1 && (
                      <button onClick={() => setActiveStep(i + 1)}
                        style={{ marginTop: '20px', fontSize: '13px', fontWeight: '500', color: c.text, background: c.accent, border: 'none', padding: '10px 22px', borderRadius: '4px', cursor: 'pointer', fontFamily: "'DM Sans', sans-serif" }}>
                        Next step →
                      </button>
                    )}
                  </div>
                )}
              </div>
            )
          })}
        </div>
      </div>
    </div>
  )
}
