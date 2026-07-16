from flask import Blueprint, jsonify, request
import os

from app.application.use_cases.create_job_use_case import CreateJobUseCase
from app.application.use_cases.get_job_use_case import GetJobUseCase


def create_routes(create_job: CreateJobUseCase, get_job: GetJobUseCase) -> Blueprint:
    api = Blueprint('api', __name__)

    @api.route('/health', methods=['GET'])
    def health():
        return jsonify({
            'status': 'healthy',
            'service': 'submission-service',
            'databaseConfigured': bool(os.getenv('DATABASE_URL')),
            'javaServiceConfigured': bool(os.getenv('JAVA_SERVICE_URL')),
        })

    @api.route('/', methods=['GET'])
    def root():
        return jsonify({
            'service': 'submission-service',
            'status': 'running',
            'endpoints': ['/health', '/api/jobs'],
        })

    @api.route('/api/jobs', methods=['POST'])
    def submit_job():
        data = request.get_json(silent=True) or {}
        text = data.get('text', '')
        try:
            job = create_job.execute(text)
            return jsonify({
                'jobId': job.id,
                'status': job.status,
                'sentiment': job.sentiment,
                'keywords': job.keywords,
            }), 201
        except ValueError as exc:
            return jsonify({'error': str(exc)}), 400
        except Exception as exc:
            return jsonify({'error': f'Error al procesar: {exc}'}), 500

    @api.route('/api/jobs/<job_id>', methods=['GET'])
    def job_status(job_id):
        try:
            job = get_job.execute(job_id)
            return jsonify({
                'jobId': job.id,
                'status': job.status,
                'text': job.text,
                'sentiment': job.sentiment,
                'keywords': job.keywords,
                'createdAt': job.created_at.isoformat() if job.created_at else None,
                'updatedAt': job.updated_at.isoformat() if job.updated_at else None,
            })
        except LookupError as exc:
            return jsonify({'error': str(exc)}), 404

    return api
