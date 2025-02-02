package org.Presenter;

import org.Model.*;

import java.util.ArrayList;
import java.util.UUID;

public class PresenterFacade implements IPresenter {
    @Override
    public void CreateAccount(String login, String email, String password, int role) {
        Facade facade = new Facade();
        if (role == 0) {
            Client client = new Client();
            client.email = email;
            client.login = login;
            client.password = password;
            client.role = UserRole.Client;
            facade.AddUser(client);
        } else if (role == 1) {
            Organizer organizer = new Organizer();
            organizer.email = email;
            organizer.login = login;
            organizer.password = password;
            organizer.role = UserRole.Organizer;
            facade.AddUser(organizer);
        }
    }

    @Override
    public User LogIn(String login, String password) {
        Facade facade = new Facade();
        User user = new User();
        user.login = login;
        user.password = password;

        if (facade.GetUserByCredentials(login, password) != null) {
            user = facade.GetUserByCredentials(login, password);
            if (user.role == UserRole.Client) {
                return new Client(user.id, user.login, user.email, user.role);
            } else if (user.role == UserRole.Organizer) {
                return new Organizer(user.id, user.login, user.email, user.role);
            }
        } else {
            // nie znalenon użytkowania w bazie lub hasło jest nie te
            return null;
        }
        return user;
    }

    @Override
    public Event[] GetAvailableEventsById(int userId) {
        IModel model = new Facade();
        return model.GetAvailableEventsById(userId);
    }

    @Override
    public Event[] GetEventsById(Integer id) {
        var model = new Facade();
        return model.GetEventsById(id);
    }

    @Override
    public void CreateEvent(CreateEventRequest request) {
        IModel model = new Facade();

        var event = new Event();
        event.sellStartDate = request.startDate;
        event.saleEndDate = request.endDate;
        event.organizer = request.organizer;
        event.location = request.place;
        event.userId = request.userId;

        for (var pool : request.ticketPools) {
            var ticketPool = new TicketPool(
                    -1,
                    pool.amountOfTickets,
                    0,
                    pool.price,
                    pool.sellStartDate,
                    pool.sellEndDate,
                    false,
                    pool.number,
                    -1);

            event.addTicketPool(ticketPool);
        }

        event.blockedIds = request.blackList.toArray(new Integer[0]);
        model.AddEvent(event);
    }

    public Event[] GetHistoricalEventsById(Integer id) {
        var model = new Facade();
        return model.GetHistoricalEventsById(id);
    }


    @Override
    public void AddOpinion(int userId, int rate, String comment, int eventId) {
        IModel model = new Facade();
        var opinion = new Opinion();
        opinion.userId = userId;
        opinion.opinion = rate;
        opinion.comment = comment;
        opinion.eventId = eventId;
        model.AddOpinion(opinion);
    }

    @Override
    public void ResellTicket(String ticketId, float price) {
        IModel model = new Facade();

        TicketToReSell ticketToReSell = new TicketToReSell();
        ticketToReSell.ticketId = ticketId;
        ticketToReSell.price = price;
        model.AddTicketForResell(ticketToReSell);
    }

    @Override
    public Ticket[] GetTickets(int userId) {
        IModel model = new Facade();
        return model.GetTicketsById(userId);
    }

    @Override
    public Ticket[] GetHistoricalTickets(int userId) {
        IModel model = new Facade();
        return model.GetHistoricalTicketsById(userId);
    }

    @Override
    public Ticket[] GetTicketsForResell(int userId) {
        IModel model = new Facade();
        return model.GetTicketForSell(userId);
    }

    @Override
    public void BuyTicket(int ticketPoolId, int userId) {
        IModel model = new Facade();
        model.AddTicket(ticketPoolId, userId);
    }

    @Override
    public void BuyTicketFromResell(String ticketId, int newOwnerid) {
        IModel model = new Facade();
        model.BuyTicketFromResell(UUID.fromString(ticketId), newOwnerid);
    }

    @Override
    public ArrayList<User> SearchUsersInDataBase(String login) {
        IModel model = new Facade();
        return model.SearchUsersInDataBase(login);

    }
    @Override
    public ArrayList<User> AddBlockedUsers(ArrayList<User> blockedList){
        return blockedList;
    }

    @Override
    public EventDetails GetEventDetails(int eventId) {
        IModel model = new Facade();
        return model.GetEventDetailsById(eventId);
    }

    public void DeleteTicketFromResll(String ticketId) {
        IModel model = new Facade();
        model.DeleteTicketFromResell(UUID.fromString(ticketId));
    }
}