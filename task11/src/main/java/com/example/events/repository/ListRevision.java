package com.example.events.repository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
@Repository
public class ListRevision {
    private final JdbcTemplate jdbc;
    public ListRevision(JdbcTemplate jdbc){ this.jdbc=jdbc; }
    public long current(){ return jdbc.queryForObject("select revision from list_revision where id=1", Long.class); }
    public void bump(){ jdbc.update("update list_revision set revision=revision+1 where id=1"); }
}
