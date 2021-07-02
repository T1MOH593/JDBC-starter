package com.dmdev.jdbc.starter.dao;

import com.dmdev.jdbc.starter.Entity.Ticket;

import java.util.ArrayList;
import java.util.Optional;

public interface Dao<K, E> {
    E save(E ticket);
    void update(E ticket);
    ArrayList<E> findAll();
    Optional<E> findById(K id);
    boolean delete(K id);
}
