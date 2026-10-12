package com.example.coursemanagement.service;

import com.example.coursemanagement.model.Course;
import com.example.coursemanagement.model.CourseCategory;
import com.example.coursemanagement.model.CourseImage;
import com.example.coursemanagement.repository.CourseCategoryRepository;
import com.example.coursemanagement.repository.CourseImageRepository;
import com.example.coursemanagement.repository.CourseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseCategoryRepository categoryRepository;
    private final CourseImageRepository imageRepository;

    public CourseService(CourseRepository courseRepository,
                         CourseCategoryRepository categoryRepository,
                         CourseImageRepository imageRepository) {
        this.courseRepository = courseRepository;
        this.categoryRepository = categoryRepository;
        this.imageRepository = imageRepository;
    }

    public List<CourseCategory> getAllCategories() {
        return categoryRepository.findAll();
    }

    public List<Course> searchCourses(String keyword, Integer categoryId, Boolean isActive,
                                     BigDecimal minFee, BigDecimal maxFee, String sort) {
        if (minFee != null && maxFee != null && minFee.compareTo(maxFee) > 0) {
            throw new IllegalArgumentException("Học phí tối thiểu không được lớn hơn học phí tối đa.");
        }
        return courseRepository.searchCourses(
                (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null,
                categoryId,
                isActive,
                minFee,
                maxFee,
                sort
        );
    }

    public Course getCourseById(Integer id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("CourseId không tồn tại: " + id));
    }

    @Transactional
    public void saveCourse(Course course) {
        if (course.getCategory() == null || course.getCategory().getId() == null) {
            throw new IllegalArgumentException("CourseCategoryId không được để trống.");
        }

        CourseCategory category = categoryRepository.findById(course.getCategory().getId())
                .orElseThrow(() -> new IllegalArgumentException("CourseCategoryId không tồn tại."));
        course.setCategory(category);

        boolean isDuplicate = (course.getId() == null)
                ? courseRepository.existsByNameIgnoreCaseAndCategoryId(course.getName(), category.getId())
                : courseRepository.existsByNameIgnoreCaseAndCategoryIdAndIdNot(course.getName(), category.getId(), course.getId());

        if (isDuplicate) {
            throw new IllegalArgumentException("Tên khóa học không được trùng trong cùng một loại khóa học.");
        }

        courseRepository.save(course);
    }

    @Transactional
    public void deleteCategory(Integer categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new IllegalArgumentException("CourseCategoryId không tồn tại.");
        }
        if (courseRepository.existsByCategoryId(categoryId)) {
            throw new IllegalStateException("Không được phép xóa loại khóa học này vì vẫn còn khóa học thuộc loại đó.");
        }
        categoryRepository.deleteById(categoryId);
    }

    @Transactional
    public void addImageToCourse(Integer courseId, String imageUrl, Boolean isThumbnail) {
        Course course = getCourseById(courseId);
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("URL hình ảnh không hợp lệ.");
        }

        CourseImage image = new CourseImage();
        image.setCourse(course);
        image.setImageUrl(imageUrl.trim());

        if (Boolean.TRUE.equals(isThumbnail)) {
            imageRepository.resetThumbnailByCourseId(courseId);
            image.setIsThumbnail(true);
        } else {
            boolean hasImages = !imageRepository.findByCourseId(courseId).isEmpty();
            image.setIsThumbnail(!hasImages);
        }

        imageRepository.save(image);
    }

    @Transactional
    public void setAsThumbnail(Integer imageId) {
        CourseImage targetImage = imageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("CourseImageId không tồn tại: " + imageId));

        Integer courseId = targetImage.getCourse().getId();
        imageRepository.resetThumbnailByCourseId(courseId);

        targetImage.setIsThumbnail(true);
        imageRepository.save(targetImage);
    }
}