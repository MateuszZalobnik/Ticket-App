package org.Presenter;

import org.Model.Event;
import org.Model.EventDetails;
import org.Model.Ticket;
import org.Model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
	 * @param userId
	 */
	Event[] GetAvailableEventsById(int userId);

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

	/**
	 *
	 * @param userId
	 */
	Ticket[] GetHistoricalTickets(int userId);

	Ticket[] GetTicketsForResell(int userId);

	/**
	 * 
	 * @param ticketPoolId
	 */
	void BuyTicket(int ticketPoolId, int userId);

	/**
	 * 
	 * @param ticketId
	 */
	void BuyTicketFromResell(String ticketId, int newOwnerid);

	ArrayList<User> SearchUsersInDataBase(String login);


	ArrayList<User> AddBlockedUsers(ArrayList<User> blockedList);

	EventDetails GetEventDetails(int eventId);
	void DeleteTicketFromResll(String ticketId);
}