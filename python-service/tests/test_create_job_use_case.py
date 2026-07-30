import pytest

from app.application.use_cases.create_job_use_case import CreateJobUseCase
from app.domain.entities.job import Job


class FakeJobRepository:
    def __init__(self):
        self.created_text = None

    def create(self, text: str) -> Job:
        self.created_text = text
        return Job(id='job-1', text=text, status='PENDIENTE')

    def find_by_id(self, job_id: str):
        return Job(id=job_id, text=self.created_text, status='PENDIENTE')


class FakeJavaClient:
    def __init__(self):
        self.triggered_ids = []

    def trigger_analysis(self, job_id: str) -> None:
        self.triggered_ids.append(job_id)


def test_create_job_rejects_empty_text():
    use_case = CreateJobUseCase(FakeJobRepository(), FakeJavaClient())

    with pytest.raises(ValueError, match='no puede estar vacío'):
        use_case.execute('   ')


def test_create_job_triggers_java_analysis():
    repository = FakeJobRepository()
    java_client = FakeJavaClient()
    use_case = CreateJobUseCase(repository, java_client)

    job = use_case.execute('excelente proyecto')

    assert job.id == 'job-1'
    assert repository.created_text == 'excelente proyecto'
    assert java_client.triggered_ids == ['job-1']
