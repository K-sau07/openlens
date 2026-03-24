import { useEffect, useRef } from 'react'

export default function RepoGraph() {
  const canvasRef = useRef(null)

  useEffect(() => {
    const canvas = canvasRef.current
    if (!canvas) return
    const ctx = canvas.getContext('2d')
    let animId

    const resize = () => {
      const parent = canvas.parentElement
      if (!parent) return
      const w = parent.offsetWidth
      const h = parent.offsetHeight
      if (!w || !h) return
      canvas.width = w * window.devicePixelRatio
      canvas.height = h * window.devicePixelRatio
      canvas.style.width = w + 'px'
      canvas.style.height = h + 'px'
      ctx.setTransform(window.devicePixelRatio, 0, 0, window.devicePixelRatio, 0, 0)
    }
    setTimeout(resize, 60)
    window.addEventListener('resize', resize)

    const W = () => canvas.offsetWidth
    const H = () => canvas.offsetHeight

    const nodeTypes = [
      { label: 'issue #17', type: 'issue', color: '#AA5535' },
      { label: 'issue #23', type: 'issue', color: '#AA5535' },
      { label: 'issue #31', type: 'issue', color: '#AA5535' },
      { label: 'builder.py', type: 'file', color: '#C4714D' },
      { label: 'routes.py', type: 'file', color: '#C4714D' },
      { label: 'test_builder.py', type: 'file', color: '#C4714D' },
      { label: 'PR #23', type: 'pr', color: '#7CB67A' },
      { label: 'PR #31', type: 'pr', color: '#7CB67A' },
      { label: 'maintainer', type: 'maintainer', color: '#ECD9B8' },
      { label: 'opencodeintel', type: 'repo', color: '#ECD9B8' },
    ]

    // fixed % positions spread across full canvas
    const positions = [
      [22, 25], [55, 18], [78, 30],  // issues
      [18, 68], [48, 72], [72, 65],  // files
      [35, 42], [65, 45],            // PRs
      [82, 18],                      // maintainer
      [50, 48],                      // repo (center)
    ]

    const nodes = nodeTypes.map((nt, i) => ({
      ...nt,
      x: positions[i][0],
      y: positions[i][1],
      vx: (Math.random() - 0.5) * 0.025,
      vy: (Math.random() - 0.5) * 0.025,
      r: nt.type === 'repo' ? 7 : nt.type === 'maintainer' ? 5 : 4,
      pulse: Math.random() * Math.PI * 2,
      active: false,
    }))

    const edges = [
      [9,0],[9,1],[9,2],[9,6],[9,7],[9,8],
      [0,3],[1,4],[2,5],
      [6,3],[6,4],[7,5],
      [8,6],[8,7],
    ]

    const activationOrder = [9,8,0,1,2,6,7,3,4,5]
    let tick = 0

    function draw() {
      const w = W(), h = H()
      if (!w || !h) { animId = requestAnimationFrame(draw); return }
      ctx.clearRect(0, 0, w, h)
      tick++

      activationOrder.forEach((ni, order) => {
        if (tick > order * 20 + 15 && !nodes[ni].active) nodes[ni].active = true
      })

      edges.forEach(([a, b]) => {
        const na = nodes[a], nb = nodes[b]
        if (!na.active || !nb.active) return
        const ax = (na.x/100)*w, ay = (na.y/100)*h
        const bx = (nb.x/100)*w, by = (nb.y/100)*h
        const g = ctx.createLinearGradient(ax,ay,bx,by)
        g.addColorStop(0, na.color+'45')
        g.addColorStop(1, nb.color+'25')
        ctx.beginPath(); ctx.moveTo(ax,ay); ctx.lineTo(bx,by)
        ctx.strokeStyle=g; ctx.lineWidth=0.6; ctx.stroke()
        const p = (tick*0.007)%1
        ctx.beginPath(); ctx.arc(ax+(bx-ax)*p, ay+(by-ay)*p, 1.5, 0, Math.PI*2)
        ctx.fillStyle = na.color+'99'; ctx.fill()
      })

      nodes.forEach(node => {
        if (!node.active) return
        const x=(node.x/100)*w, y=(node.y/100)*h
        node.pulse += 0.022
        const gr = ctx.createRadialGradient(x,y,node.r*.5,x,y,node.r+6+Math.sin(node.pulse)*1.5)
        gr.addColorStop(0, node.color+'35'); gr.addColorStop(1, node.color+'00')
        ctx.beginPath(); ctx.arc(x,y,node.r+8,0,Math.PI*2); ctx.fillStyle=gr; ctx.fill()
        ctx.beginPath(); ctx.arc(x,y,node.r,0,Math.PI*2)
        ctx.fillStyle=node.color+'BB'; ctx.fill()
        ctx.strokeStyle=node.color; ctx.lineWidth=0.8; ctx.stroke()
        ctx.font = `10px 'JetBrains Mono', monospace`
        ctx.fillStyle = node.color; ctx.textAlign='center'
        ctx.shadowColor=node.color; ctx.shadowBlur=5
        ctx.fillText(node.label, x, y+node.r+14)
        ctx.shadowBlur=0
        node.x += node.vx; node.y += node.vy
        if (node.x<8||node.x>92) node.vx*=-1
        if (node.y<8||node.y>92) node.vy*=-1
      })

      animId = requestAnimationFrame(draw)
    }

    draw()
    return () => { cancelAnimationFrame(animId); window.removeEventListener('resize', resize) }
  }, [])

  return <canvas ref={canvasRef} style={{ display:'block', width:'100%', height:'100%' }} />
}
