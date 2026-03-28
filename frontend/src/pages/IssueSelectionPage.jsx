import { useState } from 'react'
import { useLocation, useNavigate } from 'react-router-dom'

const c = {
  bg: '#0C0604', text: '#ECD9B8', muted: 'rgba(236,217,184,0.5)',
  dim: 'rgba(236,217,184,0.25)', accent: '#AA5535', accent2: '#C4714D',
  border: 'rgba(170,85,53,0.2)', card: '#130907', green: '#7CB67A',
}

const STYLES = `
  @import url('https://fonts.googleapis.com/css2?family=Playfair+Display:ital,wght@0,400;1,400&family=JetBrains+Mono:wght@400&family=DM+Sans:wght@300;400;500&display=swap');
  @keyframes fadeUp{from{opacity:0;transform:translateY(20px)}to{opacity:1;transform:translateY(0)}}
  .issue-card{transition:border-color .15s,transform .15s;cursor:pointer}
  .issue-card:hover{border-color:rgba(170,85,53,.5)!important;transform:translateY(-2px)}
  .issue-card.selected{border-color:#AA5535!important;background:rgba(170,85,53,.07)!important}
  .fade-in{animation:fadeUp .5s cubic-bezier(.22,1,.36,1) forwards}
`

// fallback issues used until backend returns real ones
const FALLBACK_ISSUES = [
  { id: 17, number: 17, title: 'Add support for multi-file context', labels: ['good first issue', 'help wanted'], complexity: 'beginner', estimatedHours: '2–4 hours', filesCount: 3 },
  { id: 23, number: 23, title: 'Improve error messages for invalid repo paths', labels: ['good first issue'], complexity: 'beginner', estimatedHours: '1–2 hours', filesCount: 2 },
  { id: 31, number: 31, title: 'Add language detection to context parser', labels: ['enhancement'], complexity: 'intermediate', estimatedHours: '3–5 hours', filesCount: 4 },
]

const LEVEL_COLOR = {
  beginner: { bg: 'rgba(124,182,122,0.1)', text: '#7CB67A', border: 'rgba(124,182,122,0.25)' },
  intermediate: { bg: 'rgba(170,85,53,0.1)', text: '#C4714D', border: 'rgba(170,85,53,0.25)' },
  advanced: { bg: 'rgba(122,168,216,0.1)', text: '#7AA8D8', border: 'rgba(122,168,216,0.25)' },
}

