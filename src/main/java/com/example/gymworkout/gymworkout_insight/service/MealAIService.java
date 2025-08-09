package com.example.gymworkout.gymworkout_insight.service;

import com.example.gymworkout.gymworkout_insight.entity.Meal;
import com.example.gymworkout.gymworkout_insight.entity.MealItem;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MealAIService {
    List<MealItem> analyzeMealImageLegacy(MultipartFile image, String contextNotes);
    List<MealItem> analyzeMealImage(MultipartFile image, String contextNotes);
    String generateNutritionInsight(Meal meal);
    String generatePeriodNutritionInsight(List<Meal> meals, int days);

}
