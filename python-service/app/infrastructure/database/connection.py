import os
import psycopg2
from psycopg2.extras import RealDictCursor


def get_database_url() -> str:
    url = os.getenv('DATABASE_URL')
    if not url:
        raise ValueError('DATABASE_URL no está configurada')
    if os.getenv('RENDER'):
        if '.oregon-postgres.render.com' in url:
            url = url.replace('.oregon-postgres.render.com', '')
    elif 'sslmode=' not in url:
        separator = '&' if '?' in url else '?'
        url = f'{url}{separator}sslmode=require'
    return url


def get_connection():
    return psycopg2.connect(get_database_url(), cursor_factory=RealDictCursor)


def init_schema():
    conn = get_connection()
    try:
        cursor = conn.cursor()
        cursor.execute("""
            CREATE TABLE IF NOT EXISTS analysis_jobs (
                id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                text TEXT NOT NULL,
                status VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
                sentiment VARCHAR(20),
                keywords JSONB,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """)
        conn.commit()
        cursor.close()
    finally:
        conn.close()
