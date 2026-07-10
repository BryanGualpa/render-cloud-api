from flask import Flask, jsonify
import os
import psycopg2
from psycopg2.extras import RealDictCursor

app = Flask(__name__)


DATABASE_URL = os.getenv(
    'DATABASE_URL',
    'postgresql://admin:yn6BEQGw4MY3bt2nLBLzrrkdfmVsFm4g@dpg-d5tdhafpm1nc7399kgv0-a.oregon-postgres.render.com:5432/appdb_y98p'
)


def get_db_connection():
    """Conexión a PostgreSQL. En Render usa la URL interna (sin dominio público)."""
    url = DATABASE_URL

    if os.getenv('RENDER') and '.oregon-postgres.render.com' in url:
        url = url.replace('.oregon-postgres.render.com', '')
        return psycopg2.connect(url, cursor_factory=RealDictCursor)

    if 'sslmode=' not in url:
        separator = '&' if '?' in url else '?'
        url = f'{url}{separator}sslmode=require'
    return psycopg2.connect(url, cursor_factory=RealDictCursor)


@app.route('/')
def home():
    return jsonify({
        'message': ' API Flask + PostgreSQL en Render',
        'status': 'running',
        'database': 'PostgreSQL externo configurado',
        'student': 'Docente Arquitectura'
    })

@app.route('/health')
def health():
    """Health check que prueba la conexión a PostgreSQL"""
    try:
        # Conectar a PostgreSQL
        conn = get_db_connection()
        cursor = conn.cursor()
        cursor.execute('SELECT version(), NOW()')
        result = cursor.fetchone()
        cursor.close()
        conn.close()
        
        return jsonify({
            'status': 'healthy',
            'postgres': 'connected',
            'version': result['version'].split(',')[0],
            'timestamp': result['now'].isoformat()
        })
    except Exception as e:
        return jsonify({
            'status': 'error',
            'message': str(e)
        }), 500

@app.route('/test-db')
def test_db():
    """Prueba simple de creación y consulta"""
    try:
        conn = get_db_connection()
        cursor = conn.cursor()
        
        # Crear tabla temporal
        cursor.execute("""
            CREATE TABLE IF NOT EXISTS test_table (
                id SERIAL PRIMARY KEY,
                message TEXT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """)
        
        # Insertar dato
        cursor.execute(
            "INSERT INTO test_table (message) VALUES (%s) RETURNING id, message, created_at",
            ('¡Hola desde Render!',)
        )
        
        new_row = cursor.fetchone()
        
        # Leer todos los datos
        cursor.execute("SELECT * FROM test_table ORDER BY created_at DESC")
        all_rows = cursor.fetchall()
        
        conn.commit()
        cursor.close()
        conn.close()
        
        return jsonify({
            'inserted': new_row,
            'all_data': all_rows,
            'count': len(all_rows)
        })
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@app.route('/env')
def show_env():
    """Muestra variables de entorno (sin credenciales)"""
    return jsonify({
        'port': os.getenv('PORT'),
        'python_version': os.getenv('PYTHON_VERSION', '3.11'),
        'in_render': bool(os.getenv('RENDER')),
        'database_configured': bool(os.getenv('DATABASE_URL'))
    })

if __name__ == '__main__':
    port = int(os.getenv('PORT', 5000))
    print(f" Servidor en puerto {port}")
    app.run(host='0.0.0.0', port=port, debug=False)
