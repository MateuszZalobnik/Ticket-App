package org.Model;

public class TicketPool {

	public TicketPool() {
	}
	public TicketPool(int id, int numberOfTickets, float price, String sellStartDate, String saleEndDate, boolean shouldStartWhenPreviousPoolEnd, int poolNumber) {
		this.id = id;
		this.numberOfTickets = numberOfTickets;
		this.price = price;
		this.sellStartDate = sellStartDate;
		this.sellEndDate = saleEndDate;
		this.shouldStartWhenPreviousPoolEnd = shouldStartWhenPreviousPoolEnd;
		this.poolNumber = poolNumber;
	}
	public int id;
	public int numberOfTickets;
	public float price;
	public String sellStartDate;
	public String sellEndDate;
	public boolean shouldStartWhenPreviousPoolEnd;
	public int poolNumber;
}