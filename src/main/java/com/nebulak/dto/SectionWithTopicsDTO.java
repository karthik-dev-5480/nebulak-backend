package com.nebulak.dto;

import java.util.List;

public class SectionWithTopicsDTO {
    private Long id;
    private String title;
    private Integer sectionOrder;
    private String description;
    private List<TopicDTO> topics; // The list of topics within this section

    // Constructor
    public SectionWithTopicsDTO(Long id, String title, Integer sectionOrder, String description, List<TopicDTO> topics) {
        this.id = id;
        this.title = title;
        this.sectionOrder = sectionOrder;
        this.description = description;
        this.topics = topics;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Integer getSectionOrder() { return sectionOrder; }
    public void setSectionOrder(Integer sectionOrder) { this.sectionOrder = sectionOrder; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<TopicDTO> getTopics() { return topics; }
    public void setTopics(List<TopicDTO> topics) { this.topics = topics; }
}