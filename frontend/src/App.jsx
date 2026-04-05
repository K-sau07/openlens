import { Routes, Route } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext'
import LandingPage from './pages/LandingPage'
import AnalysisPage from './pages/AnalysisPage'
import QuizPage from './pages/QuizPage'
import IssueSelectionPage from './pages/IssueSelectionPage'
import GuidePage from './pages/GuidePage'
import LoginPage from './pages/auth/LoginPage'
import RegisterPage from './pages/auth/RegisterPage'

export default function App() {
  return (
    <AuthProvider>
      <Routes>
        <Route path="/" element={<LandingPage />} />
        <Route path="/analysis" element={<AnalysisPage />} />
        <Route path="/quiz" element={<QuizPage />} />
        <Route path="/issues" element={<IssueSelectionPage />} />
        <Route path="/guide" element={<GuidePage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
      </Routes>
    </AuthProvider>
  )
}
