// all requests go through Vite's proxy to http://localhost:8080
const BASE = ''

async function request(method, path, body) {
  const res = await fetch(`${BASE}${path}`, {
    method,
    headers: { 'Content-Type': 'application/json' },
    body: body ? JSON.stringify(body) : undefined,
  })
  if (!res.ok) {
    const err = await res.json().catch(() => ({ message: res.statusText }))
    throw new Error(err.message || `Request failed: ${res.status}`)
  }
  return res.json()
}

export const api = {
  analyzeRepo: (repoUrl) =>
    request('POST', '/api/repos/analyze', { repoUrl }),

  getRepoStatus: (repoUrl) =>
    request('GET', `/api/repos/status?url=${encodeURIComponent(repoUrl)}`),

  getQuizQuestions: (repoId) =>
    request('GET', `/api/quiz/${repoId}/questions`),

  submitQuiz: (repoId, answers) =>
    request('POST', `/api/quiz/${repoId}/submit`, { answers }),

  getGuide: (repoId, issueId) =>
    request('GET', `/api/guide/${repoId}/issues/${issueId}`),
}
