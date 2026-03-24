import { useEffect, useRef } from 'react'
import Navbar from '../components/layout/Navbar'
import Ticker from '../components/ui/Ticker'
import BriefCard from '../components/ui/BriefCard'
import RepoGraph from '../components/ui/RepoGraph'

const STYLES = `
  @import url('https://fonts.googleapis.com/css2?family=Playfair+Display:ital,wght@0,400;0,500;1,400&family=JetBrains+Mono:wght@400;500&family=DM+Sans:wght@300;400;500&display=swap');

  *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

  .ol-page { background: #0C0604; color: #ECD9B8; font-family: 'DM Sans', sans-serif; }

  @keyframes heroIn {
    from { opacity: 0; transform: translateY(40px); }
    to   { opacity: 1; transform: translateY(0); }
  }
  @keyframes cardReveal {
    from { opacity: 0; transform: translateY(60px); }
    to   { opacity: 1; transform: translateY(0); }
  }
  @keyframes ctaPulse {
    0%, 100% { box-shadow: 0 0 0 0 rgba(170,85,53,0.5); }
    60%       { box-shadow: 0 0 0 12px rgba(170,85,53,0); }
  }
  .hero-eyebrow  { opacity:0; animation: heroIn .7s cubic-bezier(.22,1,.36,1) .1s forwards; }
  .hero-headline { opacity:0; animation: heroIn .9s cubic-bezier(.22,1,.36,1) .2s forwards; }
  .hero-sub      { opacity:0; animation: heroIn .7s cubic-bezier(.22,1,.36,1) .4s forwards; }
  .hero-cta      { opacity:0; animation: heroIn .7s cubic-bezier(.22,1,.36,1) .55s forwards; }
  .card-reveal   { opacity:0; animation: cardReveal 1s cubic-bezier(.22,1,.36,1) .7s forwards; }

  .cta-btn {
    animation: ctaPulse 3s ease-in-out infinite;
    transition: background .2s, transform .15s;
  }
  .cta-btn:hover { background: #C4714D !important; transform: translateY(-2px); }

  .reveal {
    opacity: 0;
    transform: translateY(32px);
    transition: opacity .7s cubic-bezier(.22,1,.36,1), transform .7s cubic-bezier(.22,1,.36,1);
  }
  .reveal.show { opacity: 1; transform: translateY(0); }
  .reveal.d1 { transition-delay: .1s; }
  .reveal.d2 { transition-delay: .2s; }
  .reveal.d3 { transition-delay: .3s; }

  .feat-card {
    transition: border-color .2s, transform .2s;
    cursor: default;
  }
  .feat-card:hover {
    border-color: rgba(170,85,53,.5) !important;
    transform: translateY(-4px);
  }
  .step-num {
    font-family: 'Playfair Display', serif;
    font-size: 96px;
    font-weight: 400;
    color: rgba(170,85,53,.06);
    line-height: 1;
    position: absolute;
    bottom: 24px;
    right: 24px;
    user-select: none;
  }
`

function useReveal(threshold = 0.12) {
  const ref = useRef(null)
  useEffect(() => {
    const el = ref.current
    if (!el) return
    const items = el.querySelectorAll('.reveal')
    const obs = new IntersectionObserver(
      entries => entries.forEach(e => { if (e.isIntersecting) { e.target.classList.add('show'); obs.unobserve(e.target) } }),
      { threshold }
    )
    items.forEach(i => obs.observe(i))
    return () => obs.disconnect()
  }, [])
  return ref
}

