from dataclasses import dataclass
from datetime import datetime
from typing import List, Optional


@dataclass
class Job:
    id: str
    text: str
    status: str
    sentiment: Optional[str] = None
    keywords: Optional[List[str]] = None
    created_at: Optional[datetime] = None
    updated_at: Optional[datetime] = None
