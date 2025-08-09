package com.example.gymworkout.gymworkout_insight.controller;

import com.example.gymworkout.gymworkout_insight.entity.Meal;

public record MealUploadResponse(Meal meal, String insight) {}
