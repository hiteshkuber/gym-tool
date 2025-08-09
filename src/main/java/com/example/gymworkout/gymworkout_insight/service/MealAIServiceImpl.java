package com.example.gymworkout.gymworkout_insight.service;

import com.example.gymworkout.gymworkout_insight.entity.Meal;
import com.example.gymworkout.gymworkout_insight.entity.MealItem;
import com.example.gymworkout.gymworkout_insight.utils.GeminiUtils;
import com.google.genai.Client;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class MealAIServiceImpl implements MealAIService {
    @Value("${gemini.api.key}")
    private String apiKey;

    @Override
    public List<MealItem> analyzeMealImageLegacy(MultipartFile image, String contextNotes) {
        try {
            byte[] imageBytes = image.getBytes();

            String prompt = """
            You are a nutritionist. What do you see in the image given image%s and respond ONLY with a valid JSON array.
            Tell me what you see here, and what would be its nutritianal values.
            Each array item: {description, calories, protein, carbs, fats, sugar}. No extra text or markdown.
            """.formatted(
                    (contextNotes != null && !contextNotes.isBlank())
                            ? " with this extra information: " + contextNotes
                            : ""
            );

            String body = GeminiUtils.buildImagePromptJson(prompt, imageBytes, image.getContentType());

            String aiResponse = Client.builder()
                    .apiKey(apiKey)
                    .build()
                    .models
                    .generateContent("gemini-2.5-flash", body, null)
                    .text();

            // Extract JSON
            String jsonPart = GeminiUtils.extractJsonFromText(aiResponse);

            JSONArray arr = new JSONArray(jsonPart);
            List<MealItem> items = new ArrayList<>();
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                MealItem mi = new MealItem();
                mi.setDescription(obj.optString("description"));
                mi.setCalories(obj.optInt("calories"));
                mi.setProtein(obj.optInt("protein"));
                mi.setCarbs(obj.optInt("carbs"));
                mi.setFats(obj.optInt("fats"));
                mi.setSugar(obj.optInt("sugar"));
                items.add(mi);
            }
            return items;

        } catch (Exception e) {
            throw new RuntimeException("AI analysis failed: " + e.getMessage(), e);
        }
    }

    @Override
    public List<MealItem> analyzeMealImage(MultipartFile image, String contextNotes) {
        try {
            byte[] imageBytes = image.getBytes();

            String promptText = """
        You are a nutritionist. Analyze the food or drink in the image and provide a valid JSON array.
        Each item in the array must be an object with the keys: description, calories, protein, carbs, fats, sugar.
        Do not include any extra text or markdown outside of the JSON array.
        If you see a brand or a product, try to provide its typical nutritional values.
        Extra context: %s
        Respond only in valid json array
        Each array item: {description, calories, protein, carbs, fats, sugar}. No extra text or markdown.
        """.formatted(contextNotes != null && !contextNotes.isBlank() ? contextNotes : "None provided.");

            // Use the new, corrected utility method
            String body = GeminiUtils.buildImagePromptJson(promptText, imageBytes, image.getContentType());

            // The rest of your code remains the same
            String aiResponse = Client.builder()
                    .apiKey(apiKey)
                    .build()
                    .models
                    .generateContent("gemini-2.5-flash", body, null)
                    .text();

            // ... rest of your code to extract and parse the JSON ...
            String jsonPart = GeminiUtils.extractJsonFromText(aiResponse);

            JSONArray arr = new JSONArray(jsonPart);
            List<MealItem> items = new ArrayList<>();
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                MealItem mi = new MealItem();
                mi.setDescription(obj.optString("description"));
                mi.setCalories(obj.optInt("calories"));
                mi.setProtein(obj.optInt("protein"));
                mi.setCarbs(obj.optInt("carbs"));
                mi.setFats(obj.optInt("fats"));
                mi.setSugar(obj.optInt("sugar"));
                items.add(mi);
            }
            return items;

        } catch (Exception e) {
            throw new RuntimeException("AI analysis failed: " + e.getMessage(), e);
        }
    }

    @Override
    public String generateNutritionInsight(Meal meal) {
        StringBuilder sb = new StringBuilder();
        meal.getItems().forEach(it -> {
            sb.append(String.format("%s: %d cal, P:%dg, C:%dg, F:%dg, Sugar:%dg%n",
                    it.getDescription(),
                    it.getCalories(),
                    it.getProtein(),
                    it.getCarbs(),
                    it.getFats(),
                    it.getSugar()));
        });

        String prompt = """
        You are a personal nutrition coach helping with lean muscle growth.
        The following meal's nutrition breakdown is:
        %s
        Provide:
        1. Feedback on this meal for lean muscle growth.
        2. Suggestions for improvement.
        3. Motivation to continue.
        Tone: encouraging, concise.
        """.formatted(sb.toString());

        String body = GeminiUtils.buildTextPromptJson(prompt);

        return Client.builder()
                .apiKey(apiKey)
                .build()
                .models
                .generateContent("gemini-2.5-flash", body, null)
                .text();
    }

    @Override
    public String generatePeriodNutritionInsight(List<Meal> meals, int days) {
        if (meals == null || meals.isEmpty()) return "No meals logged in this period.";
        int totalCal = 0, totalProtein = 0, totalCarbs = 0, totalFats = 0, totalSugar = 0;
        int mealCount = meals.size();

        for (Meal meal : meals) {
            for (MealItem item : meal.getItems()) {
                totalCal += item.getCalories() != null ? item.getCalories() : 0;
                totalProtein += item.getProtein() != null ? item.getProtein() : 0;
                totalCarbs += item.getCarbs() != null ? item.getCarbs() : 0;
                totalFats += item.getFats() != null ? item.getFats() : 0;
                totalSugar += item.getSugar() != null ? item.getSugar() : 0;
            }
        }

        String summary = """
            Meals logged: %d
            Total Calories: %d kcal
            Protein: %dg  Carbs: %dg  Fats: %dg  Sugar: %dg
            """.formatted(mealCount, totalCal, totalProtein, totalCarbs, totalFats, totalSugar);

        String prompt = """
            You are a personal nutrition coach. The user has this meal intake log for the last %d days:
            %s
            The user's goal is lean muscle growth.
            Give:
            1. Summary of nutrition progress.
            2. 2-3 improvement suggestions.
            3. Motivation for staying on track.
            Tone: concise, positive, evidence-driven.
            """.formatted(days, summary);

        String body = GeminiUtils.buildTextPromptJson(prompt);
        return Client.builder().apiKey(apiKey)
                .build()
                .models
                .generateContent("gemini-2.5-flash", body, null)
                .text();
    }
}