export default function IssueSelectionPage() {
  const { state } = useLocation()
  const navigate = useNavigate()
  const { repoUrl, repoId, skillLevel, matchedIssues } = state || {}
  const issues = matchedIssues?.length ? matchedIssues : FALLBACK_ISSUES
  const repoName = repoUrl?.replace('https://github.com/', '') || ''
  const [selected, setSelected] = useState(null)

  const handleGenerate = () => {
    if (selected === null) return
    navigate('/guide', { state: { repoUrl, repoId, issue: issues[selected] } })
  }

  return (
    <div style={{ background: c.bg, color: c.text, minHeight: '100vh', fontFamily: "'DM Sans', sans-serif" }}>
      <style>{STYLES}</style>

      <nav style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '20px 56px', borderBottom: `0.5px solid ${c.border}` }}>
        <div style={{ fontFamily: "'Playfair Display', serif", fontSize: '20px' }}>OpenLens</div>
        <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '11px', color: c.accent2, background: 'rgba(170,85,53,0.1)', border: `0.5px solid ${c.border}`, padding: '5px 12px', borderRadius: '3px' }}>{repoName}</div>
      </nav>

      <div style={{ maxWidth: '720px', margin: '0 auto', padding: '72px 40px' }}>
        <div className="fade-in" style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '10px', letterSpacing: '3px', color: c.accent, textTransform: 'uppercase', marginBottom: '16px' }}>
          Your matched issues
        </div>
        <h1 className="fade-in" style={{ fontFamily: "'Playfair Display', serif", fontSize: 'clamp(28px, 4vw, 42px)', fontWeight: '400', lineHeight: '1.2', marginBottom: '12px' }}>
          Pick your <em style={{ fontStyle: 'italic', color: c.accent2 }}>first PR</em>
        </h1>
        <p style={{ fontSize: '14px', color: c.muted, fontWeight: '300', marginBottom: '8px' }}>
          These issues are matched to your skill level. Pick one and we'll generate your full contribution guide.
        </p>
        {skillLevel && (
          <div style={{ display: 'inline-flex', alignItems: 'center', gap: '8px', marginBottom: '40px', padding: '5px 12px', background: LEVEL_COLOR[skillLevel]?.bg || 'rgba(170,85,53,0.1)', border: `0.5px solid ${LEVEL_COLOR[skillLevel]?.border || c.border}`, borderRadius: '3px' }}>
            <span style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '10px', color: LEVEL_COLOR[skillLevel]?.text || c.accent2, letterSpacing: '1px', textTransform: 'uppercase' }}>Your level: {skillLevel}</span>
          </div>
        )}

        <div style={{ display: 'flex', flexDirection: 'column', gap: '12px', marginBottom: '40px' }}>
          {issues.map((issue, i) => {
            const lvl = LEVEL_COLOR[issue.complexity] || LEVEL_COLOR.beginner
            return (
              <div key={i} className={`issue-card${selected === i ? ' selected' : ''}`} onClick={() => setSelected(i)}
                style={{ background: c.card, border: `0.5px solid ${c.border}`, borderRadius: '8px', padding: '24px 28px' }}>
                <div style={{ display: 'flex', alignItems: 'flex-start', gap: '16px' }}>
                  <div style={{ width: '20px', height: '20px', borderRadius: '50%', border: `1.5px solid`, borderColor: selected === i ? c.accent : 'rgba(170,85,53,0.3)', background: selected === i ? c.accent : 'transparent', flexShrink: 0, marginTop: '3px', display: 'flex', alignItems: 'center', justifyContent: 'center', transition: 'all .15s' }}>
                    {selected === i && <div style={{ width: '7px', height: '7px', borderRadius: '50%', background: c.text }} />}
                  </div>
                  <div style={{ flex: 1 }}>
                    <div style={{ display: 'flex', gap: '8px', alignItems: 'center', marginBottom: '8px', flexWrap: 'wrap' }}>
                      <span style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '10px', color: c.dim }}>#{issue.number}</span>
                      {issue.labels?.map(l => (
                        <span key={l} style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '10px', padding: '2px 8px', borderRadius: '20px', background: 'rgba(170,85,53,0.08)', color: c.accent2, border: `0.5px solid ${c.border}` }}>{l}</span>
                      ))}
                      <span style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '10px', padding: '2px 8px', borderRadius: '3px', background: lvl.bg, color: lvl.text, border: `0.5px solid ${lvl.border}`, marginLeft: 'auto' }}>{issue.complexity}</span>
                    </div>
                    <div style={{ fontFamily: "'Playfair Display', serif", fontSize: '18px', fontWeight: '400', color: c.text, marginBottom: '10px', lineHeight: '1.4' }}>{issue.title}</div>
                    <div style={{ display: 'flex', gap: '20px' }}>
                      {issue.estimatedHours && <span style={{ fontSize: '12px', color: c.muted, fontWeight: '300' }}>Est. {issue.estimatedHours}</span>}
                      {issue.filesCount && <span style={{ fontSize: '12px', color: c.muted, fontWeight: '300' }}>{issue.filesCount} files to touch</span>}
                    </div>
                  </div>
                </div>
              </div>
            )
          })}
        </div>

        <button onClick={handleGenerate} disabled={selected === null}
          style={{ fontSize: '15px', fontWeight: '500', color: c.text, background: selected === null ? 'rgba(170,85,53,0.4)' : c.accent, border: 'none', padding: '14px 36px', borderRadius: '4px', cursor: selected === null ? 'not-allowed' : 'pointer', fontFamily: "'DM Sans', sans-serif", transition: 'background .2s' }}>
          Generate my contribution guide →
        </button>
      </div>
    </div>
  )
}
