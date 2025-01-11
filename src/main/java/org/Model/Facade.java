package org.Model;

import java.sql.*;
import java.util.*;

public class Facade implements IModel {

    final String connectionString = "jdbc:postgresql://localhost:5432/test2";
    private Properties props;


    public Facade() {
        props = new Properties();
        props.setProperty("user", "app_user");
        props.setProperty("password", "app");

    }

    private Connection getConnection() {
        try {
            return DriverManager.getConnection(connectionString, props);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void closeConnection(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void AddUser() {
    }

    @Override
    public User GetUserByCredentials(String login, String password) {
        return null;
    }

    @Override
    public void AddEvent(Event event) {

    }

    @Override
    public Event[] GetHistoricalEventsById(Integer userId) {
        String sql = "SELECT e.id, e.datawydarzeniastart, e.datawydarzeniakoniec, e.miejsce, e.organizator, e.uzytkownicyid, " +
                "p.id AS pool_id, p.iloscbiletow, p.cenabiletu, p.datarozpoczeciasprzedazy, p.datazakonczeniesprzedazy, " +
                "p.rozpoczeciesprzedazypozakonczeniupoprzedniejpuli, p.numerpuli " +
                "FROM public.wydarzenia e " +
                "LEFT JOIN public.pule_biletow p ON e.id = p.wydarzeniaid" +
                " WHERE e.datawydarzeniakoniec < now()";

        if (userId != null) {
            sql += " AND e.uzytkownicyid = ?";
        }

        List<Event> events = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            if (userId != null) {
                statement.setInt(1, userId);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                Map<Integer, Event> eventMap = new HashMap<>();

                while (resultSet.next()) {
                    int eventId = resultSet.getInt("id");
                    Event event = eventMap.get(eventId);

                    if (event == null) {
                        event = new Event();
                        event.sellStartDate = resultSet.getString("datawydarzeniastart");
                        event.saleEndDate = resultSet.getString("datawydarzeniakoniec");
                        event.location = resultSet.getString("miejsce");
                        event.organizer = resultSet.getString("organizator");

                        // Add the event to the map
                        eventMap.put(eventId, event);
                    }

                    TicketPool ticketPool = new TicketPool();
                    ticketPool.id = resultSet.getInt("id");
                    ticketPool.numberOfTickets = resultSet.getInt("iloscbiletow");
                    ticketPool.price = resultSet.getFloat("cenabiletu");
                    ticketPool.sellStartDate = resultSet.getString("datarozpoczeciasprzedazy");
                    ticketPool.saleEndDate = resultSet.getString("datazakonczeniesprzedazy");
                    ticketPool.shouldStartWhenPrviousPoolEnd = resultSet.getBoolean("rozpoczeciesprzedazypozakonczeniupoprzedniejpuli");
                    ticketPool.poolNumber = resultSet.getInt("numerpuli");

                    event.addTicketPool(ticketPool);
                }

                events.addAll(eventMap.values());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return events.toArray(new Event[0]);
    }

    @Override
    public Event[] GetEventsById(Integer userId) {
        String sql = "SELECT e.id, e.datawydarzeniastart, e.datawydarzeniakoniec, e.miejsce, e.organizator, e.uzytkownicyid, " +
                "p.id AS pool_id, p.iloscbiletow, p.cenabiletu, p.datarozpoczeciasprzedazy, p.datazakonczeniesprzedazy, " +
                "p.rozpoczeciesprzedazypozakonczeniupoprzedniejpuli, p.numerpuli " +
                "FROM public.wydarzenia e " +
                "LEFT JOIN public.pule_biletow p ON e.id = p.wydarzeniaid" +
                " WHERE e.datawydarzeniakoniec > now()";

        if (userId != null) {
            sql += " AND e.uzytkownicyid = ?";
        }

        List<Event> events = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            if (userId != null) {
                statement.setInt(1, userId);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                Map<Integer, Event> eventMap = new HashMap<>();

                while (resultSet.next()) {
                    int eventId = resultSet.getInt("id");
                    Event event = eventMap.get(eventId);

                    if (event == null) {
                        event = new Event();
                        event.sellStartDate = resultSet.getString("datawydarzeniastart");
                        event.saleEndDate = resultSet.getString("datawydarzeniakoniec");
                        event.location = resultSet.getString("miejsce");
                        event.organizer = resultSet.getString("organizator");

                        // Add the event to the map
                        eventMap.put(eventId, event);
                    }

                    TicketPool ticketPool = new TicketPool();
                    ticketPool.id = resultSet.getInt("id");
                    ticketPool.numberOfTickets = resultSet.getInt("iloscbiletow");
                    ticketPool.price = resultSet.getFloat("cenabiletu");
                    ticketPool.sellStartDate = resultSet.getString("datarozpoczeciasprzedazy");
                    ticketPool.saleEndDate = resultSet.getString("datazakonczeniesprzedazy");
                    ticketPool.shouldStartWhenPrviousPoolEnd = resultSet.getBoolean("rozpoczeciesprzedazypozakonczeniupoprzedniejpuli");
                    ticketPool.poolNumber = resultSet.getInt("numerpuli");

                    event.addTicketPool(ticketPool);
                }

                events.addAll(eventMap.values());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return events.toArray(new Event[0]);
    }



    @Override
    public void UpdateEvent(Event Event) {

    }

    @Override
    public void AddOpinion(Opinion opinion) {

    }

    @Override
    public void AddTicket(Ticket ticket) {

    }

    @Override
    public void AddTicketForResell(TicketToReSell ticket) {

    }
}