// ─── HERO ────────────────────────────────────────────────────────────────────
function Hero() {
  return (
    <section style={{ minHeight: '100vh', display: 'flex', flexDirection: 'column', justifyContent: 'center', alignItems: 'center', textAlign: 'center', padding: '120px 40px 80px', position: 'relative', overflow: 'hidden' }}>

      {/* ambient glow */}
      <div style={{ position: 'absolute', top: '30%', left: '50%', transform: 'translate(-50%,-50%)', width: '600px', height: '600px', background: 'radial-gradient(circle, rgba(170,85,53,0.08) 0%, transparent 65%)', pointerEvents: 'none' }} />

      <div className="hero-eyebrow" style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '10px', letterSpacing: '3px', color: '#AA5535', textTransform: 'uppercase', marginBottom: '32px' }}>
        Open Source Intelligence
      </div>

      <h1 className="hero-headline" style={{ fontFamily: "'Playfair Display', serif", fontSize: 'clamp(48px, 7vw, 88px)', lineHeight: '1.06', fontWeight: '400', letterSpacing: '-1px', maxWidth: '900px', marginBottom: '32px' }}>
        You've been told to contribute.
        <br />
        <em style={{ fontStyle: 'italic', color: '#C4714D' }}>Nobody told you how.</em>
      </h1>

      <p className="hero-sub" style={{ fontSize: '17px', lineHeight: '1.75', color: 'rgba(236,217,184,0.55)', fontWeight: '300', maxWidth: '520px', marginBottom: '48px' }}>
        Paste a GitHub repo. Tell us your level. Get a personalized contribution guide — matched issues, files to touch, maintainer patterns, and everything you need to get your first PR merged.
      </p>

      <div className="hero-cta" style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '12px' }}>
        <button className="cta-btn" style={{ fontFamily: "'DM Sans', sans-serif", fontSize: '15px', fontWeight: '500', color: '#ECD9B8', background: '#AA5535', border: 'none', padding: '14px 36px', borderRadius: '4px', cursor: 'pointer' }}>
          Browse Demo Guides
        </button>
        <span style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '11px', color: 'rgba(236,217,184,0.28)', letterSpacing: '0.5px' }}>
          No sign-in required · Just a repo URL and a skill level
        </span>
      </div>

      {/* scroll hint */}
      <div style={{ position: 'absolute', bottom: '40px', left: '50%', transform: 'translateX(-50%)', display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '8px' }}>
        <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '9px', letterSpacing: '2px', color: 'rgba(236,217,184,0.2)', textTransform: 'uppercase' }}>scroll</div>
        <div style={{ width: '1px', height: '40px', background: 'linear-gradient(to bottom, rgba(170,85,53,0.4), transparent)' }} />
      </div>
    </section>
  )
}

// ─── PRODUCT REVEAL ──────────────────────────────────────────────────────────
function ProductReveal() {
  const ref = useReveal(0.08)
  return (
    <section ref={ref} style={{ padding: '0 40px 120px', display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
      <div className="reveal" style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '10px', letterSpacing: '3px', color: '#AA5535', textTransform: 'uppercase', marginBottom: '48px' }}>
        What you get
      </div>
      {/* brief card centered as product screenshot */}
      <div className="card-reveal" style={{ width: '100%', maxWidth: '500px', position: 'relative' }}>
        {/* glow behind card */}
        <div style={{ position: 'absolute', inset: '-40px', background: 'radial-gradient(ellipse, rgba(170,85,53,0.12) 0%, transparent 70%)', pointerEvents: 'none', zIndex: 0 }} />
        <div style={{ position: 'relative', zIndex: 1 }}>
          <BriefCard />
        </div>
      </div>
    </section>
  )
}

// ─── GRAPH SECTION ───────────────────────────────────────────────────────────
function GraphSection() {
  const ref = useReveal(0.06)
  return (
    <section ref={ref} style={{ borderTop: '0.5px solid rgba(170,85,53,0.15)', borderBottom: '0.5px solid rgba(170,85,53,0.15)', background: '#0A0503' }}>
      <div className="reveal" style={{ padding: '64px 64px 0', display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end' }}>
        <div>
          <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '10px', letterSpacing: '3px', color: '#AA5535', textTransform: 'uppercase', marginBottom: '12px' }}>Repo intelligence graph</div>
          <p style={{ fontSize: '14px', color: 'rgba(236,217,184,0.4)', fontWeight: '300', maxWidth: '360px', lineHeight: '1.65' }}>OpenLens maps how issues, files, PRs, and maintainer patterns connect — before generating your guide.</p>
        </div>
        <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '11px', color: 'rgba(170,85,53,0.35)', letterSpacing: '1px' }}>opencodeintel/opencodeintel</div>
      </div>
      <div className="reveal d1" style={{ height: '420px', position: 'relative', margin: '32px 0 0' }}>
        <RepoGraph />
      </div>
    </section>
  )
}

