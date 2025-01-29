package org.Presenter;

import java.time.LocalDate;

public class TicketPoolRequest {

	public TicketPoolRequest(int amountOfTickets, float price, LocalDate sellStartDate, LocalDate sellEndDate, boolean ShouldStartWhenPreviousPoolEnd, int number) {
		this.amountOfTickets = amountOfTickets;
		this.price = price;
		this.sellStartDate = sellStartDate;
		this.sellEndDate = sellEndDate;
		this.ShouldStartWhenPreviousPoolEnd = ShouldStartWhenPreviousPoolEnd;
		this.number = number;
	}
	public int amountOfTickets;
	public float price;
	public LocalDate sellStartDate;
	public LocalDate sellEndDate;
	public boolean ShouldStartWhenPreviousPoolEnd;
	public int number;

}