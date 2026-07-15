import json
from typing import Optional

from app.domain.entities.job import Job
from app.domain.repositories.job_repository import JobRepository
from app.infrastructure.database.connection import get_connection


class PostgresJobRepository(JobRepository):
    def create(self, text: str) -> Job:
        conn = get_connection()
        try:
            cursor = conn.cursor()
            cursor.execute(
                """
                INSERT INTO analysis_jobs (text, status)
                VALUES (%s, 'PENDIENTE')
                RETURNING id, text, status, sentiment, keywords, created_at, updated_at
                """,
                (text,),
            )
            row = cursor.fetchone()
            conn.commit()
            cursor.close()
            return self._to_entity(row)
        finally:
            conn.close()

    def find_by_id(self, job_id: str) -> Optional[Job]:
        conn = get_connection()
        try:
            cursor = conn.cursor()
            cursor.execute(
                """
                SELECT id, text, status, sentiment, keywords, created_at, updated_at
                FROM analysis_jobs WHERE id = %s
                """,
                (job_id,),
            )
            row = cursor.fetchone()
            cursor.close()
            return self._to_entity(row) if row else None
        finally:
            conn.close()

    def _to_entity(self, row) -> Job:
        keywords = row['keywords']
        if isinstance(keywords, str):
            keywords = json.loads(keywords)
        return Job(
            id=str(row['id']),
            text=row['text'],
            status=row['status'],
            sentiment=row['sentiment'],
            keywords=keywords,
            created_at=row['created_at'],
            updated_at=row['updated_at'],
        )
