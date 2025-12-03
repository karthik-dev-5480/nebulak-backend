package com.nebulak.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;
    
    private int topicOrder;

    // Duration in minutes
    private Integer durationMinutes;

    // The URL of the instructional video
    @Column(nullable = false)
    private String videoUrl;

    @ManyToOne
    @JoinColumn(name = "section_id", nullable = false)
    @JsonIgnore // Avoid circular references
    private Section section;

    // Getters, Setters, and Constructors...

    public Topic() {
    }

    public Topic(Long id, String title, String description, Integer durationMinutes, String videoUrl, Section section,int topicOrder) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.videoUrl = videoUrl;
        this.section = section;
        this.topicOrder=topicOrder;
    }
    
    

    public int getTopicOrder() {
		return topicOrder;
	}

	public void setTopicOrder(int topicOrder) {
		this.topicOrder = topicOrder;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }
}