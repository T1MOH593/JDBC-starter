package com.dmdev.jdbc.starter;

import com.dmdev.jdbc.starter.util.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TransactionRunner {
    public static void main(String[] args) throws SQLException {
        try {
            deleteIdInFlightTable(8);
        } finally {
            ConnectionManager.closeConnectionPool();
        }
    }
    public static void deleteIdInFlightTable(int id) throws SQLException {
        String deleteFlightIdInTicket = """
                DELETE FROM ticket
                WHERE flight_id = ?;
                """;
        String deleteIdInFlight = """
                DELETE FROM flight
                WHERE id = ?;
                """;
        Connection connection = null;
        PreparedStatement deleteInTicket = null;
        PreparedStatement deleteInFlight = null;

        try {
            connection = ConnectionManager.get();
            deleteInTicket = connection.prepareStatement(deleteFlightIdInTicket);
            deleteInFlight = connection.prepareStatement(deleteIdInFlight);

            connection.setAutoCommit(false);

            deleteInTicket.setInt(1, id);
            deleteInFlight.setInt(1, id);

            deleteInTicket.executeUpdate();
            if(true) {
                throw new RuntimeException("oooops");
            }
            deleteInFlight.executeUpdate();

            connection.commit();
        } catch(Exception e) {
            if(connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            if(deleteInFlight != null){
                deleteInFlight.close();
            }
            if(deleteInTicket != null) {
                deleteInTicket.close();
            }
            if(connection != null) {
                connection.close();
            }
        }

    }
}
