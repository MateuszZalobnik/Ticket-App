package org.Model;

public class Ticket {

	public Ticket(String id, int ticketPool, String sellStartDate, String saleEndDate, String location, String organizer, float price) {
		this.id = id;
		this.ticketPool = ticketPool;
		this.sellStartDate = sellStartDate;
		this.saleEndDate = saleEndDate;
		this.location = location;
		this.organizer = organizer;
		this.price = price;
	}

	public String id;
	public int ticketPool;
	public String sellStartDate;
	public String saleEndDate;
	public String location;
	public String organizer;
	public float price;
}