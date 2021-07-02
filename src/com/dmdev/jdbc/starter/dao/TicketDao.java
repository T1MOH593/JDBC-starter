package com.dmdev.jdbc.starter.dao;

import com.dmdev.jdbc.starter.Entity.Flight;
import com.dmdev.jdbc.starter.Entity.Ticket;
import com.dmdev.jdbc.starter.dto.TicketFilter;
import com.dmdev.jdbc.starter.exception.DaoException;
import com.dmdev.jdbc.starter.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.joining;

public class TicketDao implements Dao<Integer, Ticket> {

    public static final TicketDao INSTANCE = new TicketDao();

    private final FlightDao flightDao = FlightDao.getInstance();

    private static final String DELETE_SQL = """
                DELETE FROM ticket
                WHERE id = ?;
                """;
    private static final String SAVE_SQL = """
            INSERT INTO ticket (passenger_no, passenger_name, flight_id, seat_no, cost)
            VALUES (?, ?, ?, ?, ?)
            """;

    private static final String FIND_ALL_SQL = """
            SELECT ticket.id,
            passenger_no,
            passenger_name,
            flight_id,
            seat_no,
            cost,
            f.id, 
            f.flight_no, 
            f.departure_date, 
            f.departure_airport_code, 
            f.arrival_date, 
            f.arrival_airport_code, 
            f.aircraft_id, 
            f.status
            FROM ticket
            JOIN flight f
                ON f.id = ticket.flight_id
            """;
    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL +
            "WHERE ticket.id = ?";

    private static final String UPDATE_SQL = """
            UPDATE ticket
            SET passenger_no = ?,
            passenger_name = ?,
            flight_id = ?,
            seat_no = ?,
            cost = ?
            WHERE id = ?;
            """;

    private TicketDao(){
    }

    public static TicketDao getInstance() {
        return INSTANCE;
    }
    public boolean delete(Integer id) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(DELETE_SQL)) {
            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    public Ticket save(Ticket ticket) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, ticket.getPassengerNo());
            preparedStatement.setString(2, ticket.getPassengerName());
            preparedStatement.setInt(3, ticket.getFlight().id());
            preparedStatement.setString(4, ticket.getSeatNo());
            preparedStatement.setBigDecimal(5, ticket.getCost());
            preparedStatement.executeUpdate();
            var generatedKeys = preparedStatement.getGeneratedKeys();
            generatedKeys.next();
            ticket.setId(generatedKeys.getInt("id"));
            return ticket;
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    public void update(Ticket ticket) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
            preparedStatement.setString(1, ticket.getPassengerNo());
            preparedStatement.setString(2, ticket.getPassengerName());
            preparedStatement.setInt(3, ticket.getFlight().id());
            preparedStatement.setString(4, ticket.getSeatNo());
            preparedStatement.setBigDecimal(5, ticket.getCost());
            preparedStatement.setInt(6, ticket.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    public ArrayList<Ticket> findAll() {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {

            var resultSet = preparedStatement.executeQuery();
            ArrayList<Ticket> tickets = new ArrayList<>();
            while (resultSet.next()){
                Ticket ticket = buildTicket(resultSet);
                tickets.add(ticket);
            }
            return tickets;
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    private Ticket buildTicket(ResultSet resultSet) throws SQLException {
        Flight flight = new Flight(
                resultSet.getInt("flight_id"),
                resultSet.getString("flight_no"),
                resultSet.getTimestamp("departure_date").toLocalDateTime(),
                resultSet.getString("departure_airport_code"),
                resultSet.getTimestamp("arrival_date").toLocalDateTime(),
                resultSet.getString("arrival_airport_code"),
                resultSet.getInt("aircraft_id"),
                resultSet.getString("status")
        );
        Ticket ticket = new Ticket(
                resultSet.getInt("id"),
                resultSet.getString("passenger_no"),
                resultSet.getString("passenger_name"),
                flightDao.findById(resultSet.getInt("flight_id")).orElse(null),
                resultSet.getString("seat_no"),
                resultSet.getBigDecimal("cost")
        );
        return ticket;
    }

    public Optional<Ticket> findById(Integer id) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            preparedStatement.setInt(1, id);
            var resultSet = preparedStatement.executeQuery();
            Ticket maybeTicket = null;
            if (resultSet.next()) {
                maybeTicket = buildTicket(resultSet);
            }
            return Optional.ofNullable(maybeTicket);
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    public List<Ticket> findAll(TicketFilter filter) {
        List parameters = new ArrayList();
        List whereSql = new ArrayList();
        if (filter.passengerName() != null) {
            whereSql.add("passenger_name = ?");
            parameters.add(filter.passengerName());
        }
        if (filter.seatNo() != null) {
            whereSql.add("seat_no LIKE ?\n");
            parameters.add("%" + filter.seatNo() + "%");
        }
        parameters.add(filter.limit());
        parameters.add(filter.offset());
        List<Ticket> tickets = new ArrayList<>();
        var where = whereSql.stream().
                collect(joining(" AND ", "WHERE ", "LIMIT ?\nOFFSET ?"));
        if (whereSql.isEmpty()) {
            whereSql.add("");
        }
        var sql = FIND_ALL_SQL + where;
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(sql)) {
            for (int i = 0; i < parameters.size(); i++) {
                preparedStatement.setObject(i + 1, parameters.get(i));
            }
            var resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                var ticket = buildTicket(resultSet);
                tickets.add(ticket);
            }
            return tickets;
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }

    }
}
