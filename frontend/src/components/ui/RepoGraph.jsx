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
      if (w === 0 || h === 0) return
      canvas.width = w * window.devicePixelRatio
      canvas.height = h * window.devicePixelRatio
      canvas.style.width = w + 'px'
      canvas.style.height = h + 'px'
      ctx.setTransform(window.devicePixelRatio, 0, 0, window.devicePixelRatio, 0, 0)
    }
    setTimeout(resize, 80)
    window.addEventListener('resize', resize)

    const W = () => canvas.offsetWidth
    const H = () => canvas.offsetHeight

    // node types with labels
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

    // place nodes spread across the full canvas
    const nodes = nodeTypes.map((nt, i) => {
      const angle = (i / nodeTypes.length) * Math.PI * 2
      const radius = i === 9 ? 0 : 28 + (i % 3) * 14 + Math.random() * 12
      return {
        ...nt,
        x: i === 9 ? 50 : 15 + ((i * 73 + 17) % 70),
        y: i === 9 ? 50 : 12 + ((i * 59 + 31) % 76),
        vx: (Math.random() - 0.5) * 0.03,
        vy: (Math.random() - 0.5) * 0.03,
        r: nt.type === 'repo' ? 7 : nt.type === 'maintainer' ? 5 : 4,
        pulse: Math.random() * Math.PI * 2,
        active: false,
        activatedAt: 0,
      }
    })

    // edges connecting nodes
    const edges = [
      [9, 0], [9, 1], [9, 2], // repo → issues
      [9, 6], [9, 7],          // repo → PRs
      [9, 8],                  // repo → maintainer
      [0, 3], [1, 4], [2, 5], // issues → files
      [6, 3], [6, 4],          // PR → files
      [7, 5],                  // PR → file
      [8, 6], [8, 7],          // maintainer → PRs
    ]

    let tick = 0
    // stagger node activation over time
    const activationOrder = [9, 8, 0, 1, 2, 6, 7, 3, 4, 5]

    function draw() {
      const w = W(), h = H()
      ctx.clearRect(0, 0, w, h)
      tick++

      // activate nodes one by one
      activationOrder.forEach((ni, order) => {
        if (tick > order * 18 + 10 && !nodes[ni].active) {
          nodes[ni].active = true
          nodes[ni].activatedAt = tick
        }
      })

      // draw edges
      edges.forEach(([a, b]) => {
        const na = nodes[a], nb = nodes[b]
        if (!na.active || !nb.active) return
        const ax = (na.x / 100) * w, ay = (na.y / 100) * h
        const bx = (nb.x / 100) * w, by = (nb.y / 100) * h
        const grad = ctx.createLinearGradient(ax, ay, bx, by)
        grad.addColorStop(0, na.color + '50')
        grad.addColorStop(1, nb.color + '30')
        ctx.beginPath()
        ctx.moveTo(ax, ay)
        ctx.lineTo(bx, by)
        ctx.strokeStyle = grad
        ctx.lineWidth = 0.5
        ctx.stroke()

        // animated pulse travelling along edge
        const progress = ((tick * 0.008) % 1)
        const px = ax + (bx - ax) * progress
        const py = ay + (by - ay) * progress
        ctx.beginPath()
        ctx.arc(px, py, 1.5, 0, Math.PI * 2)
        ctx.fillStyle = na.color + 'BB'
        ctx.fill()
      })

      // draw nodes
      nodes.forEach(node => {
        if (!node.active) return
        const x = (node.x / 100) * w
        const y = (node.y / 100) * h
        node.pulse += 0.025

        // outer glow ring
        const glowR = node.r + 3 + Math.sin(node.pulse) * 1.5
        const glow = ctx.createRadialGradient(x, y, node.r * 0.5, x, y, glowR + 4)
        glow.addColorStop(0, node.color + '40')
        glow.addColorStop(1, node.color + '00')
        ctx.beginPath()
        ctx.arc(x, y, glowR + 4, 0, Math.PI * 2)
        ctx.fillStyle = glow
        ctx.fill()

        // node circle
        ctx.beginPath()
        ctx.arc(x, y, node.r, 0, Math.PI * 2)
        ctx.fillStyle = node.color + 'CC'
        ctx.fill()
        ctx.strokeStyle = node.color
        ctx.lineWidth = 0.8
        ctx.stroke()

        // label
        ctx.font = `${node.type === 'repo' ? '600 ' : ''}10px 'JetBrains Mono', monospace`
        ctx.fillStyle = node.color
        ctx.textAlign = 'center'
        ctx.shadowColor = node.color
        ctx.shadowBlur = 6
        ctx.fillText(node.label, x, y + node.r + 14)
        ctx.shadowBlur = 0
      })

      // gentle float
      nodes.forEach(node => {
        node.x += node.vx
        node.y += node.vy
        if (node.x < 10 || node.x > 90) node.vx *= -1
        if (node.y < 10 || node.y > 90) node.vy *= -1
      })

      animId = requestAnimationFrame(draw)
    }

    draw()
    return () => {
      cancelAnimationFrame(animId)
      window.removeEventListener('resize', resize)
    }
  }, [])

  return (
    <canvas
      ref={canvasRef}
      style={{ position: 'absolute', top: 0, left: 0, width: '100%', height: '100%', display: 'block' }}
    />
  )
}
