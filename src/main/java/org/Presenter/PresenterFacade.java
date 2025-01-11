package org.Presenter;

import org.Model.*;

public class PresenterFacade implements IPresenter {
    @Override
    public void CreateAccount(String login, String email, String password, int role) {
        
    }

    @Override
    public User SignIn(String login, String password) {
        return null;
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
        // itd

        model.AddEvent(event);
    }

    @Override
    public void AddOpinion(int userId, int rate, String comment, int eventId) {

    }

    @Override
    public void ResellTicket(String ticketId, float price) {

    }

    @Override
    public Ticket[] GetTickets(int userId) {
        return new Ticket[0];
    }

    @Override
    public TicketToReSell[] GetTicketsForResell() {
        return new TicketToReSell[0];
    }

    @Override
    public void BuyTicket(int ticketId) {

    }

    @Override
    public void BuyTicketFromResell(int ticketId) {

    }
}