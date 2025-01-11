package org.Model;

import java.sql.*;
import java.util.Properties;

public class Facade implements IModel {

    final String connectionString = "jdbc:postgresql://localhost:5432/teat_1";
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
        getConnection();

    }

    @Override
    public User GetUserByCredentials(String login, String password) {
        return null;
    }

    @Override
    public void AddEvent(Event event) {

    }

    @Override
    public Event[] GetEventsById(Integer userId) {
        Connection connection = getConnection();
        var sql = "SELECT * FROM wydarzenia WHERE uzytkownicyid = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                System.out.println(resultSet.getString("organizator"));
            }

            closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
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