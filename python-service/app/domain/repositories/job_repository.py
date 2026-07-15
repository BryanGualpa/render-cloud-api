from abc import ABC, abstractmethod
from typing import Optional

from app.domain.entities.job import Job


class JobRepository(ABC):
    @abstractmethod
    def create(self, text: str) -> Job:
        pass

    @abstractmethod
    def find_by_id(self, job_id: str) -> Optional[Job]:
        pass
