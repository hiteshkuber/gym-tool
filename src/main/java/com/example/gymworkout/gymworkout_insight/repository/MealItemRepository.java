package com.example.gymworkout.gymworkout_insight.repository;

import com.example.gymworkout.gymworkout_insight.entity.MealItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MealItemRepository extends JpaRepository<MealItem, Long> {
}
