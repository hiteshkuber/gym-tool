package com.example.gymworkout.gymworkout_insight.dto;

import lombok.Data;
import java.util.List;

@Data
public class WorkoutDTO {
    private String date;
    private List<ExerciseDTO> exercises;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<ExerciseDTO> getExercises() {
        return exercises;
    }

    public void setExercises(List<ExerciseDTO> exercises) {
        this.exercises = exercises;
    }
}
