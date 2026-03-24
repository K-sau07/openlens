import Navbar from '../components/layout/Navbar'
import Ticker from '../components/ui/Ticker'
import BriefCard from '../components/ui/BriefCard'

const c = {
  bg: '#100806', bg2: '#160C08', bg3: '#1A0E0A',
  text: '#ECD9B8', muted: 'rgba(236,217,184,0.5)', dim: 'rgba(236,217,184,0.28)',
  accent: '#AA5535', accent2: '#C4714D',
  border: 'rgba(170,85,53,0.25)', border2: 'rgba(170,85,53,0.15)',
  green: '#7CB67A', greenBg: 'rgba(124,182,122,0.08)', greenBorder: 'rgba(124,182,122,0.2)',
  card: '#1A0E0A',
}

function Hero() {
  return (
    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', minHeight: '820px', alignItems: 'center' }}>
      <div style={{ padding: '80px 56px', display: 'flex', flexDirection: 'column', justifyContent: 'center', borderRight: `0.5px solid ${c.border2}`, minHeight: '820px' }}>
        <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '10px', letterSpacing: '3px', color: c.accent, textTransform: 'uppercase', marginBottom: '28px' }}>
          Open Source Intelligence
        </div>
        <h1 style={{ fontFamily: "'Playfair Display', serif", fontSize: '52px', lineHeight: '1.1', fontWeight: '400', letterSpacing: '-0.5px', marginBottom: '28px' }}>
          You've been told<br />to contribute.<br />
          <em style={{ fontStyle: 'italic', color: c.accent2 }}>Nobody told<br />you how.</em>
        </h1>
        <p style={{ fontSize: '15px', lineHeight: '1.75', color: c.muted, fontWeight: '300', marginBottom: '44px', maxWidth: '400px' }}>
          Paste a GitHub repo. Tell us your level. Get a contribution guide that actually makes sense — matched issues, files to touch, maintainer patterns, and everything you need to get your first PR merged.
        </p>
        <button style={{ fontSize: '14px', fontWeight: '500', color: c.text, background: c.accent, border: 'none', padding: '13px 30px', borderRadius: '4px', cursor: 'pointer', width: 'fit-content', marginBottom: '12px' }}>
          Browse Demo Guides
        </button>
        <span style={{ fontSize: '12px', color: c.dim }}>No sign-in required. Just a repo URL and a skill level.</span>
      </div>
      <div style={{ padding: '56px', display: 'flex', alignItems: 'center', justifyContent: 'center', minHeight: '820px', background: 'rgba(26,14,10,0.5)' }}>
        <BriefCard />
      </div>
    </div>
  )
}

