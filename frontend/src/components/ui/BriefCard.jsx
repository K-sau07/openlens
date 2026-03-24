const s = {
  card: {
    background: '#1A0E0A',
    border: '0.5px solid rgba(170,85,53,0.35)',
    borderRadius: '10px',
    width: '100%',
    maxWidth: '420px',
    overflow: 'hidden',
  },
  cardTop: {
    padding: '24px 28px 20px',
    borderBottom: '0.5px solid rgba(170,85,53,0.15)',
  },
  cardLabel: {
    fontFamily: "'JetBrains Mono', monospace",
    fontSize: '9px',
    letterSpacing: '2.5px',
    color: '#AA5535',
    textTransform: 'uppercase',
    marginBottom: '14px',
  },
  tags: { display: 'flex', gap: '8px', marginBottom: '16px', flexWrap: 'wrap' },
  tag: {
    fontFamily: "'JetBrains Mono', monospace",
    fontSize: '10px',
    padding: '4px 10px',
    borderRadius: '3px',
    background: 'rgba(170,85,53,0.12)',
    color: '#C4714D',
    border: '0.5px solid rgba(170,85,53,0.25)',
  },
  tagLevel: {
    fontFamily: "'JetBrains Mono', monospace",
    fontSize: '10px',
    padding: '4px 10px',
    borderRadius: '3px',
    background: 'rgba(100,170,100,0.1)',
    color: '#7CB67A',
    border: '0.5px solid rgba(100,170,100,0.22)',
  },
  issueTitle: {
    fontFamily: "'Playfair Display', serif",
    fontSize: '18px',
    fontWeight: '400',
    lineHeight: '1.4',
    marginBottom: '10px',
  },
  chips: { display: 'flex', gap: '6px' },
  chip: {
    fontSize: '10px',
    padding: '3px 9px',
    borderRadius: '20px',
    background: 'rgba(170,85,53,0.1)',
    color: '#C4714D',
    border: '0.5px solid rgba(170,85,53,0.2)',
  },
  chipHelp: {
    fontSize: '10px',
    padding: '3px 9px',
    borderRadius: '20px',
    background: 'rgba(80,120,170,0.1)',
    color: '#7AA8D8',
    border: '0.5px solid rgba(80,120,170,0.2)',
  },
  section: { padding: '18px 28px', borderBottom: '0.5px solid rgba(170,85,53,0.12)' },
  sectionLast: { padding: '18px 28px' },
  sLabel: {
    fontFamily: "'JetBrains Mono', monospace",
    fontSize: '9px',
    letterSpacing: '2px',
    color: 'rgba(170,85,53,0.7)',
    textTransform: 'uppercase',
    marginBottom: '10px',
  },
  filesList: { listStyle: 'none', display: 'flex', flexDirection: 'column', gap: '6px' },
  fileItem: {
    fontFamily: "'JetBrains Mono', monospace",
    fontSize: '11.5px',
    color: 'rgba(236,217,184,0.7)',
    display: 'flex',
    alignItems: 'center',
    gap: '8px',
  },
  fileDash: { width: '14px', height: '1px', background: '#AA5535', flexShrink: 0 },
  maintainerText: { fontSize: '13px', lineHeight: '1.7', color: 'rgba(236,217,184,0.5)', fontWeight: '300' },
  prList: { display: 'flex', flexDirection: 'column', gap: '8px' },
  prRow: { display: 'flex', alignItems: 'center', gap: '10px' },
  prNum: { fontFamily: "'JetBrains Mono', monospace", fontSize: '10px', color: '#AA5535', minWidth: '30px' },
  prTitle: { fontSize: '12.5px', color: 'rgba(236,217,184,0.65)', flex: 1 },
  prBadge: {
    fontFamily: "'JetBrains Mono', monospace",
    fontSize: '9px',
    color: '#7CB67A',
    background: 'rgba(100,170,100,0.08)',
    border: '0.5px solid rgba(100,170,100,0.2)',
    padding: '2px 7px',
    borderRadius: '3px',
  },
}

export default function BriefCard() {
  return (
    <div style={s.card}>
      <div style={s.cardTop}>
        <div style={s.cardLabel}>Contribution Guide</div>
        <div style={s.tags}>
          <span style={s.tag}>opencodeintel/opencodeintel</span>
          <span style={s.tagLevel}>Beginner</span>
        </div>
        <div style={s.issueTitle}>Add support for multi-file context</div>
        <div style={s.chips}>
          <span style={s.chip}>good first issue</span>
          <span style={s.chipHelp}>help wanted</span>
        </div>
      </div>

      <div style={s.section}>
        <div style={s.sLabel}>Files to touch</div>
        <ul style={s.filesList}>
          {['src/context/builder.py', 'src/api/routes/context.py', 'tests/test_context_builder.py'].map(f => (
            <li key={f} style={s.fileItem}>
              <span style={s.fileDash} />
              {f}
            </li>
          ))}
        </ul>
      </div>

      <div style={s.section}>
        <div style={s.sLabel}>Maintainer pattern</div>
        <p style={s.maintainerText}>
          Prefers small, focused PRs. Always includes tests. Responds within 48 hours.
          References issue number in PR title. Squash merges.
        </p>
      </div>

      <div style={s.sectionLast}>
        <div style={s.sLabel}>Similar merged PRs</div>
        <div style={s.prList}>
          {[
            { num: '#23', title: 'added single-file context endpoint' },
            { num: '#31', title: 'added language detection to parser' },
            { num: '#38', title: 'fixed context truncation for large files' },
          ].map(pr => (
            <div key={pr.num} style={s.prRow}>
              <span style={s.prNum}>{pr.num}</span>
              <span style={s.prTitle}>{pr.title}</span>
              <span style={s.prBadge}>merged</span>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}
