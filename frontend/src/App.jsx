import { useState, useEffect, useRef } from 'react'
import './App.css'
import { STATUS_LABELS } from './constants'

const API_URL =
  window.__API_URL__ !== undefined
    ? window.__API_URL__
    : (import.meta.env.VITE_API_URL || 'http://localhost:5000')

async function parseApiResponse(res) {
  const contentType = res.headers.get('content-type') || ''
  if (contentType.includes('application/json')) {
    return res.json()
  }

  const text = await res.text()
  if (res.status === 404) {
    throw new Error('API no encontrada. Verifica que render-cloud-api use Root Directory: python-service.')
  }
  if (res.status >= 500) {
    throw new Error('El servicio Python no está disponible. Espera el redeploy y vuelve a intentar.')
  }
  throw new Error(text || `Error HTTP ${res.status}`)
}

export default function App() {
  const [text, setText] = useState('')
  const [jobId, setJobId] = useState(null)
  const [status, setStatus] = useState(null)
  const [sentiment, setSentiment] = useState(null)
  const [keywords, setKeywords] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const pollRef = useRef(null)

  useEffect(() => {
    return () => {
      if (pollRef.current) clearInterval(pollRef.current)
    }
  }, [])

  const pollJob = (id) => {
    if (pollRef.current) clearInterval(pollRef.current)

    pollRef.current = setInterval(async () => {
      try {
        const res = await fetch(`${API_URL}/api/jobs/${id}`)
        const data = await parseApiResponse(res)
        if (!res.ok) throw new Error(data.error || 'Error al consultar el trabajo')

        setStatus(data.status)
        setSentiment(data.sentiment)
        setKeywords(data.keywords || [])

        if (data.status === 'COMPLETADO' || data.status === 'ERROR') {
          clearInterval(pollRef.current)
          setLoading(false)
        }
      } catch (err) {
        setError(err.message)
        clearInterval(pollRef.current)
        setLoading(false)
      }
    }, 2000)
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError(null)
    setJobId(null)
    setStatus(null)
    setSentiment(null)
    setKeywords([])
    setLoading(true)

    try {
      const res = await fetch(`${API_URL}/api/jobs`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ text }),
      })
      const data = await parseApiResponse(res)
      if (!res.ok) throw new Error(data.error || 'Error al enviar el análisis')

      setJobId(data.jobId)
      setStatus(data.status)
      setSentiment(data.sentiment)
      setKeywords(data.keywords || [])

      if (data.status !== 'COMPLETADO' && data.status !== 'ERROR') {
        pollJob(data.jobId)
      } else {
        setLoading(false)
      }
    } catch (err) {
      setError(err.message)
      setLoading(false)
    }
  }

  return (
    <div className="app">
      <header className="header">
        <h1>AnalytiCore</h1>
        <p>Análisis de sentimiento y palabras clave en la nube</p>
      </header>

      <form onSubmit={handleSubmit} className="form">
        <label htmlFor="text">Texto a analizar</label>
        <textarea
          id="text"
          value={text}
          onChange={(e) => setText(e.target.value)}
          placeholder="Escribe o pega un texto para analizar..."
          rows={6}
          required
        />
        <button type="submit" disabled={loading || !text.trim()}>
          {loading ? 'Analizando...' : 'Enviar análisis'}
        </button>
      </form>

      {error && <div className="error">{error}</div>}

      {jobId && (
        <section className="results">
          <h2>Resultados</h2>
          <div className="result-grid">
            <div className="result-card">
              <span className="label">Job ID</span>
              <span className="value mono">{jobId}</span>
            </div>
            <div className="result-card">
              <span className="label">Estado</span>
              <span className={`value status-${status?.toLowerCase()}`}>
                {STATUS_LABELS[status] || status}
              </span>
            </div>
            {sentiment && (
              <div className="result-card">
                <span className="label">Sentimiento</span>
                <span className={`value sentiment-${sentiment?.toLowerCase()}`}>
                  {sentiment}
                </span>
              </div>
            )}
          </div>

          {keywords.length > 0 && (
            <div className="keywords">
              <h3>Palabras clave</h3>
              <div className="keyword-tags">
                {keywords.map((kw) => (
                  <span key={kw} className="tag">{kw}</span>
                ))}
              </div>
            </div>
          )}
        </section>
      )}

      <footer className="footer">
        <p>Arquitectura orientada a servicios — Render Cloud</p>
      </footer>
    </div>
  )
}
