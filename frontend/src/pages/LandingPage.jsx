import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { api } from '../services/api'

const c = {
  bg: '#0C0604', text: '#ECD9B8', muted: 'rgba(236,217,184,0.5)',
  dim: 'rgba(236,217,184,0.25)', accent: '#AA5535', accent2: '#C4714D',
  border: 'rgba(170,85,53,0.2)', card: '#130907',
}

const STYLES = `
  @import url('https://fonts.googleapis.com/css2?family=Playfair+Display:ital,wght@0,400;1,400&family=JetBrains+Mono:wght@400;500&family=DM+Sans:wght@300;400;500&display=swap');
  @keyframes fadeUp { from{opacity:0;transform:translateY(32px)} to{opacity:1;transform:translateY(0)} }
  @keyframes ctaPulse {
    0%,100%{box-shadow:0 0 0 0 rgba(170,85,53,.5)}
    60%{box-shadow:0 0 0 12px rgba(170,85,53,0)}
  }
  .h1{opacity:0;animation:fadeUp .9s cubic-bezier(.22,1,.36,1) .1s forwards}
  .h2{opacity:0;animation:fadeUp .7s cubic-bezier(.22,1,.36,1) .25s forwards}
  .h3{opacity:0;animation:fadeUp .7s cubic-bezier(.22,1,.36,1) .4s forwards}
  .h4{opacity:0;animation:fadeUp .7s cubic-bezier(.22,1,.36,1) .5s forwards}
  .repo-input:focus{outline:none;border-color:rgba(170,85,53,.6)!important}
  .go-btn{animation:ctaPulse 3s ease-in-out infinite;transition:background .2s,transform .15s}
  .go-btn:hover{background:#C4714D!important;transform:translateY(-1px)}
  .go-btn:disabled{animation:none;opacity:.5;cursor:not-allowed}
  .ticker-track{display:flex;width:max-content;animation:ticker 44s linear infinite}
  @keyframes ticker{from{transform:translateX(0)}to{transform:translateX(-50%)}}
`

const TICKER_ITEMS = [
  'apache/kafka · Issue #4821 · beginner',
  'vercel/next.js · Issue #62103 · intermediate',
  'opencodeintel/opencodeintel · Issue #17 · beginner',
  'kubernetes/kubernetes · Issue #119204 · advanced',
  'spring-projects/spring-boot · Issue #38771 · intermediate',
]

