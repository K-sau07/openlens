import { Link } from 'react-router-dom'

export default function Navbar() {
  return (
    <nav style={{
      display: 'flex',
      justifyContent: 'space-between',
      alignItems: 'center',
      padding: '22px 56px',
      borderBottom: '0.5px solid rgba(170,85,53,0.25)',
      background: '#100806',
    }}>
      <Link to="/" style={{
        fontFamily: "'Playfair Display', serif",
        fontSize: '20px',
        color: '#ECD9B8',
        letterSpacing: '-0.3px',
      }}>
        OpenLens
      </Link>

      <div style={{ display: 'flex', alignItems: 'center', gap: '32px' }}>
        <a
          href="https://github.com/K-sau07/openlens"
          target="_blank"
          rel="noreferrer"
          style={{ fontSize: '13px', color: 'rgba(236,217,184,0.5)' }}
        >
          GitHub
        </a>
        <a href="#" style={{ fontSize: '13px', color: 'rgba(236,217,184,0.5)' }}>
          Docs
        </a>
        <button style={{
          fontSize: '13px',
          fontWeight: '500',
          color: '#ECD9B8',
          background: '#AA5535',
          border: 'none',
          padding: '8px 18px',
          borderRadius: '4px',
          cursor: 'pointer',
        }}>
          Browse Briefs
        </button>
      </div>
    </nav>
  )
}
