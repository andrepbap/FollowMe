package com.followme.entity;

public class User {

	private int id;
	private String name;
	private String email;
	private String url;
	private String authorized;
	private String password;
	
	public User(){
		
	}
	
	public User(int id, String name, String email) {
		this.id = id;
		this.name = name;
		this.email = email;
	}

	public User(int id, String url, String authorized, String name, String email, String senha, int logado) {
		this.id = id;
		this.url = url;
		this.authorized = authorized;
		this.name = name;
		this.email = email;

	}

	public String getAuthorized() {
		return authorized;
	}

	public void setAuthorized(String authorized) {
		this.authorized = authorized;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "Motorista [id=" + id + ", name=" + name + ", email=" + email
				+ "]";
	}

}