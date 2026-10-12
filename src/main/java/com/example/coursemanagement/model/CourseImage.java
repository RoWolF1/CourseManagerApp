package com.example.coursemanagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "CourseImage")
public class CourseImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "URL hình ảnh không được để trống")
    @Column(nullable = false, length = 500)
    private String imageUrl;

    @Column(nullable = false)
    private Boolean isThumbnail = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CourseId", nullable = false)
    private Course course;

    public CourseImage() {
    }

    public Integer getId() { 
        return id; 
    }
    
    public void setId(Integer id) { 
        this.id = id; 
    }
    
    public String getImageUrl() { 
        return imageUrl; 
    }
    
    public void setImageUrl(String imageUrl) { 
        this.imageUrl = imageUrl; 
    }
    
    public Boolean getIsThumbnail() { 
        return isThumbnail; 
    }
    
    public void setIsThumbnail(Boolean isThumbnail) { 
        this.isThumbnail = isThumbnail; 
    }
    
    public Course getCourse() { 
        return course; 
    }
    
    public void setCourse(Course course) { 
        this.course = course; 
    }
}