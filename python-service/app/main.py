from flask import Flask
from flask_cors import CORS

from app.application.use_cases.create_job_use_case import CreateJobUseCase
from app.application.use_cases.get_job_use_case import GetJobUseCase
from app.infrastructure.database.connection import init_schema
from app.infrastructure.database.postgres_job_repository import PostgresJobRepository
from app.infrastructure.http.java_analysis_client import JavaAnalysisClient
from app.presentation.api.routes import create_routes


def create_app() -> Flask:
    app = Flask(__name__)
    CORS(app)

    init_schema()

    repository = PostgresJobRepository()
    java_client = JavaAnalysisClient()
    create_job = CreateJobUseCase(repository, java_client)
    get_job = GetJobUseCase(repository)

    app.register_blueprint(create_routes(create_job, get_job))
    return app
