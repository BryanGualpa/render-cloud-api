package com.analyticore.application.service;

import com.analyticore.domain.model.AnalysisResult;
import com.analyticore.domain.model.Job;
import com.analyticore.domain.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalysisService {
    private static final Set<String> POSITIVE = Set.of(
            "bueno", "buena", "excelente", "genial", "feliz", "amor", "positivo",
            "maravilloso", "increible", "fantastico", "hermoso", "agradable"
    );
    private static final Set<String> NEGATIVE = Set.of(
            "malo", "mala", "terrible", "triste", "odio", "horrible", "negativo",
            "pesimo", "feo", "molesto", "enojado", "decepcion"
    );
    private static final Set<String> STOPWORDS = Set.of(
            "el", "la", "los", "las", "un", "una", "de", "del", "en", "y", "o",
            "que", "es", "a", "por", "para", "con", "se", "su", "al", "lo", "como"
    );

    private final JobRepository jobRepository;

    public AnalysisService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public void analyze(String jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Trabajo no encontrado"));

        jobRepository.updateStatus(jobId, "PROCESANDO");

        try {
            AnalysisResult result = performAnalysis(job.getText());
            jobRepository.saveResults(jobId, result);
        } catch (Exception e) {
            jobRepository.updateStatus(jobId, "ERROR");
            throw e;
        }
    }

    private AnalysisResult performAnalysis(String text) {
        String normalized = normalize(text);
        List<String> words = Arrays.stream(normalized.split("\\s+"))
                .filter(w -> w.length() > 2)
                .filter(w -> !STOPWORDS.contains(w))
                .collect(Collectors.toList());

        int positive = 0;
        int negative = 0;
        for (String word : words) {
            if (POSITIVE.contains(word)) positive++;
            if (NEGATIVE.contains(word)) negative++;
        }

        String sentiment;
        if (positive > negative) {
            sentiment = "POSITIVO";
        } else if (negative > positive) {
            sentiment = "NEGATIVO";
        } else {
            sentiment = "NEUTRO";
        }

        List<String> keywords = words.stream()
                .collect(Collectors.groupingBy(w -> w, Collectors.counting()))
                .entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(5)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        return new AnalysisResult(sentiment, keywords);
    }

    private String normalize(String text) {
        String lower = text.toLowerCase(Locale.ROOT);
        String stripped = Normalizer.normalize(lower, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return stripped.replaceAll("[^a-z0-9\\s]", " ").trim();
    }
}
