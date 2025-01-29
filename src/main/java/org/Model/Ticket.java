package org.Model;

import java.time.LocalDate;

public class Ticket {

	public Ticket(String id, TicketPool ticketPool, LocalDate sellStartDate, LocalDate saleEndDate, String location, String organizer, float price, boolean isForResell, int userId) {
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
	public LocalDate sellStartDate;
	public LocalDate saleEndDate;
	public String location;
	public String organizer;
	public float price;
	public boolean isForResell;
	public int userId;
}