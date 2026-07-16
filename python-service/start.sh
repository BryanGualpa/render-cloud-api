#!/bin/sh
set -e

echo "Starting AnalytiCore Python service..."
echo "PORT=${PORT:-5000}"
echo "RENDER=${RENDER:-}"
echo "DATABASE_URL set: $([ -n "$DATABASE_URL" ] && echo yes || echo no)"
echo "JAVA_SERVICE_URL=${JAVA_SERVICE_URL:-}"

python -c "from wsgi import app; print('App loaded:', app.name)" || {
  echo "Failed to load Flask app"
  exit 1
}

exec gunicorn \
  --bind "0.0.0.0:${PORT:-5000}" \
  --workers 1 \
  --timeout 120 \
  --access-logfile - \
  --error-logfile - \
  --capture-output \
  --enable-stdio-inheritance \
  app:app
