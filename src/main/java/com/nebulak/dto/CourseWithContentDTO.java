package com.nebulak.dto;

import java.util.List;

public class CourseWithContentDTO {
    private Long id;
    private String title;
    private String imageUrl; // Assuming courses have an image
    private List<SectionWithTopicsDTO> sections; // The list of sections with their topics

    // Constructor
    public CourseWithContentDTO(Long id, String title, String imageUrl, List<SectionWithTopicsDTO> sections) {
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
        this.sections = sections;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public List<SectionWithTopicsDTO> getSections() { return sections; }
    public void setSections(List<SectionWithTopicsDTO> sections) { this.sections = sections; }
}