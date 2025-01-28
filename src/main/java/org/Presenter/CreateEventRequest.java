package org.Presenter;

import java.util.ArrayList;
import java.util.List;

public class CreateEventRequest {
	public CreateEventRequest (String startDate, String endDate, String place, String organizer, int userId, ArrayList<TicketPoolRequest> ticketPools, ArrayList<Integer> blackList) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.place = place;
		this.organizer = organizer;
		this.userId = userId;
		this.ticketPools = ticketPools;
		this.blackList = blackList;
	}

	public String startDate;
	public String endDate;
	public String place;
	public String organizer;
	public int userId;
	public ArrayList<TicketPoolRequest> ticketPools;

	public List<Integer> blackList;

}