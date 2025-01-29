package org.Model;

import java.time.LocalDate;

public class TicketPool {

	public TicketPool() {
	}
	public TicketPool(int id, int initialNumberOfTickets, int numberOfSoldTickets, float price, LocalDate sellStartDate, LocalDate saleEndDate, boolean shouldStartWhenPreviousPoolEnd, int poolNumber, int eventId) {
		this.id = id;
		this.initialNumberOfTickets = initialNumberOfTickets;
		this.numberOfSoldTickets = numberOfSoldTickets;
		this.price = price;
		this.sellStartDate = sellStartDate;
		this.sellEndDate = saleEndDate;
		this.shouldStartWhenPreviousPoolEnd = shouldStartWhenPreviousPoolEnd;
		this.poolNumber = poolNumber;
		this.eventId = eventId;
	}
	public int id;
	public int initialNumberOfTickets;
	public int numberOfSoldTickets;
	public float price;
	public LocalDate sellStartDate;
	public LocalDate sellEndDate;
	public boolean shouldStartWhenPreviousPoolEnd;
	public int poolNumber;
	public int eventId;
}