package com.example.gymworkout.gymworkout_insight.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
public class MealItem {
    @Id
    @GeneratedValue
    private Long id;

    private String description;
    private Integer calories;
    private Integer protein;
    private Integer carbs;
    private Integer fats;
    private Integer sugar;
    private Integer quantity;

    @ManyToOne
    @JsonBackReference
    private Meal meal;

    // Getters & Setters
    public Long getId() { return id; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getCalories() { return calories; }
    public void setCalories(Integer calories) { this.calories = calories; }
    public Integer getProtein() { return protein; }
    public void setProtein(Integer protein) { this.protein = protein; }
    public Integer getCarbs() { return carbs; }
    public void setCarbs(Integer carbs) { this.carbs = carbs; }
    public Integer getFats() { return fats; }
    public void setFats(Integer fats) { this.fats = fats; }
    public Integer getSugar() { return sugar; }
    public void setSugar(Integer sugar) { this.sugar = sugar; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public Meal getMeal() { return meal; }
    public void setMeal(Meal meal) { this.meal = meal; }
}
