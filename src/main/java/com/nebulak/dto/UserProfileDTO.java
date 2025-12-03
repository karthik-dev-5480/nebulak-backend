package com.nebulak.dto;

import java.util.ArrayList;
import java.util.List;

import com.nebulak.model.Cart;
import com.nebulak.model.UserCourseEnrollment;
import com.nebulak.model.UserRoles;

public class UserProfileDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String mobile;
    private List<UserRoles> roles = new ArrayList<>();
    private String profile_pic;
    


    // Constructor
    public UserProfileDTO(String firstName, String lastName, String email, String mobile, List<UserRoles> roles,String profile_pic) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.mobile = mobile;
        this.roles = roles;
        this.profile_pic=profile_pic;
       
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



	public List<UserRoles> getRoles() {
		return roles;
	}



	public void setRoles(List<UserRoles> roles) {
		this.roles = roles;
	}



	public String getProfile_pic() {
		return profile_pic;
	}



	public void setProfile_pic(String profile_pic) {
		this.profile_pic = profile_pic;
	}
    

   

    
}
