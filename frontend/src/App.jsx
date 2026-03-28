import { Routes, Route } from 'react-router-dom'
import LandingPage from './pages/LandingPage'
import AnalysisPage from './pages/AnalysisPage'
import QuizPage from './pages/QuizPage'
import IssueSelectionPage from './pages/IssueSelectionPage'
import GuidePage from './pages/GuidePage'

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<LandingPage />} />
      <Route path="/analysis" element={<AnalysisPage />} />
      <Route path="/quiz" element={<QuizPage />} />
      <Route path="/issues" element={<IssueSelectionPage />} />
      <Route path="/guide" element={<GuidePage />} />
    </Routes>
  )
}
