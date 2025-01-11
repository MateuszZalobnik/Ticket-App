package org.Presenter;

import org.Model.*;

public class PresenterFacade implements IPresenter {
    @Override
    public void CreateAccount(String login, String email, String password, int role) {
        Facade facade = new Facade();
        if (role == 0){
            Client client = new Client();
            facade.AddUser(client);
            //powinno otiweraz widok dla klineta
        }else if (role == 1){
            Organizer organizer = new Organizer();
            facade.AddUser(organizer);
            //powinno otiweraz widok dla organizartora
        }
    }

    @Override
    public User LogIn(String login, String password) {
        Facade facade = new Facade();
        User user = new User();
        // TODO sprawdzic czy login w bazie a hasło poporawne
        if (facade.GetUserByCredentials(login, password) != null){
            user = facade.GetUserByCredentials(login, password);
            if (user.role == 0){
                Client client = new Client();
                //powinno otiweraz widok dla klineta
                return client;
            } else if (user.role == 1) {
                Organizer organizer = new Organizer();
                //powinno otiweraz widok dla organizartora
                return organizer;
            }
        }else {
            // nie znalenon użytkowania w bazie lub hasło jest nie te
            return null;
        }
        return user;
    }

    @Override
    public Event[] GetEventsById(Integer id) {
        var model = new Facade();
        return model.GetEventsById(id);
    }

    @Override
    public Event[] GetHistoricalEventsById(Integer id) {
        var model = new Facade();
        return model.GetHistoricalEventsById(id);
    }

    @Override
    public org.Model.Event[] CreateEvent(CreateEventRequest request) {
        return new Event[0];
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