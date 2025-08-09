package com.example.gymworkout.gymworkout_insight.utils;

import java.util.Base64;

public class GeminiUtils {
    public static String buildImagePromptJson(String prompt, byte[] imageBytes, String mimeType) {
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        return """
        {
          "contents": [
            { "parts": [
              { "text": "%s" },
              { "inlineData": { "mimeType": "%s", "data": "%s" } }
            ]}
          ]
        }
        """.formatted(prompt.replace("\"", "\\\""), mimeType, base64Image);
    }

    public static String buildTextPromptJson(String prompt) {
        return """
        {
          "contents": [
            { "parts": [
              { "text": "%s" }
            ]}
          ]
        }
        """.formatted(prompt.replace("\"", "\\\""));
    }

    // Robust JSON extractor for arrays or objects in AI output
    public static String extractJsonFromText(String response) {
        int startArr = response.indexOf('[');
        int endArr = response.lastIndexOf(']');
        int startObj = response.indexOf('{');
        int endObj = response.lastIndexOf('}');
        if (startArr != -1 && endArr > startArr)
            return response.substring(startArr, endArr + 1);
        else if (startObj != -1 && endObj > startObj)
            return response.substring(startObj, endObj + 1);
        else
            throw new IllegalArgumentException("No JSON object or array found in AI response.");
    }
}
