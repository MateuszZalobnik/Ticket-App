package org.Model;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class Facade implements IModel {

    final String connectionString = "jdbc:postgresql://localhost:5432/ticket-app";
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
        String blockedUserInsertQuery = "INSERT INTO public.zablokowani_uczestnicy (uzytkownicyid, wydarzeniaid) VALUES (?, ?)";

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

            // Dodanie zablokowanych użytkowników
            if (event.blockedIds != null) {
                try (PreparedStatement blockedUserStatement = connection.prepareStatement(blockedUserInsertQuery)) {
                    for (int blockedUserId : event.blockedIds) {
                        blockedUserStatement.setInt(1, blockedUserId);
                        blockedUserStatement.setInt(2, eventId);

                        blockedUserStatement.executeUpdate();
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
    public Event[] GetHistoricalEventsById(int userId) {
        String sql = "SELECT e.id, e.datawydarzeniastart, e.datawydarzeniakoniec, e.miejsce, e.organizator, e.uzytkownicyid, " +
                "p.id AS pool_id, p.iloscbiletow, p.cenabiletu, p.datarozpoczeciasprzedazy, p.datazakonczeniesprzedazy, " +
                "p.rozpoczeciesprzedazypozakonczeniupoprzedniejpuli, p.numerpuli " +
                "FROM public.wydarzenia e " +
                "LEFT JOIN public.pule_biletow p ON e.id = p.wydarzeniaid " +
                "WHERE e.datawydarzeniakoniec < now() AND e.uzytkownicyid = ?";

        List<Event> events = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                Map<Integer, Event> eventMap = new HashMap<>();

                while (resultSet.next()) {
                    int eventId = resultSet.getInt("id");
                    Event event = eventMap.get(eventId);

                    if (event == null) {
                        event = new Event();
                        event.id = eventId;
                        event.sellStartDate = LocalDate.parse(resultSet.getString("datawydarzeniastart"));
                        event.saleEndDate = LocalDate.parse(resultSet.getString("datawydarzeniakoniec"));
                        event.location = resultSet.getString("miejsce");
                        event.organizer = resultSet.getString("organizator");
                        event.ticketPools = new TicketPool[]{};

                        // Add the event to the map
                        eventMap.put(eventId, event);
                    }

                    int poolId = resultSet.getInt("pool_id");
                    if (!resultSet.wasNull()) {
                        TicketPool ticketPool = new TicketPool();
                        ticketPool.id = poolId;
                        ticketPool.initialNumberOfTickets = resultSet.getInt("iloscbiletow");
                        ticketPool.price = resultSet.getFloat("cenabiletu");
                        ticketPool.sellStartDate = LocalDate.parse(resultSet.getString("datarozpoczeciasprzedazy"));
                        ticketPool.sellEndDate = LocalDate.parse(resultSet.getString("datazakonczeniesprzedazy"));
                        ticketPool.shouldStartWhenPreviousPoolEnd = resultSet.getBoolean("rozpoczeciesprzedazypozakonczeniupoprzedniejpuli");
                        ticketPool.poolNumber = resultSet.getInt("numerpuli");

                        event.addTicketPool(ticketPool);
                    }
                }

                events.addAll(eventMap.values());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return events.toArray(new Event[0]);
    }

    @Override
    public Event[] GetEventsById(int userId) {
        String sql = "SELECT e.id, e.datawydarzeniastart, e.datawydarzeniakoniec, e.miejsce, e.organizator, e.uzytkownicyid, " +
                "p.id AS pool_id, p.iloscbiletow, p.cenabiletu, p.datarozpoczeciasprzedazy, p.datazakonczeniesprzedazy, " +
                "p.rozpoczeciesprzedazypozakonczeniupoprzedniejpuli, p.numerpuli " +
                "FROM public.wydarzenia e " +
                "LEFT JOIN public.pule_biletow p ON e.id = p.wydarzeniaid " +
                "WHERE e.datawydarzeniakoniec > now() AND e.uzytkownicyid = ?";

        List<Event> events = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                Map<Integer, Event> eventMap = new HashMap<>();

                while (resultSet.next()) {
                    int eventId = resultSet.getInt("id");
                    Event event = eventMap.get(eventId);

                    if (event == null) {
                        event = new Event();
                        event.id = eventId;
                        event.sellStartDate = LocalDate.parse(resultSet.getString("datawydarzeniastart"));
                        event.saleEndDate = LocalDate.parse(resultSet.getString("datawydarzeniakoniec"));
                        event.location = resultSet.getString("miejsce");
                        event.organizer = resultSet.getString("organizator");
                        event.ticketPools = new TicketPool[]{};

                        // Add the event to the map
                        eventMap.put(eventId, event);
                    }

                    int poolId = resultSet.getInt("pool_id");
                    if (!resultSet.wasNull()) {
                        TicketPool ticketPool = new TicketPool();
                        ticketPool.id = poolId;
                        ticketPool.initialNumberOfTickets = resultSet.getInt("iloscbiletow");
                        ticketPool.price = resultSet.getFloat("cenabiletu");
                        ticketPool.sellStartDate = LocalDate.parse(resultSet.getString("datarozpoczeciasprzedazy"));
                        ticketPool.sellEndDate = LocalDate.parse(resultSet.getString("datazakonczeniesprzedazy"));
                        ticketPool.shouldStartWhenPreviousPoolEnd = resultSet.getBoolean("rozpoczeciesprzedazypozakonczeniupoprzedniejpuli");
                        ticketPool.poolNumber = resultSet.getInt("numerpuli");

                        event.addTicketPool(ticketPool);
                    }
                }

                events.addAll(eventMap.values());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return events.toArray(new Event[0]);
    }

    @Override
    public Event[] GetAvailableEventsById(int userId) {
        String sql = "SELECT e.id, e.datawydarzeniastart, e.datawydarzeniakoniec, e.miejsce, e.organizator, e.uzytkownicyid, " +
                "p.id AS pool_id, p.iloscbiletow, p.cenabiletu, p.datarozpoczeciasprzedazy, p.datazakonczeniesprzedazy, " +
                "p.rozpoczeciesprzedazypozakonczeniupoprzedniejpuli, p.numerpuli " +
                "FROM public.wydarzenia e " +
                "LEFT JOIN public.pule_biletow p ON e.id = p.wydarzeniaid " +
                "WHERE e.datawydarzeniakoniec > now() " +
                "AND e.id NOT IN (" +
                "    SELECT z.wydarzeniaid " +
                "    FROM public.zablokowani_uczestnicy z " +
                "    WHERE z.uzytkownicyid = ?" +
                ")";

        List<Event> events = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                Map<Integer, Event> eventMap = new HashMap<>();

                while (resultSet.next()) {
                    int eventId = resultSet.getInt("id");
                    Event event = eventMap.get(eventId);

                    if (event == null) {
                        event = new Event();
                        event.id = eventId;
                        event.sellStartDate = LocalDate.parse(resultSet.getString("datawydarzeniastart"));
                        event.saleEndDate = LocalDate.parse(resultSet.getString("datawydarzeniakoniec"));
                        event.location = resultSet.getString("miejsce");
                        event.organizer = resultSet.getString("organizator");
                        event.ticketPools = new TicketPool[]{};

                        // Dodajemy wydarzenie do mapy
                        eventMap.put(eventId, event);
                    }

                    // Sprawdzamy, czy dane o puli są dostępne
                    int poolId = resultSet.getInt("pool_id");
                    if (!resultSet.wasNull()) {
                        TicketPool ticketPool = new TicketPool();
                        ticketPool.id = poolId;
                        ticketPool.initialNumberOfTickets = resultSet.getInt("iloscbiletow");
                        ticketPool.price = resultSet.getFloat("cenabiletu");
                        ticketPool.sellStartDate = LocalDate.parse(resultSet.getString("datarozpoczeciasprzedazy"));
                        ticketPool.sellEndDate = LocalDate.parse(resultSet.getString("datazakonczeniesprzedazy"));
                        ticketPool.shouldStartWhenPreviousPoolEnd = resultSet.getBoolean("rozpoczeciesprzedazypozakonczeniupoprzedniejpuli");
                        ticketPool.poolNumber = resultSet.getInt("numerpuli");

                        event.addTicketPool(ticketPool);
                    }
                }

                events.addAll(eventMap.values());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return events.toArray(new Event[0]);
    }


    @Override
    public Ticket[] GetTicketsById(Integer userId) {
        String sql = "SELECT b.id, b.pule_biletowid, b.uzytkownicyid, " +
                "p.id AS pool_id, p.iloscbiletow, " +
                "(SELECT COUNT(*) FROM public.bilety WHERE pule_biletowid = p.id) AS liczba_sprzedanych_biletow, " +
                "p.cenabiletu, p.datarozpoczeciasprzedazy, p.datazakonczeniesprzedazy, " +
                "p.rozpoczeciesprzedazypozakonczeniupoprzedniejpuli, p.numerpuli, " +
                "e.datawydarzeniastart, e.datawydarzeniakoniec, e.miejsce, e.organizator, e.id AS wydarzeniaid " +
                "FROM public.bilety b " +
                "JOIN public.pule_biletow p ON b.pule_biletowid = p.id " +
                "JOIN public.wydarzenia e ON p.wydarzeniaid = e.id " +
                "WHERE e.datawydarzeniakoniec > now() " +
                "AND b.id NOT IN (SELECT biletyid FROM public.bilety_do_odsprzedania)";

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
                    int poolId = resultSet.getInt("pool_id");
                    int initialNumberOfTickets = resultSet.getInt("iloscbiletow");
                    int numberOfSoldTickets = resultSet.getInt("liczba_sprzedanych_biletow");
                    float price = resultSet.getFloat("cenabiletu");
                    LocalDate sellStartDate = LocalDate.parse(resultSet.getString("datarozpoczeciasprzedazy"));
                    LocalDate saleEndDate = LocalDate.parse(resultSet.getString("datazakonczeniesprzedazy"));
                    boolean shouldStartWhenPreviousPoolEnd = resultSet.getBoolean("rozpoczeciesprzedazypozakonczeniupoprzedniejpuli");
                    int poolNumber = resultSet.getInt("numerpuli");
                    int eventId = resultSet.getInt("wydarzeniaid");

                    TicketPool ticketPool = new TicketPool(
                            poolId,
                            initialNumberOfTickets,
                            numberOfSoldTickets,
                            price,
                            sellStartDate,
                            saleEndDate,
                            shouldStartWhenPreviousPoolEnd,
                            poolNumber,
                            eventId
                    );

                    String ticketId = resultSet.getString("id");
                    String location = resultSet.getString("miejsce");
                    String organizer = resultSet.getString("organizator");
                    LocalDate eventStartDate = LocalDate.parse(resultSet.getString("datawydarzeniastart"));
                    LocalDate eventEndDate = LocalDate.parse(resultSet.getString("datawydarzeniakoniec"));

                    Ticket ticket = new Ticket(ticketId, ticketPool, eventStartDate, eventEndDate, location, organizer, price, false);

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
                "p.id AS pool_id, p.iloscbiletow, " +
                "(SELECT COUNT(*) FROM public.bilety WHERE pule_biletowid = p.id) AS liczba_sprzedanych_biletow, " +
                "p.cenabiletu, p.datarozpoczeciasprzedazy, p.datazakonczeniesprzedazy, " +
                "p.rozpoczeciesprzedazypozakonczeniupoprzedniejpuli, p.numerpuli, " +
                "e.datawydarzeniastart, e.datawydarzeniakoniec, e.miejsce, e.organizator, e.id AS wydarzeniaid " +
                "FROM public.bilety b " +
                "JOIN public.pule_biletow p ON b.pule_biletowid = p.id " +
                "JOIN public.wydarzenia e ON p.wydarzeniaid = e.id " +
                "WHERE e.datawydarzeniakoniec < now() " +
                "AND b.id NOT IN (SELECT biletyid FROM public.bilety_do_odsprzedania)";

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
                    int poolId = resultSet.getInt("pool_id");
                    int eventId = resultSet.getInt("wydarzeniaid");
                    int initialNumberOfTickets = resultSet.getInt("iloscbiletow");
                    int numberOfSoldTickets = resultSet.getInt("liczba_sprzedanych_biletow");
                    float price = resultSet.getFloat("cenabiletu");
                    LocalDate sellStartDate = LocalDate.parse(resultSet.getString("datarozpoczeciasprzedazy"));
                    LocalDate saleEndDate = LocalDate.parse(resultSet.getString("datazakonczeniesprzedazy"));
                    boolean shouldStartWhenPreviousPoolEnd = resultSet.getBoolean("rozpoczeciesprzedazypozakonczeniupoprzedniejpuli");
                    int poolNumber = resultSet.getInt("numerpuli");

                    TicketPool ticketPool = new TicketPool(
                            poolId,
                            initialNumberOfTickets,
                            numberOfSoldTickets,
                            price,
                            sellStartDate,
                            saleEndDate,
                            shouldStartWhenPreviousPoolEnd,
                            poolNumber,
                            eventId
                    );

                    String ticketId = resultSet.getString("id");
                    String location = resultSet.getString("miejsce");
                    String organizer = resultSet.getString("organizator");

                    Ticket ticket = new Ticket(ticketId, ticketPool, sellStartDate, saleEndDate, location, organizer, price, false);
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
        String procedureCall = "INSERT INTO public.opinie (ocena, komentarz, uzytkownicyid, wydarzeniaid) VALUES (?, ?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(procedureCall)) {

            preparedStatement.setInt(1, opinion.opinion);
            preparedStatement.setString(2, opinion.comment);
            preparedStatement.setInt(3, opinion.userId);
            preparedStatement.setInt(4, opinion.eventId);

            preparedStatement.execute();
            System.out.println("Opinia została pomyślnie dodana.");
        } catch (SQLException e) {
            System.err.println("Błąd podczas dodawania opinii: " + e.getMessage());
            e.printStackTrace();
        }
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
    public void AddTicketForResell(TicketToReSell ticketToReSell) {
        String procedureCall = "INSERT INTO public.bilety_do_odsprzedania (biletyid, cena) VALUES (?, ?)";

        try (Connection connection = getConnection();
             CallableStatement callableStatement = connection.prepareCall(procedureCall)) {

            // Set procedure parameters
            UUID ticketUuid = UUID.fromString(ticketToReSell.ticketId); // Konwersja String na UUID
            callableStatement.setObject(1, ticketUuid); // Użycie setObject do ustawienia UUID
            callableStatement.setFloat(2, ticketToReSell.price);

            // Execute the procedure
            callableStatement.execute();
            System.out.println("Bilet dodany do bazy z odsprzedaza.");
        } catch (SQLException e) {
            System.err.println("Error while adding ticket to the reselling market: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Ticket[] GetTicketForSell(int userId) {
        String sql = "SELECT r.biletyid AS ticket_id, " +
                "b.pule_biletowid, " +
                "p.datarozpoczeciasprzedazy, p.datazakonczeniesprzedazy, " +
                "e.miejsce, e.organizator, " +
                "r.cena AS resell_price " +
                "FROM public.bilety_do_odsprzedania r " +
                "JOIN public.bilety b ON r.biletyid = b.id " +
                "JOIN public.pule_biletow p ON b.pule_biletowid = p.id " +
                "JOIN public.wydarzenia e ON p.wydarzeniaid = e.id " +
                "WHERE b.uzytkownicyid != ?";

        List<Ticket> tickets = new ArrayList<>();

        try {
            Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String ticketId = resultSet.getString("ticket_id");
                int poolId = resultSet.getInt("pule_biletowid");
                LocalDate sellStartDate = LocalDate.parse(resultSet.getString("datarozpoczeciasprzedazy"));
                LocalDate saleEndDate = LocalDate.parse(resultSet.getString("datazakonczeniesprzedazy"));
                String location = resultSet.getString("miejsce");
                String organizer = resultSet.getString("organizator");
                float resellPrice = resultSet.getFloat("resell_price");

                var ticketPool = new TicketPool(poolId, 0, 0, 0, sellStartDate, saleEndDate, false, 0, 0);

                // Tworzenie obiektu Ticket
                Ticket ticket = new Ticket(ticketId, ticketPool, sellStartDate, saleEndDate, location, organizer, resellPrice, true);
                tickets.add(ticket);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Zwracanie wyników jako tablica
        return tickets.toArray(new Ticket[0]);
    }

    @Override
    public ArrayList<User> SearchUsersInDataBase(String login) {
        ArrayList<User> users = new ArrayList<>();
        String query = "SELECT id, login, email, haslo FROM uzytkownicy WHERE rola = 0 AND login = ?";

        try (Connection connection = getConnection();
             CallableStatement stmt = connection.prepareCall(query)) {
            // Ustawiamy parametr zapytania (login)
            stmt.setString(1, login);

            // Wykonujemy zapytanie
            ResultSet rs = stmt.executeQuery();

            // Przetwarzamy wyniki
            while (rs.next()) {
                int id = rs.getInt("id");
                String userLogin = rs.getString("login");
                String email = rs.getString("email");

                // Tworzymy obiekt użytkownika i dodajemy go do listy
                User user = new User(id, userLogin, email, UserRole.Client);
                users.add(user);
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Obsługuje wyjątek, np. logowanie błędów
        }

        return users;
    }

    @Override
    public void AddBlockedUsers(ArrayList<User> blockedUsers) {

    }

    @Override
    public EventDetails GetEventDetailsById(int eventId) {
        String sql = "SELECT e.id, e.datawydarzeniastart, e.datawydarzeniakoniec, e.miejsce, e.organizator, e.uzytkownicyid, " +
                "p.id AS pool_id, p.iloscbiletow, p.cenabiletu, p.datarozpoczeciasprzedazy, p.datazakonczeniesprzedazy, " +
                "p.rozpoczeciesprzedazypozakonczeniupoprzedniejpuli, p.numerpuli, " +
                "o.id AS opinion_id, o.ocena, o.komentarz, o.uzytkownicyid AS opinion_user_id, " +
                "u.id AS user_id, u.login, u.email " +
                "FROM public.wydarzenia e " +
                "LEFT JOIN public.pule_biletow p ON e.id = p.wydarzeniaid " +
                "LEFT JOIN public.bilety b ON p.id = b.pule_biletowid " +
                "LEFT JOIN public.uzytkownicy u ON b.uzytkownicyid = u.id " +
                "LEFT JOIN public.opinie o ON e.id = o.wydarzeniaid AND o.uzytkownicyid = u.id " +
                "WHERE e.id = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, eventId);

            try (ResultSet resultSet = statement.executeQuery()) {
                EventDetails eventDetails = new EventDetails();
                List<TicketPool> ticketPools = new ArrayList<>();
                List<Opinion> opinions = new ArrayList<>();
                List<User> users = new ArrayList<>();
                Set<Integer> addedUsers = new HashSet<>(); // Set for preventing duplicate users
                Set<Integer> addedPools = new HashSet<>(); // Set for preventing duplicate pools

                while (resultSet.next()) {
                    if (eventDetails.id == 0) {
                        eventDetails.id = resultSet.getInt("id");
                        eventDetails.sellStartDate = LocalDate.parse(resultSet.getString("datawydarzeniastart"));
                        eventDetails.saleEndDate = LocalDate.parse(resultSet.getString("datawydarzeniakoniec"));
                        eventDetails.location = resultSet.getString("miejsce");
                        eventDetails.organizer = resultSet.getString("organizator");
                        eventDetails.userId = resultSet.getInt("uzytkownicyid");
                    }

                    int poolId = resultSet.getInt("pool_id");
                    if (!resultSet.wasNull() && !addedPools.contains(poolId)) {
                        TicketPool ticketPool = new TicketPool();
                        ticketPool.id = poolId;
                        ticketPool.initialNumberOfTickets = resultSet.getInt("iloscbiletow");
                        ticketPool.price = resultSet.getFloat("cenabiletu");
                        ticketPool.sellStartDate = LocalDate.parse(resultSet.getString("datarozpoczeciasprzedazy"));
                        ticketPool.sellEndDate = LocalDate.parse(resultSet.getString("datazakonczeniesprzedazy"));
                        ticketPool.shouldStartWhenPreviousPoolEnd = resultSet.getBoolean("rozpoczeciesprzedazypozakonczeniupoprzedniejpuli");
                        ticketPool.poolNumber = resultSet.getInt("numerpuli");

                        ticketPools.add(ticketPool);
                        addedPools.add(poolId); // Mark pool as added
                    }

                    int opinionId = resultSet.getInt("opinion_id");
                    if (!resultSet.wasNull()) {
                        Opinion opinion = new Opinion();
                        opinion.id = opinionId;
                        opinion.opinion = resultSet.getInt("ocena");
                        opinion.comment = resultSet.getString("komentarz");
                        opinion.userId = resultSet.getInt("opinion_user_id");
                        opinion.userLogin = resultSet.getString("login");
                        opinion.eventId = eventDetails.id;
                        opinions.add(opinion);
                    }

                    int userId = resultSet.getInt("user_id");
                    if (!resultSet.wasNull() && addedUsers.add(userId)) {
                        User user = new User();
                        user.id = userId;
                        user.login = resultSet.getString("login");
                        user.email = resultSet.getString("email");
                        users.add(user);
                    }
                }

                eventDetails.ticketPools = ticketPools.toArray(new TicketPool[0]);
                eventDetails.opinions = opinions.toArray(new Opinion[0]);
                eventDetails.users = users.toArray(new User[0]);

                for (TicketPool pool : ticketPools) {
                    System.out.println(pool.poolNumber);
                }
                return eventDetails;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}