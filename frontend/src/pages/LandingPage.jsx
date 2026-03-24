import { useEffect, useRef, useState } from 'react'
import Navbar from '../components/layout/Navbar'
import Ticker from '../components/ui/Ticker'
import BriefCard from '../components/ui/BriefCard'

const c = {
  bg: '#100806', bg2: '#0E0604', bg3: '#1C1109',
  bgLight: '#F5ECD8', bgLight2: '#EDE0C4',
  text: '#ECD9B8', muted: 'rgba(236,217,184,0.5)', dim: 'rgba(236,217,184,0.28)',
  textDark: '#2C1A0E', mutedDark: 'rgba(44,26,14,0.6)', dimDark: 'rgba(44,26,14,0.35)',
  accent: '#AA5535', accent2: '#C4714D', accentDark: '#8B3E22',
  border: 'rgba(170,85,53,0.25)', border2: 'rgba(170,85,53,0.15)',
  borderDark: 'rgba(139,62,34,0.2)',
  green: '#7CB67A', greenBg: 'rgba(124,182,122,0.08)', greenBorder: 'rgba(124,182,122,0.2)',
  card: '#160C08',
}

const globalStyles = `
  @keyframes fadeUp {
    from { opacity: 0; transform: translateY(32px); }
    to   { opacity: 1; transform: translateY(0); }
  }
  @keyframes pulse-cta {
    0%, 100% { box-shadow: 0 0 0 0 rgba(170,85,53,0.4); }
    50%       { box-shadow: 0 0 0 8px rgba(170,85,53,0); }
  }
  @keyframes countUp {
    from { opacity: 0; transform: translateY(12px); }
    to   { opacity: 1; transform: translateY(0); }
  }
  .reveal { opacity: 0; }
  .reveal.visible { animation: fadeUp 0.7s cubic-bezier(0.22,1,0.36,1) forwards; }
  .reveal-delay-1.visible { animation-delay: 0.1s; }
  .reveal-delay-2.visible { animation-delay: 0.2s; }
  .reveal-delay-3.visible { animation-delay: 0.3s; }
  .reveal-delay-4.visible { animation-delay: 0.4s; }
  .step-card { transition: border-color 0.2s, transform 0.2s; }
  .step-card:hover { border-color: rgba(170,85,53,0.5) !important; transform: translateY(-3px); }
  .get-card { transition: border-color 0.2s, background 0.2s, transform 0.2s; }
  .get-card:hover { border-color: rgba(170,85,53,0.4) !important; background: rgba(28,17,9,0.9) !important; transform: translateY(-2px); }
  .cta-primary { animation: pulse-cta 2.5s ease-in-out infinite; transition: background 0.2s, transform 0.15s; }
  .cta-primary:hover { background: #C4714D !important; transform: translateY(-1px); }
  .stat-num { animation: countUp 0.6s cubic-bezier(0.22,1,0.36,1) both; }
`

function useReveal() {
  const ref = useRef(null)
  useEffect(() => {
    const el = ref.current
    if (!el) return
    const obs = new IntersectionObserver(
      ([entry]) => { if (entry.isIntersecting) { el.classList.add('visible'); obs.disconnect() } },
      { threshold: 0.12 }
    )
    obs.observe(el)
    return () => obs.disconnect()
  }, [])
  return ref
}

function Hero() {
  return (
    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', minHeight: '860px', alignItems: 'center', borderBottom: `0.5px solid ${c.border2}` }}>
      <div style={{ padding: '72px 48px 72px 64px', display: 'flex', flexDirection: 'column', justifyContent: 'center', borderRight: `0.5px solid ${c.border2}`, minHeight: '860px' }}>
        <div className="reveal" style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '10px', letterSpacing: '3px', color: c.accent, textTransform: 'uppercase', marginBottom: '24px', animationDelay: '0.05s' }}>
          Open Source Intelligence
        </div>
        <h1 className="reveal" style={{ fontFamily: "'Playfair Display', serif", fontSize: '54px', lineHeight: '1.08', fontWeight: '400', letterSpacing: '-0.5px', marginBottom: '24px', animationDelay: '0.12s' }}>
          You've been told<br />to contribute.<br />
          <em style={{ fontStyle: 'italic', color: c.accent2 }}>Nobody told<br />you how.</em>
        </h1>
        <p className="reveal" style={{ fontSize: '15px', lineHeight: '1.8', color: c.muted, fontWeight: '300', marginBottom: '40px', maxWidth: '380px', animationDelay: '0.2s' }}>
          Paste a GitHub repo. Tell us your level. Get a contribution guide that actually makes sense — matched issues, files to touch, maintainer patterns, and everything you need to get your first PR merged.
        </p>
        <div className="reveal" style={{ animationDelay: '0.28s' }}>
          <button className="cta-primary" style={{ fontSize: '14px', fontWeight: '500', color: c.text, background: c.accent, border: 'none', padding: '13px 32px', borderRadius: '4px', cursor: 'pointer', width: 'fit-content', marginBottom: '12px', display: 'block' }}>
            Browse Demo Guides
          </button>
          <span style={{ fontSize: '12px', color: c.dim }}>No sign-in required. Just a repo URL and a skill level.</span>
        </div>
      </div>
      <div style={{ padding: '48px', display: 'flex', alignItems: 'center', justifyContent: 'center', minHeight: '860px', background: 'rgba(22,12,8,0.6)', position: 'relative', overflow: 'hidden' }}>
        <div style={{ position: 'absolute', top: '50%', left: '50%', transform: 'translate(-50%,-50%)', width: '400px', height: '400px', background: 'radial-gradient(circle, rgba(170,85,53,0.06) 0%, transparent 70%)', pointerEvents: 'none' }} />
        <div className="reveal" style={{ width: '100%', animationDelay: '0.3s' }}>
          <BriefCard />
        </div>
      </div>
    </div>
  )
}

