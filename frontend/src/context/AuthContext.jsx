import { createContext, useContext, useState, useEffect } from 'react'
import { apiUrl } from '../services/apiBase'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    fetch(apiUrl('/api/auth/me'), { credentials: 'include' })
      .then(r => r.ok ? r.json() : null)
      .then(data => { if (data && data.id) setUser(data) })
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [])

  const logout = async () => {
    await fetch(apiUrl('/api/auth/logout'), { method: 'POST', credentials: 'include' })
    setUser(null)
  }

  return (
    <AuthContext.Provider value={{ user, setUser, loading, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  return useContext(AuthContext)
}
