import { useEffect, useState } from 'react'
import { useLocation, useNavigate } from 'react-router-dom'
import { api } from '../services/api'

const c = {
  bg: '#0C0604', text: '#ECD9B8', muted: 'rgba(236,217,184,0.5)',
  dim: 'rgba(236,217,184,0.25)', accent: '#AA5535', accent2: '#C4714D',
  border: 'rgba(170,85,53,0.2)', card: '#130907',
}

const STYLES = `
  @import url('https://fonts.googleapis.com/css2?family=Playfair+Display:ital,wght@0,400;1,400&family=JetBrains+Mono:wght@400&family=DM+Sans:wght@300;400;500&display=swap');
  @keyframes fadeUp{from{opacity:0;transform:translateY(20px)}to{opacity:1;transform:translateY(0)}}
  .q-enter{animation:fadeUp .5s cubic-bezier(.22,1,.36,1) forwards}
  .opt{transition:border-color .15s,background .15s;cursor:pointer}
  .opt:hover{border-color:rgba(170,85,53,.5)!important;background:rgba(170,85,53,.05)!important}
  .opt.selected{border-color:#AA5535!important;background:rgba(170,85,53,.08)!important}
  .next-btn{transition:background .2s,transform .15s;cursor:pointer}
  .next-btn:hover:not(:disabled){background:#C4714D!important;transform:translateY(-1px)}
  .next-btn:disabled{opacity:.4;cursor:not-allowed}
`

// fallback questions used until backend returns real ones
const FALLBACK_QUESTIONS = [
  {
    context: 'Language fit',
    text: 'How comfortable are you reading code you didn\'t write?',
    options: [
      { title: 'Very comfortable', sub: 'I can follow any unfamiliar code within a few minutes' },
      { title: 'Mostly comfortable', sub: 'I can follow it with some time and docs open' },
      { title: 'Some experience', sub: 'I get there eventually but it takes a while' },
      { title: 'Still learning', sub: 'I struggle with unfamiliar codebases' },
    ],
  },
  {
    context: 'Git & PRs',
    text: 'What\'s your experience contributing to repos that aren\'t yours?',
    options: [
      { title: 'I\'ve opened PRs and had them merged', sub: 'Full fork → branch → PR → merge cycle done' },
      { title: 'I\'ve forked and made changes, never opened a PR', sub: 'Done the work but never submitted' },
      { title: 'I mostly work on my own projects', sub: 'Haven\'t contributed to someone else\'s codebase yet' },
      { title: 'Still learning git basics', sub: 'Branching and PRs are new to me' },
    ],
  },
  {
    context: 'Testing',
    text: 'When you write code, do you write tests for it?',
    options: [
      { title: 'Almost always', sub: 'Tests are part of how I work' },
      { title: 'Sometimes', sub: 'When it\'s required or I have time' },
      { title: 'Rarely', sub: 'I test manually but not with automated tests' },
      { title: 'I haven\'t written tests before', sub: 'This would be a first' },
    ],
  },
  {
    context: 'Code reading',
    text: 'You open a file with 150 lines of unfamiliar code. What\'s true for you?',
    options: [
      { title: 'I can figure out what it does in a few minutes', sub: 'Even without comments I can trace the logic' },
      { title: 'I can follow it with comments or docs', sub: 'I need anchors to navigate unfamiliar code' },
      { title: 'I need someone to walk me through it', sub: 'Large files take me a long time' },
      { title: 'I focus on the parts I need to change', sub: 'I search for what\'s relevant rather than reading the whole file' },
    ],
  },
  {
    context: 'PR style',
    text: 'How do you prefer to work when making changes to a codebase?',
    options: [
      { title: 'Small focused changes, one thing at a time', sub: 'I\'d rather do one thing well' },
      { title: 'I tend to go broad but can scope down', sub: 'I refactor nearby things but can hold back' },
      { title: 'Whatever it takes to fix the issue', sub: 'I\'ll do what the issue needs' },
      { title: 'I\'m not sure yet — this would be my first PR', sub: 'I\'ll follow whatever the guide says' },
    ],
  },
]