function Story() {
  const ref = useReveal()
  const stats = [
    { num: '84%', label: 'of developers who want to contribute never make their first PR' },
    { num: '~3hrs', label: 'average time spent reading a codebase before giving up' },
    { num: '0', label: 'tools that tell you which files to touch and what gets merged' },
  ]
  return (
    <div style={{ background: c.bgLight, color: c.textDark }}>
      <div ref={ref} className="reveal" style={{ padding: '96px 64px', maxWidth: '1200px', margin: '0 auto' }}>
        <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '10px', letterSpacing: '3px', color: c.accentDark, textTransform: 'uppercase', marginBottom: '18px' }}>The problem</div>
        <h2 style={{ fontFamily: "'Playfair Display', serif", fontSize: '44px', fontWeight: '400', lineHeight: '1.15', letterSpacing: '-0.3px', maxWidth: '600px', color: c.textDark, marginBottom: '56px' }}>
          Most developers never make<br />their first PR.{' '}
          <em style={{ fontStyle: 'italic', color: c.accentDark }}>Here's why.</em>
        </h2>
        <div style={{ display: 'grid', gridTemplateColumns: '1.1fr 0.9fr', gap: '80px', alignItems: 'start' }}>
          <div style={{ fontSize: '15px', lineHeight: '1.9', color: c.mutedDark, fontWeight: '300', display: 'flex', flexDirection: 'column', gap: '18px' }}>
            <p>You find a repo you care about. You clone it. You read the README. You scroll through open issues for twenty minutes. They all feel either too small to matter or too large to touch. You close the tab.</p>
            <p>This isn't a motivation problem. It's an <strong style={{ color: c.textDark, fontWeight: '500' }}>information problem.</strong> The repo doesn't tell you which issue fits your level, which files you'd need to change, or what kind of PR this maintainer actually merges.</p>
            <p>goodfirstissue.dev gives you a list. GitHub gives you labels. Neither gives you a <strong style={{ color: c.textDark, fontWeight: '500' }}>contribution guide built for you</strong> — matched to your skill level, shaped around how this specific repo works.</p>
            <p>That's what OpenLens does.</p>
          </div>
          <div style={{ borderTop: `1.5px solid ${c.borderDark}` }}>
            {stats.map((s, i) => (
              <div key={i} className={`reveal reveal-delay-${i + 1}`} style={{ padding: '24px 0', borderBottom: `0.5px solid ${c.borderDark}` }}>
                <div className="stat-num" style={{ fontFamily: "'Playfair Display', serif", fontSize: '42px', fontWeight: '400', color: c.accentDark, lineHeight: '1', marginBottom: '6px', animationDelay: `${0.2 + i * 0.1}s` }}>{s.num}</div>
                <div style={{ fontSize: '13px', color: c.mutedDark, fontWeight: '300', lineHeight: '1.5' }}>{s.label}</div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  )
}

function HowItWorks() {
  const ref = useReveal()
  const steps = [
    { num: '01', title: ['Paste a', 'repo URL'], body: 'Any public GitHub repository. OpenLens pulls the issue list, commit history, merged PRs, and contributor patterns in the background.' },
    { num: '02', title: ['Answer 5', 'quick questions'], body: 'Repo-specific questions generated from what the codebase actually needs — not a generic skill dropdown.' },
    { num: '03', title: ['Get your', 'contribution guide'], body: 'Matched issues, files to touch, maintainer patterns, similar merged PRs, and a step-by-step path to your first merged PR.' },
  ]
  return (
    <div style={{ background: c.bg, padding: '96px 64px' }}>
      <div ref={ref} className="reveal">
        <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '10px', letterSpacing: '3px', color: c.accent, textTransform: 'uppercase', marginBottom: '18px' }}>How it works</div>
        <h2 style={{ fontFamily: "'Playfair Display', serif", fontSize: '38px', fontWeight: '400', lineHeight: '1.2', letterSpacing: '-0.3px', marginBottom: '48px', maxWidth: '520px' }}>
          Three steps from <em style={{ fontStyle: 'italic', color: c.accent2 }}>cold repo</em> to merged PR
        </h2>
      </div>
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '1px', background: c.border2 }}>
        {steps.map((step, i) => (
          <div key={i} className={`step-card reveal reveal-delay-${i + 1}`} style={{ background: c.bg3, padding: '40px 36px', border: `0.5px solid ${c.border2}`, position: 'relative' }}>
            <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '11px', color: c.accent, marginBottom: '28px', letterSpacing: '1px', opacity: 0.7 }}>{step.num}</div>
            <div style={{ fontFamily: "'Playfair Display', serif", fontSize: '24px', fontWeight: '400', lineHeight: '1.3', marginBottom: '14px' }}>
              {step.title[0]}<br /><em style={{ fontStyle: 'italic', color: c.accent2 }}>{step.title[1]}</em>
            </div>
            <div style={{ fontSize: '13.5px', lineHeight: '1.75', color: c.muted, fontWeight: '300' }}>{step.body}</div>
            <div style={{ position: 'absolute', bottom: '36px', right: '36px', fontFamily: "'JetBrains Mono', monospace", fontSize: '48px', fontWeight: '400', color: 'rgba(170,85,53,0.06)', lineHeight: '1' }}>{step.num}</div>
          </div>
        ))}
      </div>
    </div>
  )
}

