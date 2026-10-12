package com.example.coursemanagement.repository;

import com.example.coursemanagement.model.CourseImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseImageRepository extends JpaRepository<CourseImage, Integer> {
    List<CourseImage> findByCourseId(Integer courseId);

    @Modifying
    @Query("UPDATE CourseImage img SET img.isThumbnail = false WHERE img.course.id = :courseId")
    void resetThumbnailByCourseId(@Param("courseId") Integer courseId);
}