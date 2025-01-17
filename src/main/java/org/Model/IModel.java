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
	 * @param ticket
	 */
	void AddTicket(Ticket ticket);

	/**
	 * 
	 * @param ticket
	 */
	void AddTicketForResell(TicketToReSell ticket);

}