package com.example.coursemanagement.controller;

import com.example.coursemanagement.model.Course;
import com.example.coursemanagement.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public String listCourses(@RequestParam(required = false) String keyword,
                              @RequestParam(required = false) Integer categoryId,
                              @RequestParam(required = false) Boolean isActive,
                              @RequestParam(required = false) BigDecimal minFee,
                              @RequestParam(required = false) BigDecimal maxFee,
                              @RequestParam(defaultValue = "none") String sort,
                              Model model) {

        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("isActive", isActive);
        model.addAttribute("minFee", minFee);
        model.addAttribute("maxFee", maxFee);
        model.addAttribute("sort", sort);
        model.addAttribute("categories", courseService.getAllCategories());

        if (minFee != null && maxFee != null && minFee.compareTo(maxFee) > 0) {
            model.addAttribute("feeError", "Học phí tối thiểu lớn hơn học phí tối đa.");
            model.addAttribute("courses", List.of());
            return "courses/list";
        }

        List<Course> courses = courseService.searchCourses(keyword, categoryId, isActive, minFee, maxFee, sort);
        model.addAttribute("courses", courses);
        return "courses/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("course", new Course());
        model.addAttribute("categories", courseService.getAllCategories());
        return "courses/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model) {
        Course course = courseService.getCourseById(id);
        model.addAttribute("course", course);
        model.addAttribute("categories", courseService.getAllCategories());
        return "courses/form";
    }

    @PostMapping("/save")
    public String saveCourse(@Valid @ModelAttribute("course") Course course,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", courseService.getAllCategories());
            return "courses/form";
        }

        try {
            courseService.saveCourse(course);
            redirectAttributes.addFlashAttribute("successMessage", "Lưu khóa học thành công!");
            return "redirect:/courses";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("customError", ex.getMessage());
            model.addAttribute("categories", courseService.getAllCategories());
            return "courses/form";
        }
    }

    @GetMapping("/detail/{id}")
    public String courseDetail(@PathVariable("id") Integer id, Model model) {
        Course course = courseService.getCourseById(id);
        model.addAttribute("course", course);
        return "courses/detail";
    }

    @PostMapping("/detail/{id}/add-image")
    public String addImage(@PathVariable("id") Integer id,
                           @RequestParam("imageUrl") String imageUrl,
                           @RequestParam(value = "isThumbnail", defaultValue = "false") Boolean isThumbnail,
                           RedirectAttributes redirectAttributes) {
        courseService.addImageToCourse(id, imageUrl, isThumbnail);
        redirectAttributes.addFlashAttribute("successMessage", "Thêm ảnh thành công!");
        return "redirect:/courses/detail/" + id;
    }

    @PostMapping("/images/set-thumbnail/{imageId}")
    public String setThumbnail(@PathVariable("imageId") Integer imageId,
                               @RequestParam("courseId") Integer courseId,
                               RedirectAttributes redirectAttributes) {
        courseService.setAsThumbnail(imageId);
        redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật ảnh đại diện!");
        return "redirect:/courses/detail/" + courseId;
    }

    @PostMapping("/category/delete/{id}")
    public String deleteCategory(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            courseService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa thành công loại khóa học!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/courses";
    }
}