package org.Model;

public class Ticket {

	public Ticket(String id, int ticketPool, String sellStartDate, String saleEndDate, String location, String organizer, float price, boolean isForResell) {
		this.id = id;
		this.ticketPool = ticketPool;
		this.sellStartDate = sellStartDate;
		this.saleEndDate = saleEndDate;
		this.location = location;
		this.organizer = organizer;
		this.price = price;
		this.isForResell = isForResell;
	}

	public String id;
	public int ticketPool;
	public String sellStartDate;
	public String saleEndDate;
	public String location;
	public String organizer;
	public float price;
	public boolean isForResell;
}