function Story() {
  const stats = [
    { num: '84%', label: 'of developers who want to contribute to open source never make their first PR' },
    { num: '~3hrs', label: 'average time spent reading a codebase before giving up and closing the tab' },
    { num: '0', label: 'existing tools that tell you which files to touch, how this maintainer reviews, and what gets merged' },
  ]
  return (
    <div style={{ padding: '100px 56px', background: c.bg2 }}>
      <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '10px', letterSpacing: '3px', color: c.accent, textTransform: 'uppercase', marginBottom: '20px' }}>The problem</div>
      <h2 style={{ fontFamily: "'Playfair Display', serif", fontSize: '42px', fontWeight: '400', lineHeight: '1.15', letterSpacing: '-0.3px', maxWidth: '640px' }}>
        Most developers never make their first PR.<br />
        <em style={{ fontStyle: 'italic', color: c.accent2 }}>Here's why.</em>
      </h2>
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '64px', marginTop: '56px', alignItems: 'start' }}>
        <div style={{ fontSize: '15px', lineHeight: '1.85', color: c.muted, fontWeight: '300', maxWidth: '460px', display: 'flex', flexDirection: 'column', gap: '20px' }}>
          <p>You find a repo you care about. You clone it. You read the README. You scroll through open issues for twenty minutes. They all feel either too small to matter or too large to touch. You close the tab.</p>
          <p>This isn't a motivation problem. It's an <strong style={{ color: c.text, fontWeight: '500' }}>information problem.</strong> The repo doesn't tell you which issue fits your level, which files you'd need to change, or what kind of PR this maintainer actually merges.</p>
          <p>goodfirstissue.dev gives you a list. GitHub gives you labels. Neither gives you a <strong style={{ color: c.text, fontWeight: '500' }}>contribution guide built for you</strong> — matched to your skill level, shaped around how this specific repo works.</p>
          <p>That's what OpenLens does. Paste a repo, answer 5 quick questions, and walk away with everything you need to open a PR with confidence.</p>
        </div>
        <div>
          {stats.map((s, i) => (
            <div key={i} style={{ padding: '28px 0', borderBottom: i < stats.length - 1 ? `0.5px solid ${c.border2}` : 'none', borderTop: i === 0 ? `0.5px solid ${c.border2}` : 'none' }}>
              <div style={{ fontFamily: "'Playfair Display', serif", fontSize: '40px', fontWeight: '400', color: c.accent2, lineHeight: '1', marginBottom: '6px' }}>{s.num}</div>
              <div style={{ fontSize: '13px', color: c.muted, fontWeight: '300', lineHeight: '1.5' }}>{s.label}</div>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}

function HowItWorks() {
  const steps = [
    { num: '01', title: ['Paste a', 'repo URL'], body: 'Any public GitHub repository. OpenLens pulls the issue list, commit history, merged PRs, and contributor patterns in the background.' },
    { num: '02', title: ['Answer 5', 'quick questions'], body: 'Repo-specific questions generated from what the codebase actually needs — not a generic skill dropdown.' },
    { num: '03', title: ['Get your', 'contribution guide'], body: 'A full guide: matched issues, files to touch, maintainer patterns, similar merged PRs, and a step-by-step path to your first merged PR.' },
  ]
  return (
    <div style={{ padding: '100px 56px', background: c.bg }}>
      <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '10px', letterSpacing: '3px', color: c.accent, textTransform: 'uppercase', marginBottom: '20px' }}>How it works</div>
      <h2 style={{ fontFamily: "'Playfair Display', serif", fontSize: '38px', fontWeight: '400', lineHeight: '1.2', letterSpacing: '-0.3px', marginBottom: '56px', maxWidth: '560px' }}>
        Three steps from <em style={{ fontStyle: 'italic', color: c.accent2 }}>cold repo</em> to merged PR
      </h2>
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '2px' }}>
        {steps.map((step, i) => (
          <div key={i} style={{ background: c.bg3, padding: '36px 32px', border: `0.5px solid ${c.border2}`, position: 'relative' }}>
            <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '11px', color: c.accent, marginBottom: '20px', letterSpacing: '1px' }}>{step.num} —</div>
            <div style={{ fontFamily: "'Playfair Display', serif", fontSize: '22px', fontWeight: '400', lineHeight: '1.3', marginBottom: '12px' }}>
              {step.title[0]}<br /><em style={{ fontStyle: 'italic', color: c.accent2 }}>{step.title[1]}</em>
            </div>
            <div style={{ fontSize: '13.5px', lineHeight: '1.7', color: c.muted, fontWeight: '300' }}>{step.body}</div>
            {i < steps.length - 1 && (
              <div style={{ position: 'absolute', right: '-1px', top: '50%', transform: 'translateY(-50%)', width: '2px', height: '32px', background: c.accent, opacity: '0.4' }} />
            )}
          </div>
        ))}
      </div>
    </div>
  )
}

function WhatYouGet() {
  const cards = [
    { num: '01', label: 'Issues', title: ['Issues matched', 'to your level'], body: "Not a dump of every \"good first issue\" label. OpenLens reads the actual code complexity, estimates the effort, and surfaces only the issues that fit where you are right now." },
    { num: '02', label: 'Files', title: ['Exactly which', 'files to touch'], body: "Stop reading the entire codebase to figure out where to start. OpenLens maps the issue to the specific files, functions, and tests you'd need to change." },
    { num: '03', label: 'Maintainer', title: ['How this maintainer', 'actually thinks'], body: "Every maintainer has a style. OpenLens reads merged PR history so your PR lands the way they expect it — and gets merged, not ignored." },
    { num: '04', label: 'Guide', title: ['A doc you follow', 'start to finish'], body: "A complete contribution guide built for this repo, this issue, and your level. Walk in, make the PR, walk out. No more staring at the screen wondering what to do next." },
  ]
  return (
    <div style={{ padding: '100px 56px', background: c.bg2 }}>
      <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '10px', letterSpacing: '3px', color: c.accent, textTransform: 'uppercase', marginBottom: '20px' }}>What's inside your guide</div>
      <h2 style={{ fontFamily: "'Playfair Display', serif", fontSize: '38px', fontWeight: '400', lineHeight: '1.2', letterSpacing: '-0.3px', marginBottom: '56px', maxWidth: '560px' }}>
        Everything the repo <em style={{ fontStyle: 'italic', color: c.accent2 }}>won't tell you itself</em>
      </h2>
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '2px' }}>
        {cards.map((card, i) => (
          <div key={i} style={{ background: c.bg3, padding: '36px 32px', border: `0.5px solid ${c.border2}` }}>
            <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '9px', letterSpacing: '2px', color: c.accent, textTransform: 'uppercase', marginBottom: '16px' }}>{card.num} · {card.label}</div>
            <div style={{ fontFamily: "'Playfair Display', serif", fontSize: '22px', fontWeight: '400', lineHeight: '1.3', marginBottom: '12px' }}>
              {card.title[0]}<br /><em style={{ fontStyle: 'italic', color: c.accent2 }}>{card.title[1]}</em>
            </div>
            <div style={{ fontSize: '13.5px', lineHeight: '1.75', color: c.muted, fontWeight: '300' }}>{card.body}</div>
          </div>
        ))}
      </div>
    </div>
  )
}

