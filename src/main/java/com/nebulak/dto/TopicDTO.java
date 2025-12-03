package com.nebulak.dto;

public class TopicDTO {
    private Long id;
    private String title;
    private Integer topicOrder;
    private String contentPreview; // Short summary or first few lines of content

    // Constructor
    public TopicDTO(Long id, String title, Integer topicOrder ) {
        this.id = id;
        this.title = title;
        this.topicOrder = topicOrder;
        
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Integer getTopicOrder() { return topicOrder; }
    public void setTopicOrder(Integer topicOrder) { this.topicOrder = topicOrder; }
   
}