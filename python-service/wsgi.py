import traceback

try:
    from app.main import create_app

    app = create_app()
except Exception:
    from flask import Flask, jsonify

    app = Flask(__name__)
    _BOOT_ERROR = traceback.format_exc()

    @app.route("/health")
    def boot_health():
        return jsonify({
            "status": "error",
            "service": "submission-service",
            "detail": _BOOT_ERROR,
        }), 500
