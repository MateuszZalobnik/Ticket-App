package org.Model;

import java.sql.SQLException;

public interface IModel {

	void AddUser(User user) throws SQLException;

	/**
	 * 
	 * @param login
	 * @param password
	 */
	User GetUserByCredentials(String login, String password);

	/**
	 * 
	 * @param event
	 */
	void AddEvent(Event event);
	/**
	 *
	 * @param userId
	 */
	Event[] GetHistoricalEventsById(Integer userId);

	/**
	 * 
	 * @param userId
	 */
	Event[] GetEventsById(Integer userId);

	/**
	 *
	 * @param userId
	 */
	Ticket[] GetTicketsById(Integer userId);

	/**
	 *
	 * @param userId
	 */
	Ticket[] GetHistoricalTicketsById(Integer userId);

	/**
	 * 
	 * @param Event
	 */
	void UpdateEvent(Event Event);

	/**
	 * 
	 * @param opinion
	 */
	void AddOpinion(Opinion opinion);

	/**
	 * 
	 * @param ticketPoolId
	 * @param userId
	 */
	void AddTicket(int ticketPoolId, int userId);

	/**
	 * 
	 * @param ticket
	 */
	void AddTicketForResell(TicketToReSell ticket);

	Ticket[] GetTicketForSell();

	void UpdateTicket(String ticketId, boolean isForResell);

}