// ─── STORY ───────────────────────────────────────────────────────────────────
function Story() {
  const ref = useReveal()
  const stats = [
    { num: '84%', label: 'of developers who want to contribute never make their first PR' },
    { num: '~3hrs', label: 'average time reading a codebase before giving up and closing the tab' },
    { num: '0', label: 'tools that tell you which files to touch and what this maintainer merges' },
  ]
  return (
    <section ref={ref} style={{ background: '#F2E8D5', color: '#2C1A0E', padding: '112px 64px' }}>
      <div className="reveal" style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '10px', letterSpacing: '3px', color: '#8B3E22', textTransform: 'uppercase', marginBottom: '20px' }}>The problem</div>
      <h2 className="reveal d1" style={{ fontFamily: "'Playfair Display', serif", fontSize: 'clamp(32px, 4vw, 52px)', fontWeight: '400', lineHeight: '1.12', letterSpacing: '-0.5px', maxWidth: '700px', marginBottom: '64px' }}>
        Most developers never make their first PR.{' '}
        <em style={{ fontStyle: 'italic', color: '#8B3E22' }}>Here's why.</em>
      </h2>
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '80px', alignItems: 'start' }}>
        <div className="reveal d1" style={{ display: 'flex', flexDirection: 'column', gap: '20px', fontSize: '15px', lineHeight: '1.85', color: 'rgba(44,26,14,0.65)', fontWeight: '300' }}>
          <p>You find a repo you care about. You clone it. You scroll through issues for twenty minutes. They all feel either too trivial or too complex. You close the tab.</p>
          <p>This isn't a motivation problem. It's an <strong style={{ color: '#2C1A0E', fontWeight: '500' }}>information problem.</strong> The repo doesn't tell you which issue fits your level, which files to change, or what kind of PR this maintainer actually merges.</p>
          <p>goodfirstissue.dev gives you a list. GitHub gives you labels. Neither gives you a <strong style={{ color: '#2C1A0E', fontWeight: '500' }}>guide built for you.</strong> That's what OpenLens does.</p>
        </div>
        <div style={{ borderTop: '1.5px solid rgba(139,62,34,0.2)' }}>
          {stats.map((s, i) => (
            <div key={i} className={`reveal d${i + 1}`} style={{ padding: '28px 0', borderBottom: '0.5px solid rgba(139,62,34,0.15)' }}>
              <div style={{ fontFamily: "'Playfair Display', serif", fontSize: '48px', fontWeight: '400', color: '#8B3E22', lineHeight: '1', marginBottom: '8px' }}>{s.num}</div>
              <div style={{ fontSize: '13px', color: 'rgba(44,26,14,0.55)', fontWeight: '300', lineHeight: '1.5' }}>{s.label}</div>
            </div>
          ))}
        </div>
      </div>
    </section>
  )
}

// ─── HOW IT WORKS ────────────────────────────────────────────────────────────
function HowItWorks() {
  const ref = useReveal()
  const steps = [
    { num: '01', title: 'Paste a repo URL', body: 'Any public GitHub repo. OpenLens fetches issues, merged PRs, contributor patterns, and file structure in the background.' },
    { num: '02', title: 'Answer 5 quick questions', body: 'Questions generated from what this specific repo actually needs — not a generic dropdown. Your level matched to this codebase.' },
    { num: '03', title: 'Get your contribution guide', body: 'A full guide: matched issues, exact files to touch, how this maintainer thinks, similar merged PRs, and a step-by-step path to your first PR.' },
  ]
  return (
    <section ref={ref} style={{ padding: '112px 64px', background: '#0C0604' }}>
      <div className="reveal" style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '10px', letterSpacing: '3px', color: '#AA5535', textTransform: 'uppercase', marginBottom: '20px' }}>How it works</div>
      <h2 className="reveal d1" style={{ fontFamily: "'Playfair Display', serif", fontSize: 'clamp(28px, 3.5vw, 44px)', fontWeight: '400', lineHeight: '1.15', letterSpacing: '-0.3px', marginBottom: '64px', maxWidth: '560px' }}>
        Three steps from <em style={{ fontStyle: 'italic', color: '#C4714D' }}>cold repo</em> to merged PR
      </h2>
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '1px', background: 'rgba(170,85,53,0.15)' }}>
        {steps.map((step, i) => (
          <div key={i} className={`feat-card reveal d${i + 1}`} style={{ background: '#110805', padding: '48px 40px 56px', border: '0.5px solid rgba(170,85,53,0.15)', position: 'relative', overflow: 'hidden' }}>
            <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '10px', color: '#AA5535', letterSpacing: '2px', marginBottom: '24px', opacity: 0.6 }}>{step.num}</div>
            <div style={{ fontFamily: "'Playfair Display', serif", fontSize: '24px', fontWeight: '400', lineHeight: '1.3', color: '#ECD9B8', marginBottom: '14px' }}>{step.title}</div>
            <div style={{ fontSize: '14px', lineHeight: '1.75', color: 'rgba(236,217,184,0.5)', fontWeight: '300' }}>{step.body}</div>
            <div className="step-num">{step.num}</div>
          </div>
        ))}
      </div>
    </section>
  )
}

