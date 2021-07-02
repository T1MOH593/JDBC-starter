package com.dmdev.jdbc.starter.dao;

import com.dmdev.jdbc.starter.Entity.Flight;
import com.dmdev.jdbc.starter.exception.DaoException;
import com.dmdev.jdbc.starter.util.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collector;

public class FlightDao implements Dao<Integer, Flight> {

    private static final FlightDao INSTANCE = new FlightDao();

    private static String FIND_BY_ID_SQL = """
            SELECT id, 
                flight_no, 
                departure_date, 
                departure_airport_code, 
                arrival_date, 
                arrival_airport_code, 
                aircraft_id, 
                status
            FROM flight
            WHERE id = ?;
            """;

    public static FlightDao getInstance() {
        return INSTANCE;
    }

    @Override
    public Flight save(Flight ticket) {
        return null;
    }

    @Override
    public void update(Flight ticket) {

    }

    @Override
    public ArrayList<Flight> findAll() {
        return null;
    }

    public Optional<Flight> findById(Integer id, Connection connection) {
        try (var preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            preparedStatement.setInt(1, id);

            var resultSet = preparedStatement.executeQuery();
            Flight flight = null;
            if (resultSet.next()) {
                flight = new Flight(
                        resultSet.getInt("id"),
                        resultSet.getString("flight_no"),
                        resultSet.getTimestamp("departure_date").toLocalDateTime(),
                        resultSet.getString("departure_airport_code"),
                        resultSet.getTimestamp("arrival_date").toLocalDateTime(),
                        resultSet.getString("arrival_airport_code"),
                        resultSet.getInt("aircraft_id"),
                        resultSet.getString("status")
                );
            }
            return Optional.ofNullable(flight);
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    @Override
    public Optional<Flight> findById(Integer id) {
        try (var connection = ConnectionManager.get()) {
            var flight = findById(id, connection);
            return flight;
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }
        @Override
        public boolean delete (Integer id) {
            return false;
        }
    }

