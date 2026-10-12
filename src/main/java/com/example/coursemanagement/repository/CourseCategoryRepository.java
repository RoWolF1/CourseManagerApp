package com.example.coursemanagement.repository;

import com.example.coursemanagement.model.CourseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseCategoryRepository extends JpaRepository<CourseCategory, Integer> {
}