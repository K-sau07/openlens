// API origin. Empty in development so Vite's proxy handles /api/..., which keeps
// requests same-origin and the session cookie automatic. In production the UI is
// on Vercel and the API on Render, so VITE_API_URL supplies the backend origin at
// build time and every request must opt in to sending the cookie.
const API_BASE = (import.meta.env.VITE_API_URL ?? '').replace(/\/$/, '')

export function apiUrl(path) {
  return `${API_BASE}${path}`
}
