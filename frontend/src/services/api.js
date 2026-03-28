const BASE = import.meta.env.VITE_API_URL || 'http://localhost:8080'

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
  // submit repo for analysis — returns { jobId, status }
  analyzeRepo: (repoUrl) =>
    request('POST', '/api/repos/analyze', { repoUrl }),

  // poll repo status — returns { status, repoId }
  getRepoStatus: (repoUrl) =>
    request('GET', `/api/repos/status?url=${encodeURIComponent(repoUrl)}`),

  // get quiz questions for a repo
  getQuizQuestions: (repoId) =>
    request('GET', `/api/quiz/${repoId}/questions`),

  // submit quiz answers — returns { skillLevel, matchedIssues }
  submitQuiz: (repoId, answers) =>
    request('POST', `/api/quiz/${repoId}/submit`, { answers }),

  // get contribution guide for an issue
  getGuide: (repoId, issueId) =>
    request('GET', `/api/guide/${repoId}/issues/${issueId}`),
}