export default function QuizPage() {
  const { state } = useLocation()
  const navigate = useNavigate()
  const { repoUrl, repoId } = state || {}

  const [questions, setQuestions] = useState([])
  const [current, setCurrent] = useState(0)
  const [answers, setAnswers] = useState([])
  const [selected, setSelected] = useState(null)
  const [submitting, setSubmitting] = useState(false)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    if (!repoUrl) { navigate('/'); return }
    const load = async () => {
      try {
        const res = await api.getQuizQuestions(repoId)
        setQuestions(res.questions || FALLBACK_QUESTIONS)
      } catch {
        setQuestions(FALLBACK_QUESTIONS)
      } finally {
        setLoading(false)
      }
    }
    load()
  }, [repoId, repoUrl, navigate])

  const q = questions[current]
  const total = questions.length
  const repoName = repoUrl?.replace('https://github.com/', '') || ''

  const handleNext = async () => {
    if (selected === null) return
    const newAnswers = [...answers, { questionIndex: current, selectedOption: selected }]
    setAnswers(newAnswers)
    setSelected(null)

    if (current < total - 1) {
      setCurrent(current + 1)
    } else {
      // submit all answers
      setSubmitting(true)
      try {
        const res = await api.submitQuiz(repoId, newAnswers)
        navigate('/issues', { state: { repoUrl, repoId, skillLevel: res.skillLevel, matchedIssues: res.matchedIssues } })
      } catch (e) {
        // on error still navigate with what we have
        navigate('/issues', { state: { repoUrl, repoId, skillLevel: 'intermediate', matchedIssues: [] } })
      }
    }
  }

  if (loading) {
    return (
      <div style={{ background: c.bg, color: c.text, minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center', fontFamily: "'JetBrains Mono', monospace", fontSize: '12px', color: c.muted }}>
        Loading questions...
      </div>
    )
  }

  return (
    <div style={{ background: c.bg, color: c.text, minHeight: '100vh', fontFamily: "'DM Sans', sans-serif" }}>
      <style>{STYLES}</style>

      <nav style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '20px 56px', borderBottom: `0.5px solid ${c.border}` }}>
        <div style={{ fontFamily: "'Playfair Display', serif", fontSize: '20px' }}>OpenLens</div>
        <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '11px', color: c.accent2, background: 'rgba(170,85,53,0.1)', border: `0.5px solid ${c.border}`, padding: '5px 12px', borderRadius: '3px' }}>{repoName}</div>
      </nav>

      <div style={{ maxWidth: '660px', margin: '0 auto', padding: '72px 40px' }}>
        {/* progress steps */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '52px' }}>
          {Array.from({ length: total }).map((_, i) => (
            <div key={i} style={{ height: '2px', flex: 1, borderRadius: '1px', background: i < current ? c.accent : i === current ? c.accent2 : 'rgba(170,85,53,0.15)', transition: 'background .3s' }} />
          ))}
          <span style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '10px', color: c.dim, whiteSpace: 'nowrap' }}>{current + 1} / {total}</span>
        </div>

        {/* question */}
        <div key={current} className="q-enter">
          <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '10px', letterSpacing: '2px', color: c.accent, textTransform: 'uppercase', marginBottom: '16px' }}>
            {q?.context}
          </div>
          <h2 style={{ fontFamily: "'Playfair Display', serif", fontSize: 'clamp(24px, 3.5vw, 34px)', fontWeight: '400', lineHeight: '1.25', marginBottom: '10px' }}>
            {q?.text}
          </h2>
          <p style={{ fontSize: '13px', color: c.muted, fontWeight: '300', marginBottom: '36px' }}>
            Based on what this repo's open issues actually require.
          </p>

          <div style={{ display: 'flex', flexDirection: 'column', gap: '10px', marginBottom: '36px' }}>
            {q?.options?.map((opt, i) => (
              <div key={i} className={`opt${selected === i ? ' selected' : ''}`} onClick={() => setSelected(i)}
                style={{ background: c.card, border: `0.5px solid ${c.border}`, borderRadius: '6px', padding: '16px 20px', display: 'flex', alignItems: 'flex-start', gap: '14px' }}>
                <div style={{ width: '18px', height: '18px', borderRadius: '50%', border: `1.5px solid`, borderColor: selected === i ? c.accent : 'rgba(170,85,53,0.3)', background: selected === i ? c.accent : 'transparent', flexShrink: 0, marginTop: '2px', display: 'flex', alignItems: 'center', justifyContent: 'center', transition: 'all .15s' }}>
                  {selected === i && <div style={{ width: '6px', height: '6px', borderRadius: '50%', background: c.text }} />}
                </div>
                <div>
                  <div style={{ fontSize: '14px', color: c.text, marginBottom: '3px', fontWeight: '400' }}>{opt.title}</div>
                  <div style={{ fontSize: '12px', color: c.muted, fontWeight: '300', lineHeight: '1.5' }}>{opt.sub}</div>
                </div>
              </div>
            ))}
          </div>

          <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
            {current > 0 && (
              <button onClick={() => { setCurrent(current - 1); setSelected(answers[current - 1]?.selectedOption ?? null) }}
                style={{ fontSize: '13px', color: c.muted, background: 'none', border: `0.5px solid ${c.border}`, padding: '11px 20px', borderRadius: '4px', cursor: 'pointer', fontFamily: "'DM Sans', sans-serif" }}>
                Back
              </button>
            )}
            <button className="next-btn" onClick={handleNext} disabled={selected === null || submitting}
              style={{ fontSize: '14px', fontWeight: '500', color: c.text, background: c.accent, border: 'none', padding: '12px 32px', borderRadius: '4px', fontFamily: "'DM Sans', sans-serif", opacity: selected === null ? 0.4 : 1 }}>
              {submitting ? 'Matching...' : current < total - 1 ? 'Next question →' : 'See my matches →'}
            </button>
          </div>
        </div>
      </div>
    </div>
  )
}
