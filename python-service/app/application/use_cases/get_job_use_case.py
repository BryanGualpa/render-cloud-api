from app.domain.repositories.job_repository import JobRepository


class GetJobUseCase:
    def __init__(self, repository: JobRepository):
        self.repository = repository

    def execute(self, job_id: str):
        job = self.repository.find_by_id(job_id)
        if not job:
            raise LookupError('Trabajo no encontrado')
        return job
