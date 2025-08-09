package com.example.gymworkout.gymworkout_insight.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Meal {
    @Id
    @GeneratedValue
    private Long id;

    private String date; // Format: yyyy-MM-dd HH:mm

    private String notes;

    @OneToMany(mappedBy = "meal", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<MealItem> items;

    // Getters & Setters
    public Long getId() { return id; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public List<MealItem> getItems() { return items; }
    public void setItems(List<MealItem> items) { this.items = items; }
}
