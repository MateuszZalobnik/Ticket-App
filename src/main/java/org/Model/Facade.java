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

    public Connection getConnection() {
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
    public void AddUser(User user) {
        String procedureCall = "CALL public.rejestracja_uzytkownika(?, ?, ?, ?)";

        try (Connection connection = getConnection();
             CallableStatement callableStatement = connection.prepareCall(procedureCall)) {

            callableStatement.setString(1, user.login);
            callableStatement.setString(2, user.password);
            callableStatement.setString(3, user.email);
            callableStatement.setInt(4, user.role.ordinal());

            callableStatement.execute();

            System.out.println("Użytkownik został pomyślnie dodany.");
        } catch (SQLException e) {
            System.err.println("Błąd podczas dodawania użytkownika: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @Override
    public User GetUserByCredentials(String login, String password) {
        String functionCall = "SELECT public.logowanie_uzytkownika(?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(functionCall)) {

            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int userId = resultSet.getInt(1);

                String query = "SELECT id, login, email, rola FROM public.uzytkownicy WHERE id = ?";
                try (PreparedStatement userStatement = connection.prepareStatement(query)) {
                    userStatement.setInt(1, userId);
                    ResultSet userResult = userStatement.executeQuery();

                    if (userResult.next()) {
                        int id = userResult.getInt("id");
                        String userLogin = userResult.getString("login");
                        String email = userResult.getString("email");
                        int role = userResult.getInt("rola");
                        var roleEnum = UserRole.values()[role];

                        return new User(id, userLogin, email, roleEnum);
                    }
                }
            } else {
                System.out.println("Nie znaleziono użytkownika.");
            }
        } catch (SQLException e) {
            System.err.println("Błąd podczas logowania: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }


    @Override
    public void AddEvent(Event event) {
        String eventInsertQuery = "INSERT INTO public.wydarzenia (datawydarzeniastart, datawydarzeniakoniec, miejsce, organizator, uzytkownicyid) VALUES (?, ?, ?, ?, ?) RETURNING id";
        String ticketPoolInsertQuery = "INSERT INTO public.pule_biletow (iloscbiletow, cenabiletu, datarozpoczeciasprzedazy, datazakonczeniesprzedazy, rozpoczeciesprzedazypozakonczeniupoprzedniejpuli, numerpuli, wydarzeniaid) VALUES (?, ?, ?, ?, ?, ?, ?)";

        Connection connection = getConnection();
        try {
            connection.setAutoCommit(false);

            int eventId;
            try (PreparedStatement eventStatement = connection.prepareStatement(eventInsertQuery)) {
                eventStatement.setDate(1, java.sql.Date.valueOf(event.sellStartDate));
                eventStatement.setDate(2, java.sql.Date.valueOf(event.saleEndDate));
                eventStatement.setString(3, event.location);
                eventStatement.setString(4, event.organizer);
                eventStatement.setInt(5, event.userId);

                ResultSet eventResultSet = eventStatement.executeQuery();
                if (eventResultSet.next()) {
                    eventId = eventResultSet.getInt(1);
                } else {
                    throw new SQLException("Nie udało się utworzyć wydarzenia.");
                }
            }

            // Wstawienie powiązanych pul biletów
            if (event.ticketPools != null) {
                try (PreparedStatement ticketPoolStatement = connection.prepareStatement(ticketPoolInsertQuery)) {
                    for (TicketPool pool : event.ticketPools) {
                        ticketPoolStatement.setInt(1, pool.initialNumberOfTickets);
                        ticketPoolStatement.setFloat(2, pool.price);
                        ticketPoolStatement.setDate(3, java.sql.Date.valueOf(pool.sellStartDate));
                        ticketPoolStatement.setDate(4, java.sql.Date.valueOf(pool.sellEndDate));
                        ticketPoolStatement.setBoolean(5, pool.shouldStartWhenPreviousPoolEnd);
                        ticketPoolStatement.setInt(6, pool.poolNumber);
                        ticketPoolStatement.setInt(7, eventId);

                        ticketPoolStatement.executeUpdate();
                    }
                }
            }

            connection.commit(); // Zatwierdzenie transakcji
            System.out.println("Wydarzenie i pule biletów zostały pomyślnie dodane.");
        } catch (SQLException e) {
            System.err.println("Błąd podczas dodawania wydarzenia: " + e.getMessage());
            e.printStackTrace();
            try {
                connection.rollback(); // Wycofanie transakcji w razie błędu
            } catch (SQLException rollbackException) {
                System.err.println("Błąd podczas wycofywania transakcji: " + rollbackException.getMessage());
            }
        }
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
                    ticketPool.initialNumberOfTickets = resultSet.getInt("iloscbiletow");
                    ticketPool.price = resultSet.getFloat("cenabiletu");
                    ticketPool.sellStartDate = resultSet.getString("datarozpoczeciasprzedazy");
                    ticketPool.sellEndDate = resultSet.getString("datazakonczeniesprzedazy");
                    ticketPool.shouldStartWhenPreviousPoolEnd = resultSet.getBoolean("rozpoczeciesprzedazypozakonczeniupoprzedniejpuli");
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
                    ticketPool.id = resultSet.getInt("pool_id");
                    ticketPool.initialNumberOfTickets = resultSet.getInt("iloscbiletow");
                    ticketPool.price = resultSet.getFloat("cenabiletu");
                    ticketPool.sellStartDate = resultSet.getString("datarozpoczeciasprzedazy");
                    ticketPool.sellEndDate = resultSet.getString("datazakonczeniesprzedazy");
                    ticketPool.shouldStartWhenPreviousPoolEnd = resultSet.getBoolean("rozpoczeciesprzedazypozakonczeniupoprzedniejpuli");
                    ticketPool.poolNumber = resultSet.getInt("numerpuli");

                    event.addTicketPool(ticketPool);
                }

                events.addAll(eventMap.values());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("Znaleziono " + events.size() + " wydarzeń.");
        return events.toArray(new Event[0]);
    }

    @Override
    public Ticket[] GetTicketsById(Integer userId) {
        String sql = "SELECT b.id, b.pule_biletowid, b.uzytkownicyid, " +
                "p.iloscbiletow, p.cenabiletu, p.datarozpoczeciasprzedazy, p.datazakonczeniesprzedazy, " +
                "p.rozpoczeciesprzedazypozakonczeniupoprzedniejpuli, p.numerpuli, " +
                "e.datawydarzeniastart, e.datawydarzeniakoniec, e.miejsce, e.organizator " +
                "FROM public.bilety b " +
                "JOIN public.pule_biletow p ON b.pule_biletowid = p.id " +
                "JOIN public.wydarzenia e ON p.wydarzeniaid = e.id " +
                "WHERE e.datawydarzeniakoniec > now()";

        if (userId != null) {
            sql += " AND b.uzytkownicyid = ?";
        }

        List<Ticket> tickets = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            if (userId != null) {
                statement.setInt(1, userId);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String ticketId = resultSet.getString("id");
                    int poolId = resultSet.getInt("pule_biletowid");
                    String sellStartDate = resultSet.getString("datarozpoczeciasprzedazy");
                    String saleEndDate = resultSet.getString("datazakonczeniesprzedazy");
                    String location = resultSet.getString("miejsce");
                    String organizer = resultSet.getString("organizator");
                    float price = resultSet.getFloat("cenabiletu");

                    Ticket ticket = new Ticket(ticketId, poolId, sellStartDate, saleEndDate, location, organizer, price);
                    tickets.add(ticket);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tickets.toArray(new Ticket[0]);
    }

    @Override
    public Ticket[] GetHistoricalTicketsById(Integer userId) {
        String sql = "SELECT b.id, b.pule_biletowid, b.uzytkownicyid, " +
                "p.iloscbiletow, p.cenabiletu, p.datarozpoczeciasprzedazy, p.datazakonczeniesprzedazy, " +
                "p.rozpoczeciesprzedazypozakonczeniupoprzedniejpuli, p.numerpuli, " +
                "e.datawydarzeniastart, e.datawydarzeniakoniec, e.miejsce, e.organizator " +
                "FROM public.bilety b " +
                "JOIN public.pule_biletow p ON b.pule_biletowid = p.id " +
                "JOIN public.wydarzenia e ON p.wydarzeniaid = e.id " +
                "WHERE e.datawydarzeniakoniec < now()";

        if (userId != null) {
            sql += " AND b.uzytkownicyid = ?";
        }

        List<Ticket> tickets = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            if (userId != null) {
                statement.setInt(1, userId);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String ticketId = resultSet.getString("id");
                    int poolId = resultSet.getInt("pule_biletowid");
                    String sellStartDate = resultSet.getString("datarozpoczeciasprzedazy");
                    String saleEndDate = resultSet.getString("datazakonczeniesprzedazy");
                    String location = resultSet.getString("miejsce");
                    String organizer = resultSet.getString("organizator");
                    float price = resultSet.getFloat("cenabiletu");

                    Ticket ticket = new Ticket(ticketId, poolId, sellStartDate, saleEndDate, location, organizer, price);
                    tickets.add(ticket);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tickets.toArray(new Ticket[0]);
    }


    @Override
    public void UpdateEvent(Event Event) {

    }

    @Override
    public void AddOpinion(Opinion opinion) {

    }

    @Override
    public void AddTicket(int ticketPoolId, int userId) {
        String procedureCall = "CALL public.stworz_bilet(?, ?)";

        try (Connection connection = getConnection();
             CallableStatement callableStatement = connection.prepareCall(procedureCall)) {

            callableStatement.setInt(1, ticketPoolId);
            callableStatement.setInt(2, userId);

            callableStatement.execute();
            System.out.println("Bilet został pomyślnie dodany.");
        } catch (SQLException e) {
            System.err.println("Błąd podczas dodawania biletu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void AddTicketForResell(TicketToReSell ticket) {

    }
}