// ─── FEATURES ────────────────────────────────────────────────────────────────
function Features() {
  const ref = useReveal()
  const cards = [
    { num: '01', label: 'Issues', title: 'Issues matched to your level', body: "Not a dump of every label. OpenLens reads actual code complexity and surfaces only the issues that fit where you are right now." },
    { num: '02', label: 'Files', title: 'Exactly which files to touch', body: "No more reading the entire codebase blind. OpenLens maps the issue to the specific files and tests you'd need to change." },
    { num: '03', label: 'Maintainer', title: 'How this maintainer actually thinks', body: "Every maintainer has a style. OpenLens reads merged PR history so your PR lands the way they expect it — and gets merged, not ignored." },
    { num: '04', label: 'Guide', title: 'A doc you follow start to finish', body: "A complete contribution guide built for this repo, this issue, and your level. Walk in, make the PR, walk out." },
  ]
  return (
    <section ref={ref} style={{ padding: '112px 64px', background: '#EDE3CE', color: '#2C1A0E' }}>
      <div className="reveal" style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '10px', letterSpacing: '3px', color: '#8B3E22', textTransform: 'uppercase', marginBottom: '20px' }}>What's inside your guide</div>
      <h2 className="reveal d1" style={{ fontFamily: "'Playfair Display', serif", fontSize: 'clamp(28px, 3.5vw, 44px)', fontWeight: '400', lineHeight: '1.15', letterSpacing: '-0.3px', marginBottom: '64px', maxWidth: '560px', color: '#2C1A0E' }}>
        Everything the repo <em style={{ fontStyle: 'italic', color: '#8B3E22' }}>won't tell you itself</em>
      </h2>
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '2px', background: 'rgba(139,62,34,0.18)' }}>
        {cards.map((card, i) => (
          <div key={i} className={`feat-card reveal d${(i % 2) + 1}`} style={{ background: '#F2E8D5', padding: '48px 40px', border: '0.5px solid rgba(139,62,34,0.15)', cursor: 'default' }}>
            <div style={{ display: 'flex', gap: '10px', alignItems: 'center', marginBottom: '20px' }}>
              <span style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '9px', letterSpacing: '2px', color: 'rgba(139,62,34,0.5)', textTransform: 'uppercase' }}>{card.num}</span>
              <span style={{ width: '1px', height: '10px', background: 'rgba(139,62,34,0.2)', flexShrink: 0 }} />
              <span style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '9px', letterSpacing: '2px', color: '#8B3E22', textTransform: 'uppercase' }}>{card.label}</span>
            </div>
            <div style={{ fontFamily: "'Playfair Display', serif", fontSize: '22px', fontWeight: '400', lineHeight: '1.3', color: '#2C1A0E', marginBottom: '12px' }}>{card.title}</div>
            <div style={{ fontSize: '14px', lineHeight: '1.75', color: 'rgba(44,26,14,0.6)', fontWeight: '300' }}>{card.body}</div>
          </div>
        ))}
      </div>
    </section>
  )
}

