package org.Presenter;

public class TicketPoolRequest {

	public TicketPoolRequest(int amountOfTickets, float price, String sellStartDate, String sellEndDate, boolean ShouldStartWhenPreviousPoolEnd, int number) {
		this.amountOfTickets = amountOfTickets;
		this.price = price;
		this.sellStartDate = sellStartDate;
		this.sellEndDate = sellEndDate;
		this.ShouldStartWhenPreviousPoolEnd = ShouldStartWhenPreviousPoolEnd;
		this.number = number;
	}
	public int amountOfTickets;
	public float price;
	public String sellStartDate;
	public String sellEndDate;
	public boolean ShouldStartWhenPreviousPoolEnd;
	public int number;

}