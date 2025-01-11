package org.Presenter;

import java.util.List;

public class CreateEventRequest {

	public String startDate;
	public String endDate;
	public String place;
	public String organizer;
	public TicketPoolRequest[] ticketPools;

	public List<String> BlackList;

}