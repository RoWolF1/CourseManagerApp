package com.example.coursemanagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Course")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Tên khóa học không được để trống")
    @Column(nullable = false, length = 250)
    private String name;

    @NotNull(message = "Học phí không được để trống")
    @DecimalMin(value = "0.01", inclusive = true, message = "Học phí phải lớn hơn 0")
    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal tuitionFee;

    @NotNull(message = "Thời lượng không được để trống")
    @Min(value = 1, message = "Thời lượng phải lớn hơn 0")
    @Column(nullable = false)
    private Integer duration;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @NotNull(message = "Vui lòng chọn loại khóa học")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CourseCategoryId", nullable = false)
    private CourseCategory category;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CourseImage> images = new ArrayList<>();

    // Phân loại: Ngắn hạn <= 30, Dài hạn > 30 (Không tạo cột trong DB)
    @Transient
    public String getDurationType() {
        if (duration == null) return "Không xác định";
        return duration <= 30 ? "Ngắn hạn" : "Dài hạn";
    }

    // Lấy ảnh đại diện
    @Transient
    public CourseImage getThumbnail() {
        if (images == null || images.isEmpty()) return null;
        return images.stream()
                .filter(CourseImage::getIsThumbnail)
                .findFirst()
                .orElse(images.get(0));
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BigDecimal getTuitionFee() { return tuitionFee; }
    public void setTuitionFee(BigDecimal tuitionFee) { this.tuitionFee = tuitionFee; }
    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public CourseCategory getCategory() { return category; }
    public void setCategory(CourseCategory category) { this.category = category; }
    public List<CourseImage> getImages() { return images; }
    public void setImages(List<CourseImage> images) { this.images = images; }
}