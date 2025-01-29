package org.Model;

public class Ticket {

	public Ticket(String id, TicketPool ticketPool, String sellStartDate, String saleEndDate, String location, String organizer, float price, boolean isForResell, int userId) {
		this.id = id;
		this.ticketPool = ticketPool;
		this.sellStartDate = sellStartDate;
		this.saleEndDate = saleEndDate;
		this.location = location;
		this.organizer = organizer;
		this.price = price;
		this.isForResell = isForResell;
		this.userId = userId;
	}

	public String id;
	public TicketPool ticketPool;
	public String sellStartDate;
	public String saleEndDate;
	public String location;
	public String organizer;
	public float price;
	public boolean isForResell;
	public int userId;
}