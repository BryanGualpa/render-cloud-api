package com.analyticore.presentation.controller;

import com.analyticore.application.service.AnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AnalysisController {
    private final AnalysisService analysisService;

    public AnalysisController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "healthy", "service", "analysis-service");
    }

    @PostMapping("/analyze")
    public ResponseEntity<Map<String, String>> analyze(@RequestBody Map<String, String> body) {
        String jobId = body.get("jobId");
        if (jobId == null || jobId.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "jobId es requerido"));
        }
        analysisService.analyze(jobId);
        return ResponseEntity.ok(Map.of("jobId", jobId, "status", "COMPLETADO"));
    }
}
