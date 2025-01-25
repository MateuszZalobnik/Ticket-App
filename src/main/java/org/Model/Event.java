package org.Model;

import java.util.Arrays;

public class Event {
	public String sellStartDate;
	public String saleEndDate;
	public String location;
	public String organizer;
	public int userId;
	public TicketPool[] ticketPools;

	public void addTicketPool(TicketPool ticketPool) {
		if (ticketPools == null) {
			ticketPools = new TicketPool[] { ticketPool };  // Initialize array with the first ticket pool
		} else {
			ticketPools = Arrays.copyOf(ticketPools, ticketPools.length + 1);
			ticketPools[ticketPools.length - 1] = ticketPool;
		}
	}
}