// ─── DEMO CTA ─────────────────────────────────────────────────────────────────
function DemoCTA() {
  const ref = useReveal()
  const stats = [
    { num: '50', label: 'open issues' },
    { num: '0', label: 'merged PRs — wide open' },
    { num: '3', label: 'skill levels covered' },
  ]
  return (
    <section ref={ref} style={{ padding: '112px 64px', background: '#0C0604', borderTop: '0.5px solid rgba(170,85,53,0.15)' }}>
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '96px', alignItems: 'center' }}>
        <div>
          <div className="reveal" style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '10px', letterSpacing: '3px', color: '#AA5535', textTransform: 'uppercase', marginBottom: '20px' }}>Try it now</div>
          <h2 className="reveal d1" style={{ fontFamily: "'Playfair Display', serif", fontSize: 'clamp(28px, 3.5vw, 44px)', fontWeight: '400', lineHeight: '1.15', letterSpacing: '-0.3px', marginBottom: '20px' }}>
            Start with a real repo.<br />
            <em style={{ fontStyle: 'italic', color: '#C4714D' }}>See what we build for you.</em>
          </h2>
          <p className="reveal d2" style={{ fontSize: '15px', lineHeight: '1.8', color: 'rgba(236,217,184,0.5)', fontWeight: '300', maxWidth: '380px', marginBottom: '40px' }}>
            opencodeintel is a Python codebase intelligence tool — 50 open issues, active maintainer, clean PR history. The perfect first repo to try OpenLens on.
          </p>
          <div className="reveal d2" style={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-start', gap: '10px' }}>
            <button className="cta-btn" style={{ fontFamily: "'DM Sans', sans-serif", fontSize: '14px', fontWeight: '500', color: '#ECD9B8', background: '#AA5535', border: 'none', padding: '13px 32px', borderRadius: '4px', cursor: 'pointer' }}>
              Generate my contribution guide
            </button>
            <span style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '11px', color: 'rgba(236,217,184,0.25)' }}>opencodeintel/opencodeintel · no sign-in required</span>
          </div>
        </div>
        <div className="reveal d2" style={{ background: '#130907', border: '0.5px solid rgba(170,85,53,0.3)', borderRadius: '10px', padding: '32px' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '12px', marginBottom: '24px', paddingBottom: '20px', borderBottom: '0.5px solid rgba(170,85,53,0.12)' }}>
            <div style={{ width: '36px', height: '36px', background: 'rgba(170,85,53,0.1)', borderRadius: '6px', display: 'flex', alignItems: 'center', justifyContent: 'center', border: '0.5px solid rgba(170,85,53,0.2)', flexShrink: 0 }}>
              <div style={{ width: '13px', height: '13px', border: '1.5px solid #C4714D', borderRadius: '3px' }} />
            </div>
            <div>
              <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '12px', color: '#ECD9B8' }}>opencodeintel/opencodeintel</div>
              <div style={{ fontSize: '12px', color: 'rgba(236,217,184,0.4)', marginTop: '3px' }}>Python · FastAPI · Codebase intelligence</div>
            </div>
          </div>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '10px', marginBottom: '24px' }}>
            {stats.map((s, i) => (
              <div key={i} style={{ background: 'rgba(170,85,53,0.05)', border: '0.5px solid rgba(170,85,53,0.12)', borderRadius: '5px', padding: '14px' }}>
                <div style={{ fontFamily: "'Playfair Display', serif", fontSize: '22px', fontWeight: '400', color: '#C4714D', marginBottom: '4px' }}>{s.num}</div>
                <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '9px', color: 'rgba(236,217,184,0.3)', lineHeight: '1.4' }}>{s.label}</div>
              </div>
            ))}
          </div>
          <div style={{ fontSize: '13px', lineHeight: '1.7', color: 'rgba(236,217,184,0.4)', fontWeight: '300' }}>
            Semantic code search and context file generation. Understands codebases the way a senior engineer would. First contribution territory is wide open.
          </div>
        </div>
      </div>
    </section>
  )
}

// ─── FOOTER ──────────────────────────────────────────────────────────────────
function Footer() {
  return (
    <footer style={{ padding: '48px 64px', display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderTop: '0.5px solid rgba(170,85,53,0.15)', background: '#0A0503' }}>
      <div>
        <div style={{ fontFamily: "'Playfair Display', serif", fontSize: '18px', color: '#ECD9B8', marginBottom: '4px' }}>OpenLens</div>
        <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: '10px', color: 'rgba(236,217,184,0.25)', letterSpacing: '0.3px' }}>Open source contribution intelligence</div>
      </div>
      <div style={{ display: 'flex', gap: '32px' }}>
        {['GitHub', 'Docs', 'Browse Guides'].map(l => (
          <a key={l} href="#" style={{ fontFamily: "'DM Sans', sans-serif", fontSize: '13px', color: 'rgba(236,217,184,0.4)', textDecoration: 'none' }}>{l}</a>
        ))}
      </div>
    </footer>
  )
}

// ─── PAGE ─────────────────────────────────────────────────────────────────────
export default function LandingPage() {
  return (
    <div className="ol-page">
      <style>{STYLES}</style>
      <Navbar />
      <Ticker />
      <Hero />
      <ProductReveal />
      <GraphSection />
      <Story />
      <HowItWorks />
      <Features />
      <DemoCTA />
      <Footer />
    </div>
  )
}