function WhatYouGet() {
  const ref = useReveal()
  const cards = [
    { num: '01', label: 'Issues', title: ['Issues matched', 'to your level'], body: "Not a dump of every \"good first issue\" label. OpenLens reads the actual code complexity and surfaces only the issues that fit where you are right now." },
    { num: '02', label: 'Files', title: ['Exactly which', 'files to touch'], body: "Stop reading the entire codebase to figure out where to start. OpenLens maps the issue to the specific files and tests you'd need to change." },
    { num: '03', label: 'Maintainer', title: ['How this maintainer', 'actually thinks'], body: "OpenLens reads merged PR history so your PR lands the way they expect it — and gets merged, not ignored." },
    { num: '04', label: 'Guide', title: ['A doc you follow', 'start to finish'], body: "A complete contribution guide built for this repo, this issue, and your level. Walk in, make the PR, walk out." },
  ]
  return (
    <div style={{ background: c.bgLight2, color: c.textDark, padding: '96px 64px' }}>
      <div ref={ref} className="reveal">
        <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '10px', letterSpacing: '3px', color: c.accentDark, textTransform: 'uppercase', marginBottom: '18px' }}>What's inside your guide</div>
        <h2 style={{ fontFamily: "'Playfair Display', serif", fontSize: '38px', fontWeight: '400', lineHeight: '1.2', letterSpacing: '-0.3px', marginBottom: '48px', maxWidth: '520px', color: c.textDark }}>
          Everything the repo <em style={{ fontStyle: 'italic', color: c.accentDark }}>won't tell you itself</em>
        </h2>
      </div>
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '2px', background: c.borderDark }}>
        {cards.map((card, i) => (
          <div key={i} className={`get-card reveal reveal-delay-${(i % 2) + 1}`} style={{ background: c.bgLight, padding: '40px 36px', border: `0.5px solid ${c.borderDark}`, cursor: 'default' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '20px' }}>
              <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '9px', letterSpacing: '2px', color: c.accentDark, textTransform: 'uppercase', opacity: 0.7 }}>{card.num}</div>
              <div style={{ width: '1px', height: '10px', background: c.borderDark }} />
              <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '9px', letterSpacing: '2px', color: c.accentDark, textTransform: 'uppercase' }}>{card.label}</div>
            </div>
            <div style={{ fontFamily: "'Playfair Display', serif", fontSize: '22px', fontWeight: '400', lineHeight: '1.3', marginBottom: '12px', color: c.textDark }}>
              {card.title[0]}<br /><em style={{ fontStyle: 'italic', color: c.accentDark }}>{card.title[1]}</em>
            </div>
            <div style={{ fontSize: '13.5px', lineHeight: '1.75', color: c.mutedDark, fontWeight: '300' }}>{card.body}</div>
          </div>
        ))}
      </div>
    </div>
  )
}

