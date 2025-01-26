package org.Model;

public class TicketPool {

	public TicketPool() {
	}
	public TicketPool(int id, int initialNumberOfTickets, int numberOfSoldTickets, float price, String sellStartDate, String saleEndDate, boolean shouldStartWhenPreviousPoolEnd, int poolNumber) {
		this.id = id;
		this.initialNumberOfTickets = initialNumberOfTickets;
		this.numberOfSoldTickets = numberOfSoldTickets;
		this.price = price;
		this.sellStartDate = sellStartDate;
		this.sellEndDate = saleEndDate;
		this.shouldStartWhenPreviousPoolEnd = shouldStartWhenPreviousPoolEnd;
		this.poolNumber = poolNumber;
	}
	public int id;
	public int initialNumberOfTickets;
	public int numberOfSoldTickets;
	public float price;
	public String sellStartDate;
	public String sellEndDate;
	public boolean shouldStartWhenPreviousPoolEnd;
	public int poolNumber;
}