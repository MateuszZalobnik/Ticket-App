package org.Model;

public class User {

	public User() {
	}

	public User (int id, String login, String email, UserRole role) {
		this.id = id;
		this.login = login;
		this.email = email;
		this.role = role;
	}
	public int id;
	public String login;
	public String password;
	public String email;
	public UserRole role;

}