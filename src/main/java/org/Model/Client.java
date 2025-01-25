package org.Model;

public class Client extends User {

	public Ticket[] tickets;

	public Client(int id, String login, String email, UserRole role) {
		super(id, login, email, role);
	}

	public Client() {
	}
}