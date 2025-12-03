package com.nebulak.dto;

import com.nebulak.model.User;

//File: com.example.yourproject.payload.response.UserDTO.java

public class UserDTO {
 private Long id;
 private String firstName;
 private String lastName;
 private String displayName;
 private String email;
 private String mobile;
 private String profile_pic;
 private boolean enabled;
 
 
 

 public Long getId() {
	return id;
}




 public void setId(Long id) {
	this.id = id;
 }




 public String getFirstName() {
	return firstName;
 }




 public void setFirstName(String firstName) {
	this.firstName = firstName;
 }




 public String getLastName() {
	return lastName;
 }




 public void setLastName(String lastName) {
	this.lastName = lastName;
 }




 public String getDisplayName() {
	return displayName;
 }




 public void setDisplayName(String displayName) {
	this.displayName = displayName;
 }




 public String getEmail() {
	return email;
 }




 public void setEmail(String email) {
	this.email = email;
 }




 public String getMobile() {
	return mobile;
 }




 public void setMobile(String mobile) {
	this.mobile = mobile;
 }




 public String getProfile_pic() {
	return profile_pic;
 }




 public void setProfile_pic(String profile_pic) {
	this.profile_pic = profile_pic;
 }








 public boolean isEnabled() {
	return enabled;
 }




 public void setEnabled(boolean enabled) {
	this.enabled = enabled;
 }




 public UserDTO(User user) {
     this.id = user.getId();
     this.firstName = user.getFirstName();
     this.lastName = user.getLastName();
     this.displayName = user.getDisplayName();
     this.email = user.getEmail();
     this.mobile = user.getMobile();
     this.profile_pic = user.getProfile_pic();
     this.enabled = user.isEnabled();
 }
}