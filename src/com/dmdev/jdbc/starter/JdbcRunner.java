package com.dmdev.jdbc.starter;

import com.dmdev.jdbc.starter.util.ConnectionManager;
import org.postgresql.Driver;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JdbcRunner {
    public static void main(String[] args) throws SQLException {
//        String flight_id = "2";
//        var ticketsByFlightId = getTicketsByFlightId(flight_id);
//        System.out.println(ticketsByFlightId);
        try {
            var result = getFlight_idByDateBetween(LocalDate.of(2020, 12, 1).atStartOfDay(),
                    LocalDate.now().atStartOfDay());
            System.out.println(result);
        } finally {
            ConnectionManager.closeConnectionPool();
        }
    }

    public static ArrayList<Integer> getFlight_idByDateBetween(LocalDateTime start, LocalDateTime end) throws SQLException {
        String sql = """
                SELECT id
                FROM flight
                WHERE departure_date BETWEEN ? AND ?;
                """;
        ArrayList<Integer> result = new ArrayList<>();
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(sql)) {
            System.out.println(preparedStatement);
            preparedStatement.setTimestamp(1, Timestamp.valueOf(start));
            System.out.println(preparedStatement);
            preparedStatement.setTimestamp(2, Timestamp.valueOf(end));
            System.out.println(preparedStatement);
            var resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                result.add(resultSet.getObject("id", Integer.class));
            }
        }
        return result;
    }

    public static ArrayList<Integer> getTicketsByFlightId(String flight_id) throws SQLException {
        String sql = """
                SELECT id
                FROM ticket
                WHERE flight_id = %s
                """.formatted(flight_id);
        ArrayList<Integer> result = new ArrayList<>();
        try (var connection = ConnectionManager.get();
            var statement = connection.createStatement()) {
            var resultSet = statement.executeQuery(sql);
            while (resultSet.next()){
                result.add(resultSet.getInt("id"));
            }
        }
        return result;
    }
    }

