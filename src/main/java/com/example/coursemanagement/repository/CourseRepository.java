package com.example.coursemanagement.repository;

import com.example.coursemanagement.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {

    boolean existsByNameIgnoreCaseAndCategoryId(String name, Integer categoryId);
    boolean existsByNameIgnoreCaseAndCategoryIdAndIdNot(String name, Integer categoryId, Integer id);
    boolean existsByCategoryId(Integer categoryId);

    // Lọc và sắp xếp trực tiếp tại SQL Server (không kéo toàn bộ về ram)
    @Query("SELECT c FROM Course c WHERE " +
           "(:keyword IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:categoryId IS NULL OR c.category.id = :categoryId) AND " +
           "(:isActive IS NULL OR c.isActive = :isActive) AND " +
           "(:minFee IS NULL OR c.tuitionFee >= :minFee) AND " +
           "(:maxFee IS NULL OR c.tuitionFee <= :maxFee) " +
           "ORDER BY " +
           "CASE WHEN :sort = 'feeAsc' THEN c.tuitionFee END ASC, " +
           "CASE WHEN :sort = 'feeDesc' THEN c.tuitionFee END DESC, " +
           "CASE WHEN :sort = 'durationAsc' THEN c.duration END ASC, " +
           "c.id DESC")
    List<Course> searchCourses(@Param("keyword") String keyword,
                               @Param("categoryId") Integer categoryId,
                               @Param("isActive") Boolean isActive,
                               @Param("minFee") BigDecimal minFee,
                               @Param("maxFee") BigDecimal maxFee,
                               @Param("sort") String sort);
}