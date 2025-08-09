package com.example.gymworkout.gymworkout_insight.service;

import com.example.gymworkout.gymworkout_insight.entity.Workout;
import com.example.gymworkout.gymworkout_insight.entity.Exercise;
import com.google.genai.Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    public String generateInsight(Workout workout) {
        String prompt = buildPrompt(workout);

        String body = """
        {
            "contents": [
                { "parts": [{ "text": "%s" }] }
            ]
        }
        """.formatted(prompt);

        return Client.builder()
                .apiKey(apiKey)
                .build()
                .models
                .generateContent("gemini-2.5-flash", body, null)
                .text();
    }

    /**
     * Builds the AI prompt for a single workout.
     * Handles both reps/sets exercises and duration-based exercises.
     */
    private String buildPrompt(Workout workout) {
        String details = workout.getExercises().stream()
                .map(this::formatExercise)
                .collect(Collectors.joining("\n"));

        return """
        You are a professional personal trainer.
        Here is today's workout:
        %s

        Provide:
        1. 1–2 sentence evaluation
        2. Two improvement suggestions
        3. One short motivation line
        Tone: confident, encouraging, concise.
        """.formatted(details);
    }

    /**
     * Generates insight for a period (e.g., last 7 days)
     */
    public String generateInsightForPeriod(List<Workout> workouts, int days) {
        String workoutDetails = workouts.stream()
                .map(w -> {
                    String exercises = w.getExercises().stream()
                            .map(this::formatExercise)
                            .collect(Collectors.joining("\n"));
                    return String.format("%s:\n%s", w.getDate(), exercises);
                })
                .collect(Collectors.joining("\n\n"));

        String prompt = String.format("""
        You are a professional fitness coach.
        Here is the user's workout log for the last %d days:

        %s

        Provide:
        1. A summary of their progress
        2. Trends or patterns observed
        3. Three improvement suggestions
        4. A short motivation line

        Tone: confident, encouraging, concise.
        """, days, workoutDetails);

        String body = """
        {
            "contents": [
                { "parts": [{ "text": "%s" }] }
            ]
        }
        """.formatted(prompt);

        return Client.builder()
                .apiKey(apiKey)
                .build()
                .models
                .generateContent("gemini-2.5-flash", body, null)
                .text();
    }

    /**
     * Formats an exercise string depending on whether duration or reps/sets.
     */
    private String formatExercise(Exercise ex) {
        // If duration present
        if (ex.getDuration() != null && ex.getDuration() > 0) {
            if (ex.getSets() != null && ex.getSets() > 0) {
                return String.format("- %s: %d sets of %d min",
                        ex.getName(),
                        ex.getSets(),
                        ex.getDuration());
            } else {
                return String.format("- %s: %d min",
                        ex.getName(),
                        ex.getDuration());
            }
        }
        // Else fallback to normal reps/sets format
        return String.format("- %s: %d sets of %d reps at %.1f kg",
                ex.getName(),
                ex.getSets() != null ? ex.getSets() : 0,
                ex.getReps() != null ? ex.getReps() : 0,
                ex.getWeight() != null ? ex.getWeight() : 0.0);
    }
}
