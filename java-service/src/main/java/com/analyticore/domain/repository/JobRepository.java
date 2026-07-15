package com.analyticore.domain.repository;

import com.analyticore.domain.model.AnalysisResult;
import com.analyticore.domain.model.Job;

import java.util.Optional;

public interface JobRepository {
    Optional<Job> findById(String jobId);
    void updateStatus(String jobId, String status);
    void saveResults(String jobId, AnalysisResult result);
}