function DemoRepo() {
  const stats = [
    { num: '50', label: 'open issues' },
    { num: '0', label: 'merged PRs' },
    { num: '3', label: 'skill levels' },
  ]
  return (
    <div style={{ padding: '100px 56px', background: c.bg3, borderTop: `0.5px solid ${c.border2}`, borderBottom: `0.5px solid ${c.border2}` }}>
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '80px', alignItems: 'center' }}>
        <div>
          <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '10px', letterSpacing: '3px', color: c.accent, textTransform: 'uppercase', marginBottom: '20px' }}>Try it now</div>
          <h2 style={{ fontFamily: "'Playfair Display', serif", fontSize: '38px', fontWeight: '400', lineHeight: '1.2', letterSpacing: '-0.3px', marginBottom: '20px' }}>
            Start with a real repo.<br /><em style={{ fontStyle: 'italic', color: c.accent2 }}>See what we build for you.</em>
          </h2>
          <p style={{ fontSize: '15px', lineHeight: '1.75', color: c.muted, fontWeight: '300', maxWidth: '400px', marginBottom: '32px' }}>
            opencodeintel is a Python codebase intelligence tool with 50 open issues and a clean, well-maintained history. It's the perfect first repo to try OpenLens on — real issues, active maintainer, mergeable PRs.
          </p>
          <button style={{ fontSize: '14px', fontWeight: '500', color: c.text, background: c.accent, border: 'none', padding: '13px 30px', borderRadius: '4px', cursor: 'pointer', marginBottom: '10px', display: 'block' }}>
            Generate my contribution guide
          </button>
          <div style={{ fontSize: '12px', color: c.dim }}>Uses opencodeintel/opencodeintel · No sign-in required</div>
        </div>
        <div style={{ background: c.card, border: `0.5px solid rgba(170,85,53,0.35)`, borderRadius: '10px', padding: '28px 32px' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '12px', marginBottom: '20px' }}>
            <div style={{ width: '36px', height: '36px', background: 'rgba(170,85,53,0.15)', borderRadius: '6px', display: 'flex', alignItems: 'center', justifyContent: 'center', border: `0.5px solid ${c.border}`, flexShrink: 0 }}>
              <div style={{ width: '14px', height: '14px', border: `1.5px solid ${c.accent2}`, borderRadius: '3px' }} />
            </div>
            <div>
              <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '13px', color: c.text }}>opencodeintel/opencodeintel</div>
              <div style={{ fontSize: '12px', color: c.muted, marginTop: '2px' }}>Codebase intelligence · Python · FastAPI</div>
            </div>
          </div>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '10px', marginBottom: '20px' }}>
            {stats.map((s, i) => (
              <div key={i} style={{ background: 'rgba(170,85,53,0.06)', border: `0.5px solid ${c.border2}`, borderRadius: '5px', padding: '12px 14px' }}>
                <div style={{ fontFamily: "'Playfair Display', serif", fontSize: '22px', fontWeight: '400', color: c.accent2, marginBottom: '2px' }}>{s.num}</div>
                <div style={{ fontSize: '11px', color: c.dim }}>{s.label}</div>
              </div>
            ))}
          </div>
          <div style={{ fontSize: '13px', lineHeight: '1.65', color: c.muted, fontWeight: '300' }}>
            A semantic code search and context file generation platform. Understands codebases the way a senior engineer would. First contribution territory is wide open.
          </div>
        </div>
      </div>
    </div>
  )
}

function Footer() {
  return (
    <div style={{ padding: '56px', display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderTop: `0.5px solid ${c.border2}` }}>
      <div>
        <div style={{ fontFamily: "'Playfair Display', serif", fontSize: '18px', color: c.text, marginBottom: '6px' }}>OpenLens</div>
        <div style={{ fontSize: '12px', color: c.dim, fontWeight: '300' }}>Open source contribution intelligence. Built for developers who want their first PR merged.</div>
      </div>
      <div style={{ display: 'flex', gap: '28px' }}>
        {['GitHub', 'Docs', 'Browse Guides'].map(link => (
          <a key={link} href="#" style={{ fontSize: '12px', color: c.muted }}>{link}</a>
        ))}
      </div>
    </div>
  )
}

export default function LandingPage() {
  return (
    <div style={{ background: c.bg, color: c.text, minHeight: '100vh' }}>
      <Navbar />
      <Ticker />
      <Hero />
      <Story />
      <HowItWorks />
      <WhatYouGet />
      <DemoRepo />
      <Footer />
    </div>
  )
}
