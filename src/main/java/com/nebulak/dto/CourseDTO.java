package com.nebulak.dto;

import java.util.ArrayList;
import java.util.List;

import com.nebulak.model.Category;
import com.nebulak.model.Section;

//com.nebulak.dto.CourseDTO.java
public class CourseDTO {
 private Long id;
 private String title;
 private String description;
 private String instructorName;
 private Double price;
 private String imageUrl;
 private Double discountedPrice;
 private int duration;
 private Long categoryId;
 private Category category;
 private List<Section> sections = new ArrayList<>();
 
 

 public List<Section> getSections() {
	return sections;
}
 public void setSections(List<Section> sections) {
	this.sections = sections;
 }
 public Category getCategory() {
	return category;
}
 public void setCategory(Category category) {
	this.category = category;
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
 public String getInstructorName() {
	return instructorName;
 }
 public void setInstructorName(String instructorName) {
	this.instructorName = instructorName;
 }
 public Double getPrice() {
	return price;
 }
 public void setPrice(Double price) {
	this.price = price;
 }
 public String getImageUrl() {
	return imageUrl;
 }
 public void setImageUrl(String imageUrl) {
	this.imageUrl = imageUrl;
 }
 public Long getCategoryId() {
	return categoryId;
 }
 public void setCategoryId(Long categoryId) {
	this.categoryId = categoryId;
 }
 public Double getDiscountedPrice() {
	return discountedPrice;
 }
 public void setDiscountedPrice(Double discountedPrice) {
	this.discountedPrice = discountedPrice;
 }
 public int getDuration() {
	return duration;
 }
 public void setDuration(int duration) {
	this.duration = duration;
 }


}