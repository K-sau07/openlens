import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'

const NAV_STYLE = {
  display: 'flex', justifyContent: 'space-between', alignItems: 'center',
  padding: '22px 56px', borderBottom: '0.5px solid rgba(170,85,53,0.25)',
  background: '#100806',
}
const LINK_STYLE = { fontSize: '13px', color: 'rgba(236,217,184,0.5)', textDecoration: 'none' }

export default function Navbar() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = async () => {
    await logout()
    navigate('/')
  }

  return (
    <nav style={NAV_STYLE}>
      <Link to="/" style={{ fontFamily: "'Playfair Display', serif", fontSize: '20px', color: '#ECD9B8', letterSpacing: '-0.3px', textDecoration: 'none' }}>
        OpenLens
      </Link>

      <div style={{ display: 'flex', alignItems: 'center', gap: '28px' }}>
        <a href="https://github.com/K-sau07/openlens" target="_blank" rel="noreferrer" style={LINK_STYLE}>GitHub</a>
        <a href="#" style={LINK_STYLE}>Docs</a>

        {user ? (
          <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
              <div style={{ width: '28px', height: '28px', borderRadius: '50%', background: 'rgba(170,85,53,0.2)', border: '0.5px solid rgba(170,85,53,0.4)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                <span style={{ fontFamily: "'DM Sans', sans-serif", fontSize: '11px', color: '#C4714D', fontWeight: '500' }}>
                  {user.name?.charAt(0).toUpperCase()}
                </span>
              </div>
              <span style={{ fontSize: '13px', color: 'rgba(236,217,184,0.7)', fontFamily: "'DM Sans', sans-serif" }}>{user.name}</span>
            </div>
            <button onClick={handleLogout} style={{ fontSize: '12px', color: 'rgba(236,217,184,0.4)', background: 'none', border: '0.5px solid rgba(170,85,53,0.2)', padding: '6px 14px', borderRadius: '4px', cursor: 'pointer', fontFamily: "'JetBrains Mono', monospace", letterSpacing: '0.5px' }}>
              logout
            </button>
          </div>
        ) : (
          <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
            <Link to="/login" style={{ fontSize: '13px', color: 'rgba(236,217,184,0.6)', textDecoration: 'none', fontFamily: "'DM Sans', sans-serif" }}>Sign in</Link>
            <Link to="/register" style={{ fontSize: '13px', fontWeight: '500', color: '#ECD9B8', background: '#AA5535', padding: '8px 18px', borderRadius: '4px', textDecoration: 'none', fontFamily: "'DM Sans', sans-serif" }}>
              Get started
            </Link>
          </div>
        )}
      </div>
    </nav>
  )
}
