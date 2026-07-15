import os
import requests


class JavaAnalysisClient:
    def __init__(self):
        self.base_url = os.getenv('JAVA_SERVICE_URL', 'http://localhost:8080')

    def trigger_analysis(self, job_id: str) -> None:
        response = requests.post(
            f'{self.base_url}/api/analyze',
            json={'jobId': job_id},
            timeout=30,
        )
        response.raise_for_status()