function DemoRepo() {
  const ref = useReveal()
  const stats = [
    { num: '50', label: 'open issues' },
    { num: '0', label: 'merged PRs' },
    { num: '3', label: 'skill levels' },
  ]
  return (
    <div style={{ background: c.bg, padding: '96px 64px', borderTop: `0.5px solid ${c.border2}` }}>
      <div ref={ref} className="reveal" style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '80px', alignItems: 'center' }}>
        <div>
          <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '10px', letterSpacing: '3px', color: c.accent, textTransform: 'uppercase', marginBottom: '18px' }}>Try it now</div>
          <h2 style={{ fontFamily: "'Playfair Display', serif", fontSize: '38px', fontWeight: '400', lineHeight: '1.2', letterSpacing: '-0.3px', marginBottom: '18px' }}>
            Start with a real repo.<br /><em style={{ fontStyle: 'italic', color: c.accent2 }}>See what we build for you.</em>
          </h2>
          <p style={{ fontSize: '15px', lineHeight: '1.8', color: c.muted, fontWeight: '300', maxWidth: '380px', marginBottom: '36px' }}>
            opencodeintel is a Python codebase intelligence tool with 50 open issues and a clean history. The perfect first repo to try OpenLens on — real issues, active maintainer, mergeable PRs.
          </p>
          <button className="cta-primary" style={{ fontSize: '14px', fontWeight: '500', color: c.text, background: c.accent, border: 'none', padding: '13px 32px', borderRadius: '4px', cursor: 'pointer', marginBottom: '10px', display: 'block' }}>
            Generate my contribution guide
          </button>
          <div style={{ fontSize: '12px', color: c.dim }}>Uses opencodeintel/opencodeintel · No sign-in required</div>
        </div>
        <div className="reveal reveal-delay-2" style={{ background: c.card, border: `0.5px solid rgba(170,85,53,0.35)`, borderRadius: '10px', padding: '28px 32px' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '12px', marginBottom: '22px' }}>
            <div style={{ width: '38px', height: '38px', background: 'rgba(170,85,53,0.12)', borderRadius: '7px', display: 'flex', alignItems: 'center', justifyContent: 'center', border: `0.5px solid ${c.border}`, flexShrink: 0 }}>
              <div style={{ width: '14px', height: '14px', border: `1.5px solid ${c.accent2}`, borderRadius: '3px' }} />
            </div>
            <div>
              <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '13px', color: c.text }}>opencodeintel/opencodeintel</div>
              <div style={{ fontSize: '12px', color: c.muted, marginTop: '3px' }}>Codebase intelligence · Python · FastAPI</div>
            </div>
          </div>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '10px', marginBottom: '22px' }}>
            {stats.map((s, i) => (
              <div key={i} style={{ background: 'rgba(170,85,53,0.06)', border: `0.5px solid ${c.border2}`, borderRadius: '5px', padding: '14px 16px' }}>
                <div style={{ fontFamily: "'Playfair Display', serif", fontSize: '24px', fontWeight: '400', color: c.accent2, marginBottom: '3px' }}>{s.num}</div>
                <div style={{ fontSize: '11px', color: c.dim }}>{s.label}</div>
              </div>
            ))}
          </div>
          <div style={{ fontSize: '13px', lineHeight: '1.7', color: c.muted, fontWeight: '300', borderTop: `0.5px solid ${c.border2}`, paddingTop: '18px' }}>
            A semantic code search and context file generation platform. First contribution territory is wide open.
          </div>
        </div>
      </div>
    </div>
  )
}

function Footer() {
  return (
    <div style={{ background: c.bg2, padding: '48px 64px', display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderTop: `0.5px solid ${c.border2}` }}>
      <div>
        <div style={{ fontFamily: "'Playfair Display', serif", fontSize: '18px', color: c.text, marginBottom: '6px' }}>OpenLens</div>
        <div style={{ fontSize: '12px', color: c.dim, fontWeight: '300' }}>Open source contribution intelligence.</div>
      </div>
      <div style={{ display: 'flex', gap: '28px' }}>
        {['GitHub', 'Docs', 'Browse Guides'].map(link => (
          <a key={link} href="#" style={{ fontSize: '12px', color: c.muted, transition: 'color 0.2s' }}>{link}</a>
        ))}
      </div>
    </div>
  )
}

export default function LandingPage() {
  return (
    <div style={{ background: c.bg, color: c.text, minHeight: '100vh' }}>
      <style>{globalStyles}</style>
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
