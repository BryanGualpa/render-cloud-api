FROM python:3.11-slim

WORKDIR /app

# Instalar dependencias del sistema necesarias
RUN apt-get update && apt-get install -y \
    libpq-dev \
    && rm -rf /var/lib/apt/lists/*


COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copiar la aplicación
COPY app.py .

# Puerto (Render lo asigna)
ENV PORT=5000
EXPOSE $PORT


CMD gunicorn --bind 0.0.0.0:$PORT --workers 1 app:app
