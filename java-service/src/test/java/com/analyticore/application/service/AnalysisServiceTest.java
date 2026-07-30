package com.analyticore.application.service;

import com.analyticore.domain.model.AnalysisResult;
import com.analyticore.domain.model.Job;
import com.analyticore.domain.repository.JobRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalysisServiceTest {

    @Mock
    private JobRepository jobRepository;

    @InjectMocks
    private AnalysisService analysisService;

    @Test
    void analyze_detectsPositiveSentiment() {
        Job job = new Job();
        job.setId("job-1");
        job.setText("excelente bueno genial");
        job.setStatus("PENDIENTE");

        when(jobRepository.findById("job-1")).thenReturn(Optional.of(job));

        analysisService.analyze("job-1");

        verify(jobRepository).updateStatus("job-1", "PROCESANDO");

        ArgumentCaptor<AnalysisResult> captor = ArgumentCaptor.forClass(AnalysisResult.class);
        verify(jobRepository).saveResults(org.mockito.Mockito.eq("job-1"), captor.capture());
        assertEquals("POSITIVO", captor.getValue().getSentiment());
    }

    @Test
    void analyze_detectsNegativeSentiment() {
        Job job = new Job();
        job.setId("job-2");
        job.setText("terrible horrible malo");
        job.setStatus("PENDIENTE");

        when(jobRepository.findById("job-2")).thenReturn(Optional.of(job));

        analysisService.analyze("job-2");

        ArgumentCaptor<AnalysisResult> captor = ArgumentCaptor.forClass(AnalysisResult.class);
        verify(jobRepository).saveResults(org.mockito.Mockito.eq("job-2"), captor.capture());
        assertEquals("NEGATIVO", captor.getValue().getSentiment());
    }
}
