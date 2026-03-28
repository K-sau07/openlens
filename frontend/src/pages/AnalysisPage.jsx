import { useEffect, useState } from 'react'
import { useLocation, useNavigate } from 'react-router-dom'
import { api } from '../services/api'

const c = {
  bg: '#0C0604', text: '#ECD9B8', muted: 'rgba(236,217,184,0.5)',
  dim: 'rgba(236,217,184,0.25)', accent: '#AA5535', accent2: '#C4714D',
  border: 'rgba(170,85,53,0.2)', card: '#130907', green: '#7CB67A',
}

const STYLES = `
  @import url('https://fonts.googleapis.com/css2?family=Playfair+Display:ital,wght@0,400;1,400&family=JetBrains+Mono:wght@400&family=DM+Sans:wght@300;400;500&display=swap');
  @keyframes fadeUp{from{opacity:0;transform:translateY(16px)}to{opacity:1;transform:translateY(0)}}
  @keyframes pulse{0%,100%{opacity:1}50%{opacity:.3}}
  .log-line{opacity:0;animation:fadeUp .4s ease forwards}
  .dot-pulse{animation:pulse 1.2s ease-in-out infinite}
  .progress-fill{transition:width .6s ease}
`

const LOG_STEPS = [
  { label: 'Fetching repo metadata and language breakdown', count: null },
  { label: 'Reading open issues and labels', count: null },
  { label: 'Scanning merged PR history for patterns', count: null },
  { label: 'Estimating issue complexity by skill level', count: null },
  { label: 'Profiling maintainer review style', count: null },
  { label: 'Generating questions tailored to this repo', count: null },
]

