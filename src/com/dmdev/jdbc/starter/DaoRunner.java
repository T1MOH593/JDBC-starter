package com.dmdev.jdbc.starter;

import com.dmdev.jdbc.starter.Entity.Ticket;
import com.dmdev.jdbc.starter.dao.TicketDao;
import com.dmdev.jdbc.starter.dto.TicketFilter;

import java.math.BigDecimal;

public class DaoRunner {
    public static void main(String[] args) {
        var ticket = TicketDao.getInstance().findById(4);
        System.out.println(ticket);
    }

    private static void filterTest() {
        TicketFilter filter = new TicketFilter(3, 0, null, "A1");
        var tickets = TicketDao.getInstance().findAll(filter);
        System.out.println(tickets);
    }

    private static void findByIdTest() {
        System.out.println(TicketDao.getInstance().findById(3));
    }

    private static void findAllTest() {
        var instance = TicketDao.getInstance().findAll();
        for (Ticket ticket : instance) {
            System.out.println(ticket);
        }
    }

    private static void deleteTest() {
        var instance = TicketDao.getInstance();
        instance.delete(57);
    }

    private static void saveTest() {
        var instance = TicketDao.getInstance();
        var ticket = new Ticket();
        ticket.setCost(BigDecimal.TEN);
//        ticket.setFlight(5);
        ticket.setSeatNo("5H");
        ticket.setPassengerNo("54FR");
        ticket.setPassengerName("АНтон Гарабец");
        var savedTicket = instance.save(ticket);
        System.out.println(savedTicket);
    }
}
