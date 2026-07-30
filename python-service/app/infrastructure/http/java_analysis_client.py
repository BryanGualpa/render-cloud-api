import os
import time

import requests


class JavaAnalysisClient:
    def __init__(self):
        self.base_url = os.getenv('JAVA_SERVICE_URL', 'http://localhost:8080').rstrip('/')
        self.timeout = int(os.getenv('JAVA_SERVICE_TIMEOUT', '90'))
        self.retries = int(os.getenv('JAVA_SERVICE_RETRIES', '3'))

    def trigger_analysis(self, job_id: str) -> None:
        last_error = None

        for attempt in range(1, self.retries + 1):
            try:
                response = requests.post(
                    f'{self.base_url}/api/analyze',
                    json={'jobId': job_id},
                    timeout=self.timeout,
                )
                response.raise_for_status()
                return
            except requests.RequestException as exc:
                last_error = exc
                if attempt < self.retries:
                    time.sleep(5 * attempt)

        raise last_error