export default function AnalysisPage() {
  const { state } = useLocation()
  const navigate = useNavigate()
  const repoUrl = state?.repoUrl || ''
  const repoName = repoUrl.replace('https://github.com/', '').replace('http://github.com/', '')

  const [visibleSteps, setVisibleSteps] = useState(0)
  const [done, setDone] = useState(false)
  const [error, setError] = useState('')
  const [repoId, setRepoId] = useState(null)

  // simulate log steps appearing while we poll
  useEffect(() => {
    if (!repoUrl) { navigate('/'); return }
    let step = 0
    const stepTimer = setInterval(() => {
      step++
      setVisibleSteps(step)
      if (step >= LOG_STEPS.length) clearInterval(stepTimer)
    }, 900)
    return () => clearInterval(stepTimer)
  }, [repoUrl, navigate])

  // poll backend every 2s until READY
  useEffect(() => {
    if (!repoUrl) return
    let attempts = 0
    const max = 30 // 60s timeout

    const poll = async () => {
      try {
        const res = await api.getRepoStatus(repoUrl)
        if (res.status === 'READY') {
          setRepoId(res.repoId)
          setDone(true)
        } else if (res.status === 'FAILED') {
          setError('Analysis failed. Please try again.')
        } else {
          attempts++
          if (attempts < max) setTimeout(poll, 2000)
          else setError('Analysis is taking too long. Please try again.')
        }
      } catch (e) {
        setError(e.message || 'Failed to check status')
      }
    }
    setTimeout(poll, 2000)
  }, [repoUrl])

  const handleContinue = () => {
    navigate('/quiz', { state: { repoUrl, repoId } })
  }

  const pct = Math.round((visibleSteps / LOG_STEPS.length) * 100)

  return (
    <div style={{ background: c.bg, color: c.text, minHeight: '100vh', fontFamily: "'DM Sans', sans-serif", display: 'flex', flexDirection: 'column' }}>
      <style>{STYLES}</style>

      <nav style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '20px 56px', borderBottom: `0.5px solid ${c.border}` }}>
        <div style={{ fontFamily: "'Playfair Display', serif", fontSize: '20px' }}>OpenLens</div>
        <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '11px', color: c.accent2, background: 'rgba(170,85,53,0.1)', border: `0.5px solid ${c.border}`, padding: '5px 12px', borderRadius: '3px' }}>{repoName}</div>
      </nav>

      <div style={{ flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', padding: '60px 40px' }}>
        <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '10px', letterSpacing: '3px', color: c.accent, textTransform: 'uppercase', marginBottom: '20px' }}>Reading the repo</div>

        <h1 style={{ fontFamily: "'Playfair Display', serif", fontSize: 'clamp(28px, 4vw, 44px)', fontWeight: '400', lineHeight: '1.2', textAlign: 'center', marginBottom: '12px' }}>
          Figuring out what this
          <br /><em style={{ fontStyle: 'italic', color: c.accent2 }}>repo actually needs</em>
        </h1>
        <p style={{ fontSize: '14px', color: c.muted, fontWeight: '300', marginBottom: '48px', textAlign: 'center' }}>
          Before we ask you anything, we read the codebase.
        </p>

        {/* log card */}
        <div style={{ width: '100%', maxWidth: '560px', background: c.card, border: `0.5px solid ${c.border}`, borderRadius: '8px', overflow: 'hidden', marginBottom: '24px' }}>
          <div style={{ padding: '12px 20px', borderBottom: `0.5px solid ${c.border}`, display: 'flex', alignItems: 'center', gap: '10px' }}>
            {!done && !error && <div className="dot-pulse" style={{ width: '7px', height: '7px', borderRadius: '50%', background: c.accent }} />}
            {done && <div style={{ width: '7px', height: '7px', borderRadius: '50%', background: c.green }} />}
            {error && <div style={{ width: '7px', height: '7px', borderRadius: '50%', background: '#E07070' }} />}
            <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '10px', letterSpacing: '1.5px', color: done ? c.green : error ? '#E07070' : c.accent, textTransform: 'uppercase' }}>
              {done ? 'Analysis complete' : error ? 'Analysis failed' : 'Analysis running'}
            </div>
          </div>
          <div style={{ padding: '16px 20px', display: 'flex', flexDirection: 'column', gap: '10px', minHeight: '180px' }}>
            {LOG_STEPS.slice(0, visibleSteps).map((step, i) => (
              <div key={i} className="log-line" style={{ display: 'flex', alignItems: 'center', gap: '10px', animationDelay: `${i * 0.05}s` }}>
                <div style={{ width: '14px', height: '14px', borderRadius: '50%', border: `1px solid`, borderColor: i < visibleSteps - 1 || done ? c.green : c.accent, background: i < visibleSteps - 1 || done ? 'rgba(124,182,122,0.1)' : 'transparent', display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }}>
                  {(i < visibleSteps - 1 || done) && <div style={{ width: '5px', height: '5px', borderRadius: '50%', background: c.green }} />}
                </div>
                <span style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '11.5px', color: i < visibleSteps - 1 || done ? 'rgba(236,217,184,0.6)' : c.muted }}>{step.label}</span>
              </div>
            ))}
          </div>
        </div>

        {/* progress bar */}
        <div style={{ width: '100%', maxWidth: '560px', marginBottom: '32px' }}>
          <div style={{ height: '2px', background: 'rgba(170,85,53,0.1)', borderRadius: '1px', overflow: 'hidden' }}>
            <div className="progress-fill" style={{ height: '100%', width: `${done ? 100 : pct}%`, background: done ? c.green : c.accent, borderRadius: '1px' }} />
          </div>
          <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: '8px' }}>
            <span style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '10px', color: c.dim }}>{done ? 'Ready' : 'Analyzing...'}</span>
            <span style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '10px', color: c.dim }}>{done ? '100%' : `${pct}%`}</span>
          </div>
        </div>

        {error && <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '12px', color: '#E07070', marginBottom: '20px' }}>{error}</div>}

        {done && (
          <button onClick={handleContinue} style={{ fontFamily: "'DM Sans', sans-serif", fontSize: '15px', fontWeight: '500', color: c.text, background: c.accent, border: 'none', padding: '14px 40px', borderRadius: '4px', cursor: 'pointer' }}>
            Answer 5 questions →
          </button>
        )}
      </div>
    </div>
  )
}
