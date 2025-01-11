package org.Presenter;

import org.Model.Event;
import org.Model.Ticket;
import org.Model.TicketToReSell;
import org.Model.User;

public interface IPresenter {

	/**
	 * 
	 * @param login
	 * @param email
	 * @param password
	 * @param role
	 */
	void CreateAccount(String login, String email, String password, int role);

	/**
	 * 
	 * @param login
	 * @param password
	 */
	User LogIn(String login, String password);

	/**
	 * 
	 * @param id
	 */
	Event[] GetEventsById(Integer id);

	/**
	 *
	 * @param id
	 */
	Event[] GetHistoricalEventsById(Integer id);

	/**
	 * 
	 * @param request
	 */
	void CreateEvent(CreateEventRequest request);

	/**
	 * 
	 * @param userId
	 * @param rate
	 * @param comment
	 * @param eventId
	 */
	void AddOpinion(int userId, int rate, String comment, int eventId);

	/**
	 * 
	 * @param ticketId
	 * @param price
	 */
	void ResellTicket(String ticketId, float price);

	/**
	 * 
	 * @param userId
	 */
	Ticket[] GetTickets(int userId);

	TicketToReSell[] GetTicketsForResell();

	/**
	 * 
	 * @param ticketId
	 */
	void BuyTicket(int ticketId);

	/**
	 * 
	 * @param ticketId
	 */
	void BuyTicketFromResell(int ticketId);

}