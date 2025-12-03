package com.nebulak.dto;

public class SectionDTO {
    private String title;
    private Integer sectionOrder;
    private String description;

    // Getters and Setters

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getSectionOrder() {
        return sectionOrder;
    }

    public void setSectionOrder(Integer sectionOrder) {
        this.sectionOrder = sectionOrder;
    }

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public SectionDTO(String title, Integer sectionOrder,String description) {
		super();
		this.title = title;
		this.sectionOrder = sectionOrder;
		this.description=description;
	}
    
}