from app.domain.repositories.job_repository import JobRepository
from app.infrastructure.http.java_analysis_client import JavaAnalysisClient


class CreateJobUseCase:
    def __init__(self, repository: JobRepository, java_client: JavaAnalysisClient):
        self.repository = repository
        self.java_client = java_client

    def execute(self, text: str):
        if not text or not text.strip():
            raise ValueError('El texto no puede estar vacío')
        if len(text) > 5000:
            raise ValueError('El texto no puede superar 5000 caracteres')

        job = self.repository.create(text.strip())
        self.java_client.trigger_analysis(job.id)
        updated = self.repository.find_by_id(job.id)
        return updated or job
