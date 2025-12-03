package com.nebulak.dto;

import java.util.ArrayList;
import java.util.List;

import com.nebulak.model.UserCourseEnrollment;

public class UserCourseEnrollmentsDTO {

	 private List<UserCourseEnrollment> enrollments = new ArrayList<>();

	 public List<UserCourseEnrollment> getEnrollments() {
		 return enrollments;
	 }

	 public void setEnrollments(List<UserCourseEnrollment> enrollments) {
		 this.enrollments = enrollments;
	 }

	 public UserCourseEnrollmentsDTO(List<UserCourseEnrollment> enrollments) {
		super();
		this.enrollments = enrollments;
	 }

}
