package com.nebulak.dto;

public class Subscribe {
	private String email;
	private String name;
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Subscribe(String email, String name) {
		super();
		this.email = email;
		this.name = name;
	}
	
}
