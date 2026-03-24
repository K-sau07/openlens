import { useEffect, useRef, useState } from 'react'

const LINES = [
  { text: '$ openlens analyze apache/kafka', color: '#ECD9B8', delay: 0 },
  { text: '  fetching open issues...', color: 'rgba(236,217,184,0.5)', delay: 700 },
  { text: '  found 50 issues · filtering by complexity', color: 'rgba(236,217,184,0.5)', delay: 1400 },
  { text: '  reading 23 merged PRs...', color: 'rgba(236,217,184,0.5)', delay: 2100 },
  { text: '  profiling maintainer review patterns', color: 'rgba(236,217,184,0.5)', delay: 2800 },
  { text: '  mapping file dependencies...', color: 'rgba(236,217,184,0.5)', delay: 3500 },
  { text: '  generating quiz questions for this repo', color: 'rgba(236,217,184,0.5)', delay: 4200 },
  { text: '', color: '', delay: 4800 },
  { text: '  ✓ 5 questions ready', color: '#7CB67A', delay: 5000 },
  { text: '  ✓ 3 issues matched to your level', color: '#7CB67A', delay: 5400 },
  { text: '  ✓ contribution guide generated', color: '#7CB67A', delay: 5800 },
  { text: '', color: '', delay: 6200 },
  { text: '  guide saved → open in OpenLens', color: '#C4714D', delay: 6400 },
]

export default function TerminalTyper() {
  const [visibleLines, setVisibleLines] = useState([])
  const [cursor, setCursor] = useState(true)
  const ref = useRef(null)
  const started = useRef(false)

  useEffect(() => {
    // blink cursor
    const blink = setInterval(() => setCursor(c => !c), 530)
    return () => clearInterval(blink)
  }, [])

  useEffect(() => {
    const obs = new IntersectionObserver(([e]) => {
      if (e.isIntersecting && !started.current) {
        started.current = true
        LINES.forEach((line, i) => {
          setTimeout(() => {
            setVisibleLines(prev => [...prev, { ...line, idx: i }])
          }, line.delay)
        })
      }
    }, { threshold: 0.3 })
    if (ref.current) obs.observe(ref.current)
    return () => obs.disconnect()
  }, [])

  return (
    <div ref={ref} style={{
      background: '#0A0503',
      border: '0.5px solid rgba(170,85,53,0.3)',
      borderRadius: '8px',
      overflow: 'hidden',
      fontFamily: "'JetBrains Mono', monospace",
    }}>
      <div style={{ padding: '10px 16px', borderBottom: '0.5px solid rgba(170,85,53,0.2)', display: 'flex', alignItems: 'center', gap: '6px', background: 'rgba(170,85,53,0.06)' }}>
        {['#FF5F57','#FEBC2E','#28C840'].map((col, i) => (
          <div key={i} style={{ width: '10px', height: '10px', borderRadius: '50%', background: col, opacity: 0.8 }} />
        ))}
        <span style={{ fontSize: '11px', color: 'rgba(236,217,184,0.3)', marginLeft: '8px' }}>openlens — terminal</span>
      </div>
      <div style={{ padding: '20px 20px 24px', minHeight: '220px' }}>
        {visibleLines.map((line, i) => (
          <div key={i} style={{ fontSize: '12px', lineHeight: '1.9', color: line.color, display: 'flex', alignItems: 'center', gap: '0' }}>
            {line.text}
            {i === visibleLines.length - 1 && (
              <span style={{ display: 'inline-block', width: '8px', height: '14px', background: cursor ? '#AA5535' : 'transparent', marginLeft: '2px', verticalAlign: 'middle' }} />
            )}
          </div>
        ))}
      </div>
    </div>
  )
}
