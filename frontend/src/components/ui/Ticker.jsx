const ITEMS = [
  { repo: 'apache/kafka', issue: 'Issue #4821 — Add partition rebalancing test', level: 'beginner' },
  { repo: 'vercel/next.js', issue: 'Issue #62103 — Fix hydration mismatch in app router', level: 'intermediate' },
  { repo: 'opencodeintel/opencodeintel', issue: 'Issue #17 — Add multi-file context support', level: 'beginner' },
  { repo: 'kubernetes/kubernetes', issue: 'Issue #119204 — Improve CRD validation error', level: 'advanced' },
  { repo: 'spring-projects/spring-boot', issue: 'Issue #38771 — Document virtual thread config', level: 'intermediate' },
]

// duplicate for seamless loop
const ALL = [...ITEMS, ...ITEMS]

export default function Ticker() {
  return (
    <div style={{
      overflow: 'hidden',
      borderBottom: '0.5px solid rgba(170,85,53,0.2)',
      background: 'rgba(170,85,53,0.05)',
      padding: '10px 0',
    }}>
      <div style={{
        display: 'flex',
        width: 'max-content',
        animation: 'ticker 44s linear infinite',
      }}>
        {ALL.map((item, i) => (
          <span key={i} style={{
            fontFamily: "'JetBrains Mono', monospace",
            fontSize: '11px',
            color: 'rgba(236,217,184,0.4)',
            padding: '0 24px',
            whiteSpace: 'nowrap',
          }}>
            <span style={{ color: '#C4714D' }}>{item.repo}</span>
            <span style={{ color: '#AA5535', padding: '0 6px' }}>·</span>
            {item.issue}
            <span style={{ color: '#AA5535', padding: '0 6px' }}>·</span>
            Contribution guide generated · {item.level}
          </span>
        ))}
      </div>

      <style>{`
        @keyframes ticker {
          from { transform: translateX(0); }
          to { transform: translateX(-50%); }
        }
      `}</style>
    </div>
  )
}
