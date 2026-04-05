import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'

const c = {
  bg: '#0C0604', text: '#ECD9B8', muted: 'rgba(236,217,184,0.5)',
  dim: 'rgba(236,217,184,0.25)', accent: '#AA5535', accent2: '#C4714D',
  border: 'rgba(170,85,53,0.2)', card: '#130907',
}

const STYLES = `
  @import url('https://fonts.googleapis.com/css2?family=Playfair+Display:ital,wght@0,400;1,400&family=JetBrains+Mono:wght@400&family=DM+Sans:wght@300;400;500&display=swap');
  @keyframes fadeUp{from{opacity:0;transform:translateY(20px)}to{opacity:1;transform:translateY(0)}}
  .auth-card{animation:fadeUp .6s cubic-bezier(.22,1,.36,1) forwards}
  .auth-input{transition:border-color .15s}
  .auth-input:focus{outline:none;border-color:rgba(170,85,53,.6)!important}
  .auth-btn{transition:background .2s,transform .15s;cursor:pointer}
  .auth-btn:hover:not(:disabled){background:#C4714D!important;transform:translateY(-1px)}
  .auth-btn:disabled{opacity:.5;cursor:not-allowed}
`

export default function LoginPage() {
  const navigate = useNavigate()
  const { setUser } = useAuth()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleSubmit = async () => {
    if (!email || !password) { setError('please fill in all fields'); return }
    setError(''); setLoading(true)
    try {
      const res = await fetch('/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({ email, password }),
      })
      const data = await res.json()
      if (!res.ok) { setError(data.error || 'login failed'); return }
      const me = await fetch('/api/auth/me', { credentials: 'include' })
      if (me.ok) setUser(await me.json())
      navigate('/')
    } catch {
      setError('something went wrong, try again')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div style={{ background: c.bg, color: c.text, minHeight: '100vh', fontFamily: "'DM Sans', sans-serif", display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', padding: '40px' }}>
      <style>{STYLES}</style>
      <div className="auth-card" style={{ width: '100%', maxWidth: '420px' }}>
        <div style={{ textAlign: 'center', marginBottom: '40px' }}>
          <div style={{ fontFamily: "'Playfair Display', serif", fontSize: '24px', marginBottom: '8px' }}>OpenLens</div>
          <div style={{ fontSize: '13px', color: c.muted, fontWeight: '300' }}>Sign in to your account</div>
        </div>
        <div style={{ background: c.card, border: `0.5px solid ${c.border}`, borderRadius: '8px', padding: '32px' }}>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
            <div>
              <label style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '10px', letterSpacing: '1.5px', color: c.muted, textTransform: 'uppercase', display: 'block', marginBottom: '8px' }}>Email</label>
              <input className="auth-input" type="email" value={email} onChange={e => setEmail(e.target.value)}
                onKeyDown={e => e.key === 'Enter' && handleSubmit()}
                placeholder="you@example.com"
                style={{ width: '100%', padding: '11px 14px', background: 'rgba(170,85,53,0.05)', border: `0.5px solid ${c.border}`, borderRadius: '4px', color: c.text, fontFamily: "'DM Sans', sans-serif", fontSize: '14px', boxSizing: 'border-box' }} />
            </div>
            <div>
              <label style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '10px', letterSpacing: '1.5px', color: c.muted, textTransform: 'uppercase', display: 'block', marginBottom: '8px' }}>Password</label>
              <input className="auth-input" type="password" value={password} onChange={e => setPassword(e.target.value)}
                onKeyDown={e => e.key === 'Enter' && handleSubmit()}
                placeholder="••••••••"
                style={{ width: '100%', padding: '11px 14px', background: 'rgba(170,85,53,0.05)', border: `0.5px solid ${c.border}`, borderRadius: '4px', color: c.text, fontFamily: "'DM Sans', sans-serif", fontSize: '14px', boxSizing: 'border-box' }} />
            </div>
            {error && <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '11px', color: '#E07070' }}>{error}</div>}
            <button className="auth-btn" onClick={handleSubmit} disabled={loading}
              style={{ width: '100%', padding: '12px', background: c.accent, border: 'none', borderRadius: '4px', color: c.text, fontFamily: "'DM Sans', sans-serif", fontSize: '14px', fontWeight: '500', marginTop: '4px' }}>
              {loading ? 'Signing in...' : 'Sign in'}
            </button>
          </div>
          <div style={{ textAlign: 'center', marginTop: '24px', fontSize: '13px', color: c.muted }}>
            No account?{' '}
            <Link to="/register" style={{ color: c.accent2, textDecoration: 'none', fontWeight: '500' }}>Create one</Link>
          </div>
        </div>
        <div style={{ textAlign: 'center', marginTop: '24px' }}>
          <Link to="/" style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '11px', color: c.dim, textDecoration: 'none' }}>← back to home</Link>
        </div>
      </div>
    </div>
  )
}