export default function LandingPage() {
  const navigate = useNavigate()
  const [repoUrl, setRepoUrl] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleSubmit = async () => {
    const url = repoUrl.trim()
    if (!url) { setError('Paste a GitHub repo URL to get started'); return }
    if (!url.includes('github.com')) { setError('Must be a GitHub repository URL'); return }
    setError('')
    setLoading(true)
    try {
      const res = await api.analyzeRepo(url)
      navigate('/analysis', { state: { repoUrl: url, jobId: res.jobId } })
    } catch (e) {
      setError(e.message || 'Something went wrong, try again')
      setLoading(false)
    }
  }

  const handleKey = (e) => { if (e.key === 'Enter') handleSubmit() }

  return (
    <div style={{ background: c.bg, color: c.text, minHeight: '100vh', fontFamily: "'DM Sans', sans-serif" }}>
      <style>{STYLES}</style>

      {/* NAV */}
      <nav style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '20px 56px', borderBottom: `0.5px solid ${c.border}` }}>
        <div style={{ fontFamily: "'Playfair Display', serif", fontSize: '20px' }}>OpenLens</div>
        <div style={{ display: 'flex', gap: '28px', alignItems: 'center' }}>
          <a href="https://github.com/K-sau07/openlens" target="_blank" rel="noreferrer" style={{ fontSize: '13px', color: c.muted, textDecoration: 'none' }}>GitHub</a>
          <a href="#" style={{ fontSize: '13px', color: c.muted, textDecoration: 'none' }}>Docs</a>
        </div>
      </nav>

      {/* TICKER */}
      <div style={{ overflow: 'hidden', borderBottom: `0.5px solid ${c.border}`, background: 'rgba(170,85,53,0.04)', padding: '9px 0' }}>
        <div className="ticker-track">
          {[...TICKER_ITEMS, ...TICKER_ITEMS].map((item, i) => (
            <span key={i} style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '11px', color: c.dim, padding: '0 24px', whiteSpace: 'nowrap' }}>
              <span style={{ color: c.accent2 }}>{item.split(' · ')[0]}</span>
              <span style={{ color: c.accent, padding: '0 6px' }}>·</span>
              {item.split(' · ').slice(1).join(' · ')}
            </span>
          ))}
        </div>
      </div>

      {/* HERO */}
      <div style={{ minHeight: 'calc(100vh - 120px)', display: 'flex', flexDirection: 'column', justifyContent: 'center', alignItems: 'center', textAlign: 'center', padding: '80px 40px', position: 'relative', overflow: 'hidden' }}>
        <div style={{ position: 'absolute', top: '40%', left: '50%', transform: 'translate(-50%,-50%)', width: '700px', height: '700px', background: 'radial-gradient(circle, rgba(170,85,53,0.07) 0%, transparent 65%)', pointerEvents: 'none' }} />

        <div className="h1" style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '10px', letterSpacing: '3px', color: c.accent, textTransform: 'uppercase', marginBottom: '28px' }}>
          Open Source Intelligence
        </div>

        <h1 className="h2" style={{ fontFamily: "'Playfair Display', serif", fontSize: 'clamp(44px, 6.5vw, 84px)', lineHeight: '1.06', fontWeight: '400', letterSpacing: '-1px', maxWidth: '860px', marginBottom: '28px' }}>
          You've been told to contribute.
          <br />
          <em style={{ fontStyle: 'italic', color: c.accent2 }}>Nobody told you how.</em>
        </h1>

        <p className="h3" style={{ fontSize: '17px', lineHeight: '1.75', color: c.muted, fontWeight: '300', maxWidth: '500px', marginBottom: '52px' }}>
          Paste a GitHub repo below. We analyze it, ask you 5 quick questions, and generate a personalized contribution guide — matched issues, files to touch, maintainer patterns.
        </p>

        {/* REPO INPUT */}
        <div className="h4" style={{ width: '100%', maxWidth: '560px', display: 'flex', flexDirection: 'column', gap: '12px' }}>
          <div style={{ display: 'flex', gap: '0', border: `0.5px solid rgba(170,85,53,0.35)`, borderRadius: '6px', overflow: 'hidden', background: c.card }}>
            <input
              className="repo-input"
              type="text"
              value={repoUrl}
              onChange={e => { setRepoUrl(e.target.value); setError('') }}
              onKeyDown={handleKey}
              placeholder="https://github.com/owner/repo"
              disabled={loading}
              style={{ flex: 1, padding: '14px 20px', background: 'transparent', border: 'none', color: c.text, fontFamily: "'JetBrains Mono', monospace", fontSize: '13px', outline: 'none' }}
            />
            <button
              className="go-btn"
              onClick={handleSubmit}
              disabled={loading}
              style={{ padding: '14px 28px', background: c.accent, border: 'none', color: c.text, fontFamily: "'DM Sans', sans-serif", fontSize: '14px', fontWeight: '500', cursor: 'pointer', whiteSpace: 'nowrap' }}
            >
              {loading ? 'Analyzing...' : 'Analyze repo →'}
            </button>
          </div>
          {error && <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '11px', color: '#E07070', textAlign: 'left', paddingLeft: '4px' }}>{error}</div>}
          <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '10px', color: c.dim, textAlign: 'center', letterSpacing: '0.3px' }}>
            No sign-in required · Any public GitHub repo
          </div>
        </div>

        {/* scroll hint */}
        <div style={{ position: 'absolute', bottom: '36px', left: '50%', transform: 'translateX(-50%)', display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '6px' }}>
          <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '9px', letterSpacing: '2px', color: 'rgba(236,217,184,0.15)', textTransform: 'uppercase' }}>scroll to learn more</div>
          <div style={{ width: '1px', height: '32px', background: 'linear-gradient(to bottom, rgba(170,85,53,0.3), transparent)' }} />
        </div>
      </div>
    </div>
  )
}
