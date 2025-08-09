package com.example.gymworkout.gymworkout_insight.controller;

import com.example.gymworkout.gymworkout_insight.entity.Meal;
import com.example.gymworkout.gymworkout_insight.entity.MealItem;
import com.example.gymworkout.gymworkout_insight.repository.MealRepository;
import com.example.gymworkout.gymworkout_insight.service.MealAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/meal")
public class MealController {

    @Autowired
    private MealRepository mealRepository;

    @Autowired
    private MealAIService mealAIService;

    /**
     * Analyse meal from image (no DB save)
     */
    @PostMapping("/analyze")
    public ResponseEntity<?> analyzeMeal(
            @RequestParam("image") MultipartFile image,
            @RequestParam(value = "notes", required = false) String notes
    ) {
        try {
            List<MealItem> items = mealAIService.analyzeMealImage(image, notes);
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error analyzing meal: " + e.getMessage());
        }
    }

    /**
     * Save confirmed meal/nutrition details
     */
    @PostMapping("/confirm")
    public ResponseEntity<?> confirmMeal(@RequestBody Meal meal) {
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            meal.setDate(LocalDateTime.now().format(fmt));
            if (meal.getItems() != null) {
                meal.getItems().forEach(item -> item.setMeal(meal));
            }
            Meal savedMeal = mealRepository.save(meal);
            String insight = mealAIService.generateNutritionInsight(savedMeal);
            return ResponseEntity.ok(Map.of("meal", savedMeal, "insight", insight));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error saving meal: " + e.getMessage());
        }
    }

    @GetMapping("/history")
    public List<Meal> getMealHistory() {
        return mealRepository.findAll();
    }

    @GetMapping("/insight/{mealId}")
    public ResponseEntity<?> getSingleMealInsight(@PathVariable Long mealId) {
        Optional<Meal> optionalMeal = mealRepository.findById(mealId);
        if (optionalMeal.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Meal not found for ID " + mealId));
        }
        String feedback = mealAIService.generateNutritionInsight(optionalMeal.get());
        return ResponseEntity.ok(Map.of("insight", feedback));
    }

    @GetMapping("/insight/last7days")
    public ResponseEntity<?> getLast7DaysMealInsight() {
        String startDate = LocalDate.now().minusDays(6).toString();
        List<Meal> meals = mealRepository.findMealsFromDate(startDate);
        if (meals.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "No meals found in last 7 days"));
        }
        String feedback = mealAIService.generatePeriodNutritionInsight(meals, 7);
        return ResponseEntity.ok(Map.of("insight", feedback));
    }

    @GetMapping("/insight/last30days")
    public ResponseEntity<?> getLast30DaysMealInsight() {
        String startDate = LocalDate.now().minusDays(29).toString();
        List<Meal> meals = mealRepository.findMealsFromDate(startDate);
        if (meals.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "No meals found in last 30 days"));
        }
        String feedback = mealAIService.generatePeriodNutritionInsight(meals, 30);
        return ResponseEntity.ok(Map.of("insight", feedback));
    }

    @GetMapping("/insight/last365days")
    public ResponseEntity<?> getLast365DaysMealInsight() {
        String startDate = LocalDate.now().minusDays(364).toString();
        List<Meal> meals = mealRepository.findMealsFromDate(startDate);
        if (meals.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "No meals found in last year"));
        }
        String feedback = mealAIService.generatePeriodNutritionInsight(meals, 365);
        return ResponseEntity.ok(Map.of("insight", feedback));
    }
}
