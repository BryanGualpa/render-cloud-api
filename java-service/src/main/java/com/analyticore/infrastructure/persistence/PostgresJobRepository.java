package com.analyticore.infrastructure.persistence;

import com.analyticore.domain.model.AnalysisResult;
import com.analyticore.domain.model.Job;
import com.analyticore.domain.repository.JobRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class PostgresJobRepository implements JobRepository {
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public PostgresJobRepository(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public Optional<Job> findById(String jobId) {
        List<Job> jobs = jdbcTemplate.query(
                "SELECT id, text, status, sentiment, keywords, created_at, updated_at FROM analysis_jobs WHERE id = ?::uuid",
                this::mapJob,
                jobId
        );
        return jobs.stream().findFirst();
    }

    @Override
    public void updateStatus(String jobId, String status) {
        jdbcTemplate.update(
                "UPDATE analysis_jobs SET status = ?, updated_at = ? WHERE id = ?::uuid",
                status, LocalDateTime.now(), jobId
        );
    }

    @Override
    public void saveResults(String jobId, AnalysisResult result) {
        try {
            String keywordsJson = objectMapper.writeValueAsString(result.getKeywords());
            jdbcTemplate.update(
                    "UPDATE analysis_jobs SET status = 'COMPLETADO', sentiment = ?, keywords = ?::jsonb, updated_at = ? WHERE id = ?::uuid",
                    result.getSentiment(), keywordsJson, LocalDateTime.now(), jobId
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializando keywords", e);
        }
    }

    private Job mapJob(ResultSet rs, int rowNum) throws SQLException {
        Job job = new Job();
        job.setId(rs.getString("id"));
        job.setText(rs.getString("text"));
        job.setStatus(rs.getString("status"));
        job.setSentiment(rs.getString("sentiment"));
        String keywordsJson = rs.getString("keywords");
        if (keywordsJson != null) {
            try {
                job.setKeywords(objectMapper.readValue(keywordsJson, List.class));
            } catch (JsonProcessingException e) {
                job.setKeywords(List.of());
            }
        }
        if (rs.getTimestamp("created_at") != null) {
            job.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        if (rs.getTimestamp("updated_at") != null) {
            job.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        }
        return job;
    }
}
